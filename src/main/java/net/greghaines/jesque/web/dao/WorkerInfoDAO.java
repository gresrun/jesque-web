package net.greghaines.jesque.web.dao;

import java.util.List;
import java.util.Map;

import net.greghaines.jesque.web.WorkerInfo;

public interface WorkerInfoDAO
{
	long getWorkerCount();
	
	List<WorkerInfo> getActiveWorkers();
	
	List<WorkerInfo> getAllWorkers();
	
	WorkerInfo getWorker(String workerName);
	
	Map<String,List<WorkerInfo>> getWorkerHostMap();
}
