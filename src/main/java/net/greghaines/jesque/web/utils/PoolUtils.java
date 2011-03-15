/*
 * Copyright 2011 Greg Haines
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *    http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
