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
package net.greghaines.jesque.web.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import net.greghaines.jesque.Config;
import net.greghaines.jesque.utils.VersionUtils;
import net.greghaines.jesque.web.WorkerInfo;
import net.greghaines.jesque.web.dao.FailureDAO;
import net.greghaines.jesque.web.dao.KeysDAO;
import net.greghaines.jesque.web.dao.QueueInfoDAO;
import net.greghaines.jesque.web.dao.WorkerInfoDAO;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller("jesqueController")
public class JesqueController
{
	private static final List<String> tabs = Arrays.asList("Overview", "Working", "Failed", "Queues", "Workers", "Stats");
	private static final List<String> statsSubTabs = Arrays.asList("resque", "redis", "keys");
	
	@Resource
	private Config config;
	@Resource
	private FailureDAO failureDAO;
	@Resource
	private KeysDAO keysDAO;
	@Resource
	private QueueInfoDAO queueInfoDAO;
	@Resource
	private WorkerInfoDAO workerInfoDAO;
	
	@RequestMapping(value = "/overview", method = RequestMethod.GET)
	public String overview(final ModelMap modelMap)
	{
		addHeaderAttributes(modelMap, "Overview", null, null);
		addQueuesAttributes(modelMap);
		addWorkingAttributes(modelMap);
		addPollController(modelMap, "overview", false);
		return "overview";
	}
	
	@RequestMapping(value = "/overview.poll", method = RequestMethod.GET)
	public String overviewPoll(final ModelMap modelMap)
	{
		addQueuesAttributes(modelMap);
		addWorkingAttributes(modelMap);
		addPollController(modelMap, "overview", true);
		return "overview";
	}
	
	@RequestMapping(value = "/queues", method = RequestMethod.GET)
	public String queues(final ModelMap modelMap)
	{
		addHeaderAttributes(modelMap, "Queues", null, null);
		addQueuesAttributes(modelMap);
		return "queues";
	}
	
	@RequestMapping(value = "/stats", method = RequestMethod.GET)
	public String stats(final ModelMap modelMap)
	{
		return "redirect:/stats/resque";
	}
	
	@RequestMapping(value = "/stats/{statType}", method = RequestMethod.GET)
	public String stats(@PathVariable("statType") final String statType, final ModelMap modelMap)
	{
		return "stats";
	}
	
	@RequestMapping(value = "/stats/key/{keyName}", method = RequestMethod.GET)
	public String statsKey(@PathVariable("keyName") final String statType, final ModelMap modelMap)
	{
		final String viewName = null;
		return viewName;
	}
	
	@RequestMapping(value = "/workers", method = RequestMethod.GET)
	public String workers(final ModelMap modelMap)
	{
		final String viewName = addWorkersAttributes(modelMap, false);
		addHeaderAttributes(modelMap, "Workers", null, null);
		return viewName;
	}
	
