package net.greghaines.jesque.web.utils;

import static net.greghaines.jesque.utils.ResqueConstants.DATE_FORMAT;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class RedisDateFormatThreadLocal extends ThreadLocal<DateFormat>
{
	private static volatile RedisDateFormatThreadLocal instance = null;
	private static final Object instanceLock = new Object();
	
	public static RedisDateFormatThreadLocal getInstance()
	{
		if (instance == null)
		{
			synchronized (instanceLock)
			{
				if (instance == null)
				{
					instance = new RedisDateFormatThreadLocal();
				}
			}
		}
		return instance;
	}
	
	private RedisDateFormatThreadLocal(){}
	
	protected DateFormat initialValue()
	{
		return new SimpleDateFormat(DATE_FORMAT);
	}
}
