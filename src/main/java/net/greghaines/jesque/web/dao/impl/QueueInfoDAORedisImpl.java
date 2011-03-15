package net.greghaines.jesque.web.dao.impl;

import static net.greghaines.jesque.utils.ResqueConstants.QUEUE;
import static net.greghaines.jesque.utils.ResqueConstants.QUEUES;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import net.greghaines.jesque.Config;
import net.greghaines.jesque.Job;
import net.greghaines.jesque.json.ObjectMapperFactory;
import net.greghaines.jesque.utils.JesqueUtils;
import net.greghaines.jesque.web.QueueInfo;
import net.greghaines.jesque.web.dao.QueueInfoDAO;
import net.greghaines.jesque.web.utils.PoolUtils;
import net.greghaines.jesque.web.utils.PoolUtils.PoolWork;

import redis.clients.jedis.Jedis;
import redis.clients.util.Pool;

public class QueueInfoDAORedisImpl implements QueueInfoDAO
{
	private final Config config;
	private final Pool<Jedis> jedisPool;
	
	public QueueInfoDAORedisImpl(final Config config, final Pool<Jedis> jedisPool)
	{
		if (config == null)
		{
			throw new IllegalArgumentException("config must not be null");
		}
		if (jedisPool == null)
		{
			throw new IllegalArgumentException("jedisPool must not be null");
		}
		this.config = config;
		this.jedisPool = jedisPool;
	}

	public Set<String> getQueueNames()
	{
		return PoolUtils.doWorkInPoolNicely(this.jedisPool, new PoolWork<Jedis,Set<String>>()
		{
			public Set<String> doWork(final Jedis jedis)
			throws Exception
			{
				return jedis.smembers(key(QUEUES));
			}
		});
	}

	public List<QueueInfo> getQueueInfos()
	{
		final Set<String> queueNames = getQueueNames();
		return PoolUtils.doWorkInPoolNicely(this.jedisPool, new PoolWork<Jedis,List<QueueInfo>>()
		{
			public List<QueueInfo> doWork(final Jedis jedis)
			throws Exception
			{
				final List<QueueInfo> queueInfos = new ArrayList<QueueInfo>(queueNames.size());
				for (final String queueName : queueNames)
				{
					final QueueInfo queueInfo = new QueueInfo();
					queueInfo.setName(queueName);
					queueInfo.setSize(jedis.llen(key(QUEUE, queueName)));
					queueInfos.add(queueInfo);
				}
				Collections.sort(queueInfos);
				return queueInfos;
			}
		});
	}

	public QueueInfo getQueueInfo(final String name, final int jobOffset, final int jobCount)
	{
		return PoolUtils.doWorkInPoolNicely(this.jedisPool, new PoolWork<Jedis,QueueInfo>()
		{
			public QueueInfo doWork(final Jedis jedis)
			throws Exception
			{
				final QueueInfo queueInfo = new QueueInfo();
				queueInfo.setName(name);
				final List<String> payloads = jedis.lrange(key(QUEUE, name), jobOffset, jobOffset + jobCount - 1);
				final List<Job> jobs = new ArrayList<Job>(payloads.size());
				for (final String payload : payloads)
				{
					jobs.add(ObjectMapperFactory.get().readValue(payload, Job.class));
				}
				queueInfo.setJobs(jobs);
				return queueInfo;
			}
		});
	}
	
	/**
	 * Builds a namespaced Redis key with the given arguments.
	 * 
	 * @param parts the key parts to be joined
	 * @return an assembled String key
	 */
	private String key(final String... parts)
	{
		return JesqueUtils.createKey(this.config.getNamespace(), parts);
	}
}
