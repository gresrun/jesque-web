package net.greghaines.jesque.web;

import java.io.Serializable;
import java.util.List;

public class KeyInfo implements Comparable<KeyInfo>, Serializable
{
	private static final long serialVersionUID = 6243902746964006352L;
	
	private String name;
	private KeyType type;
	private Long size;
	private List<String> arrayValue;
	
	public KeyInfo(){}
	
	public KeyInfo(final String name, final KeyType type)
	{
		this.name = name;
		this.type = type;
	}

	public String getName()
	{
		return this.name;
	}

	public void setName(final String name)
	{
		this.name = name;
	}

	public KeyType getType()
	{
		return this.type;
	}

	public void setType(final KeyType type)
	{
		this.type = type;
	}

	public Long getSize()
	{
		return this.size;
	}

	public void setSize(final Long size)
	{
		this.size = size;
	}

	public List<String> getArrayValue()
	{
		return this.arrayValue;
	}

	public void setArrayValue(final List<String> arrayValue)
	{
		this.arrayValue = arrayValue;
	}
	
	@Override
	public String toString()
	{
		return this.name;
	}

	public int compareTo(final KeyInfo other)
	{
		int retVal = 1;
		if (other != null)
		{
			if (this.name != null && other.name != null)
			{
				retVal = this.name.compareTo(other.name);
			}
			else if (this.name == null && other.name == null)
			{
				retVal = 0;
			}
			else if (this.name == null)
			{
				retVal = -1;
			}
		}
		return retVal;
	}
}
