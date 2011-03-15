package net.greghaines.jesque.web.dao;

import java.util.List;

import net.greghaines.jesque.JobFailure;

public interface FailureDAO
{
	long getCount();
	
	List<JobFailure> getFailures(int offset, int count);
	
	void clear();
	
	void requeue(int index);
}