	@RequestMapping(value = "/workers.poll", method = RequestMethod.GET)
	public String workersPoll(final ModelMap modelMap)
	{
		return addWorkersAttributes(modelMap, true);
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/workers/{workerName}", method = RequestMethod.GET)
	public String workers(@PathVariable("workerName") final String workerName, final ModelMap modelMap)
	{
		final Object[] retVal = addWorkersAttributes(workerName, modelMap, false);
		final String activeSubTab = (String) retVal[0];
		final String viewName = (String) retVal[1];
		final List<String> subTabs = (List<String>) retVal[2];
		addHeaderAttributes(modelMap, "Workers", subTabs, activeSubTab);
		return viewName;
	}
	
	@RequestMapping(value = "/workers/{workerName}.poll", method = RequestMethod.GET)
	public String workersPoll(@PathVariable("workerName") final String workerName, final ModelMap modelMap)
	{
		final Object[] retVal = addWorkersAttributes(workerName, modelMap, true);
		final String viewName = (String) retVal[1];
		return viewName;
	}
	
	@RequestMapping(value = "/working", method = RequestMethod.GET)
	public String working(final ModelMap modelMap)
	{
		addHeaderAttributes(modelMap, "Working", null, null);
		addWorkingAttributes(modelMap);
		return "working";
	}
	
	@RequestMapping(value = "/working/{workerName}", method = RequestMethod.GET)
	public String working(@PathVariable("workerName") final String workerName, final ModelMap modelMap)
	{
		addHeaderAttributes(modelMap, "Working", null, null);
		modelMap.addAttribute("worker", this.workerInfoDAO.getWorker(workerName));
		return "working-detail";
	}
	
	private String addWorkersAttributes(final ModelMap modelMap, final boolean poll)
	{
		final Map<String,List<WorkerInfo>> hostMap = this.workerInfoDAO.getWorkerHostMap();
		final String viewName;
		if (hostMap.size() == 1)
		{
			modelMap.addAttribute("workers", combineWorkerInfos(hostMap));
			addPollController(modelMap, "workers", poll);
			viewName = "workers";
		}
		else
		{
			modelMap.addAttribute("hostMap", hostMap);
			modelMap.addAttribute("totalWorkerCount", totalWorkerInfoCount(hostMap));
			viewName = "workers-hosts";
		}
		return viewName;
	}
	
	private Object[] addWorkersAttributes(final String workerName, final ModelMap modelMap, final boolean poll)
	{
		final WorkerInfo workerInfo = this.workerInfoDAO.getWorker(workerName);
		final Map<String,List<WorkerInfo>> hostMap = this.workerInfoDAO.getWorkerHostMap();
		final String activeSubTab;
		final String viewName;
		if (workerInfo != null)
		{ // Display workers detail
			activeSubTab = workerInfo.getHost();
			viewName = "workers-detail";
			modelMap.addAttribute("worker", workerInfo);
		}
		else if (!hostMap.containsKey(workerName) && !"all".equalsIgnoreCase(workerName))
		{ // Unknown worker name
			activeSubTab = null;
			viewName = "workers-detail";
		}
		else
		{ // Display a list of workers
			viewName = "workers";
			addPollController(modelMap, "workers/" + workerName, poll);
			if ("all".equalsIgnoreCase(workerName))
			{
				activeSubTab = null;
				modelMap.addAttribute("workers", combineWorkerInfos(hostMap));
			}
			else
			{
				activeSubTab = workerName;
				modelMap.addAttribute("workers", hostMap.get(workerName));
			}
		}
		List<String> subTabs = null;
		if (hostMap.size() > 1)
		{
			subTabs = new ArrayList<String>(hostMap.keySet());
		}
		return new Object[]{activeSubTab, viewName, subTabs};
	}

	private void addWorkingAttributes(final ModelMap modelMap)
	{
		modelMap.addAttribute("totalWorkerCount", this.workerInfoDAO.getWorkerCount());
		modelMap.addAttribute("workers", this.workerInfoDAO.getActiveWorkers());
	}

	private void addQueuesAttributes(final ModelMap modelMap)
	{
		modelMap.addAttribute("queues", this.queueInfoDAO.getQueueInfos());
		modelMap.addAttribute("failureCount", this.failureDAO.getCount());
	}

	private void addHeaderAttributes(final ModelMap modelMap, final String activeTab, 
			final List<String> subTabs, final String activeSubTab)
	{
		modelMap.addAttribute("tabs", tabs);
		modelMap.addAttribute("activeTab", activeTab);
		if (subTabs != null)
		{
			modelMap.addAttribute("subTabs", subTabs);
			modelMap.addAttribute("activeSubTab", activeSubTab);
		}
		modelMap.addAttribute("namespace", this.config.getNamespace());
		modelMap.addAttribute("redisUri", buildRedisURI());
		modelMap.addAttribute("version", VersionUtils.getVersion());
	}

	private String buildRedisURI()
	{
		final StringBuilder sb = new StringBuilder(64);
		sb.append("redis://").append(this.config.getHost()).append(':').append(this.config.getPort())
			.append('/').append(this.config.getDatabase());
		return sb.toString();
	}

	private static void addPollController(final ModelMap modelMap, final String path, final boolean poll)
	{
		modelMap.addAttribute("poll", poll);
		final StringBuilder sb = new StringBuilder(64);
		sb.append("<p class=\"poll\">");
		if (poll)
		{
			sb.append("Last Updated: ").append(new SimpleDateFormat("HH:mm:ss").format(new Date()));
		}
		else
		{
			sb.append("<a href=\"").append(path).append(".poll\" rel=\"poll\">Live Poll</a>");
		}
		sb.append("</p>");
		modelMap.addAttribute("pollController", sb.toString());
	}
	
	private static List<WorkerInfo> combineWorkerInfos(final Map<?,List<WorkerInfo>> hostMap)
	{
		final List<WorkerInfo> allWorkers = new LinkedList<WorkerInfo>();
		for (final List<WorkerInfo> hostWorkers : hostMap.values())
		{
			allWorkers.addAll(hostWorkers);
		}
		return allWorkers;
	}
	
	private static long totalWorkerInfoCount(final Map<?,List<WorkerInfo>> hostMap)
	{
		long count = 0;
		for (final List<WorkerInfo> workerInfos : hostMap.values())
		{
			count += workerInfos.size();
		}
		return count;
	}
}
