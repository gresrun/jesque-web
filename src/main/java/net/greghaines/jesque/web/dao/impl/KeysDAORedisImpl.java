package net.greghaines.jesque.web.dao.impl;

import java.util.ArrayList;
import java.util.List;

import redis.clients.jedis.Jedis;
import redis.clients.util.Pool;

import net.greghaines.jesque.web.KeyInfo;
import net.greghaines.jesque.web.KeyType;
import net.greghaines.jesque.web.dao.KeysDAO;
import net.greghaines.jesque.web.utils.PoolUtils;
import net.greghaines.jesque.web.utils.PoolUtils.PoolWork;

public class KeysDAORedisImpl implements KeysDAO
{
	private final Pool<Jedis> jedisPool;

	public KeysDAORedisImpl(final Pool<Jedis> jedisPool)
	{
		if (jedisPool == null)
		{
			throw new IllegalArgumentException("jedisPool must not be null");
		}
		this.jedisPool = jedisPool;
	}
	
	public KeyInfo getKeyInfo(final String key)
	{
		return PoolUtils.doWorkInPoolNicely(this.jedisPool, new KeyDAOPoolWork(key));
	}
	
	public KeyInfo getKeyInfo(final String key, final int offset, final int count)
	{
		return PoolUtils.doWorkInPoolNicely(this.jedisPool, new KeyDAOPoolWork(key, offset, count));
	}
	
	private static final class KeyDAOPoolWork implements PoolWork<Jedis,KeyInfo>
	{
		private final String key;
		private final int offset;
		private final int count;
		private final boolean doArrayValue;
		
		private KeyDAOPoolWork(final String key)
		{
			this.key = key;
			this.offset = -1;
			this.count = -1;
			this.doArrayValue = false;
		}

		private KeyDAOPoolWork(final String key, final int offset, final int count)
		{
			this.key = key;
			this.offset = offset;
			this.count = count;
			this.doArrayValue = true;
		}

		public KeyInfo doWork(final Jedis jedis)
		throws Exception
		{
			final KeyInfo keyInfo;
			final KeyType keyType = KeyType.getKeyTypeByValue(jedis.type(this.key));
			switch (keyType)
			{
			case HASH:
				keyInfo = new KeyInfo(this.key, keyType);
				keyInfo.setSize(jedis.hlen(this.key));
				if (this.doArrayValue)
				{
					final List<String> allFields = new ArrayList<String>(jedis.hkeys(this.key));
					if (this.offset >= allFields.size())
					{
						keyInfo.setArrayValue(new ArrayList<String>(1));
					}
					else
					{
						final int toIndex = (this.offset + this.count > allFields.size()) ? allFields.size() : (this.offset + this.count);
						final List<String> subFields = allFields.subList(this.offset, toIndex);
						final List<String> values = jedis.hmget(this.key, subFields.toArray(new String[subFields.size()]));
						final List<String> arrayValue = new ArrayList<String>(subFields.size());
						for (int i = 0; i < subFields.size(); i++)
						{
							arrayValue.add("{" + subFields.get(i) + "=" + values.get(i) + "}");
						}
						keyInfo.setArrayValue(arrayValue);
					}
				}
				break;
			case LIST:
				keyInfo = new KeyInfo(this.key, keyType);
				keyInfo.setSize(jedis.llen(this.key));
				if (this.doArrayValue)
				{
					keyInfo.setArrayValue(jedis.lrange(this.key, this.offset, this.offset + this.count));
				}
				break;
			case SET:
				keyInfo = new KeyInfo(this.key, keyType);
				keyInfo.setSize(jedis.scard(this.key));
				if (this.doArrayValue)
				{
					final List<String> allMembers = new ArrayList<String>(jedis.smembers(this.key));
					if (this.offset >= allMembers.size())
					{
						keyInfo.setArrayValue(new ArrayList<String>(1));
					}
					else
					{
						final int toIndex = (this.offset + this.count > allMembers.size()) ? allMembers.size() : (this.offset + this.count);
						keyInfo.setArrayValue(new ArrayList<String>(allMembers.subList(this.offset, toIndex)));
					}
				}
				break;
			case STRING:
				keyInfo = new KeyInfo(this.key, keyType);
				keyInfo.setSize(jedis.strlen(this.key));
				if (this.doArrayValue)
				{
					final List<String> arrayValue = new ArrayList<String>(1);
					arrayValue.add(jedis.get(this.key));
					keyInfo.setArrayValue(arrayValue);
				}
				break;
			case ZSET:
				keyInfo = new KeyInfo(this.key, keyType);
				keyInfo.setSize(jedis.zcard(this.key));
				if (this.doArrayValue)
				{
					keyInfo.setArrayValue(new ArrayList<String>(jedis.zrange(this.key, this.offset, this.offset + this.count)));
				}
				break;
			default:
				keyInfo = null;
				break;
			}
			return keyInfo;
		}
	}
}
