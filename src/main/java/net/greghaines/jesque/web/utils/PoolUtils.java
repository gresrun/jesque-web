package net.greghaines.jesque.web.utils;

import org.apache.commons.pool.impl.GenericObjectPool;

import redis.clients.util.Pool;

public final class PoolUtils
{
	public static <T,V> V doWorkInPool(final Pool<T> pool, final PoolWork<T,V> work)
	throws Exception
	{
		if (pool == null)
		{
			throw new IllegalArgumentException("pool must not be null");
		}
		if (work == null)
		{
			throw new IllegalArgumentException("work must not be null");
		}
		final V result;
		final T poolResource = pool.getResource();
		try
		{
			result = work.doWork(poolResource);
		}
		finally
		{
			pool.returnResource(poolResource);
		}
		return result;
	}
	
	public static <T,V> V doWorkInPoolNicely(final Pool<T> pool, final PoolWork<T,V> work)
	{
		final V result;
		try
		{
			result = doWorkInPool(pool, work);
		}
		catch (RuntimeException re)
		{
			throw re;
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
		return result;
	}
	
	public static GenericObjectPool.Config getDefaultPoolConfig()
	{
		final GenericObjectPool.Config cfg = new GenericObjectPool.Config();
		cfg.minIdle = 1;
		cfg.maxIdle = 10;
		cfg.testOnBorrow = true;
		return cfg;
	}
	
	public interface PoolWork<T,V>
	{
		V doWork(T poolResource) throws Exception;
	}
	
	private PoolUtils(){}
}
