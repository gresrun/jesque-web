package net.greghaines.jesque.web;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public enum KeyType
{
	HASH("hash"),
	LIST("list"),
	NONE("none"),
	SET("set"),
	STRING("string"),
	ZSET("zset");
	
	private final String val;
	
	private KeyType(final String val)
	{
		this.val = val;
	}
	
	@Override
	public String toString()
	{
		return this.val;
	}
	
	private static final Map<String,KeyType> valTypeMap;
	
	static
	{
		final Map<String,KeyType> vtm = new HashMap<String,KeyType>();
		for (final KeyType keyType : KeyType.values())
		{
			vtm.put(keyType.toString(), keyType);
		}
		valTypeMap = Collections.unmodifiableMap(vtm);
	}
	
	public static KeyType getKeyTypeByValue(final String val)
	{
		return valTypeMap.get(val);
	}
}
