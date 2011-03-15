package net.greghaines.jesque.web.dao;

import java.util.List;
import java.util.Set;

import net.greghaines.jesque.web.QueueInfo;

public interface QueueInfoDAO
{
	Set<String> getQueueNames();
	
	List<QueueInfo> getQueueInfos();
	
	QueueInfo getQueueInfo(String name, int jobOffset, int jobCount);
}
