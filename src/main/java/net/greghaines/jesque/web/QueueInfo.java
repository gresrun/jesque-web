package net.greghaines.jesque.web;

import java.io.Serializable;
import java.util.List;

import net.greghaines.jesque.Job;

public class QueueInfo implements Comparable<QueueInfo>, Serializable
{
	private static final long serialVersionUID = 562750483276247591L;
	
	private String name;
	private Long size;
	private List<Job> jobs;
	
	public QueueInfo(){}
	
	public String getName()
	{
		return this.name;
	}

	public void setName(final String name)
	{
		this.name = name;
	}

	public Long getSize()
	{
		return this.size;
	}

	public void setSize(final Long size)
	{
		this.size = size;
	}

	public List<Job> getJobs()
	{
		return this.jobs;
	}

	public void setJobs(final List<Job> jobs)
	{
		this.jobs = jobs;
	}

	@Override
	public String toString()
	{
		return this.name;
	}

	public int compareTo(final QueueInfo other)
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
