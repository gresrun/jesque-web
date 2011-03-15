package net.greghaines.jesque.web.utils;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import net.greghaines.jesque.json.ObjectMapperFactory;

public final class TagHelper
{
	public static String formatDate(final Date date, final String format)
	{
		return new SimpleDateFormat(format).format(date);
	}
	
	public static String toJson(final Object obj)
	throws IOException
	{
		return ObjectMapperFactory.get().writeValueAsString(obj);
	}

	private TagHelper(){}
}
