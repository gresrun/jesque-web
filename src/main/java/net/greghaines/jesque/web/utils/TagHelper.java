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

import static net.greghaines.jesque.utils.JesqueUtils.createBacktrace;
import static net.greghaines.jesque.utils.JesqueUtils.join;
import static net.greghaines.jesque.utils.ResqueConstants.COLON;

import java.io.IOException;
import java.util.Date;
import java.util.regex.Pattern;

import net.greghaines.jesque.json.ObjectMapperFactory;

public final class TagHelper
{
	private static Pattern colonPattern = Pattern.compile(COLON);
	
	public static String formatDate(final Date date)
	{
		return RedisDateFormatThreadLocal.getInstance().get().format(date);
	}
	
	public static String toJson(final Object obj)
	throws IOException
	{
		return ObjectMapperFactory.get().writeValueAsString(obj);
	}
	
	public static String asBacktrace(final Throwable t)
	{
		return join("\n", createBacktrace(t));
	}
	
	public static String workerShortName(final String workerName)
	{
		final String shortName;
		if (workerName == null)
		{
			shortName = null;
		}
		else
		{
			final String[] nameParts = colonPattern.split(workerName);
			shortName = nameParts[0] + COLON + nameParts[1];
		}
		return shortName;
	}

	private TagHelper(){}
}
