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

import static net.greghaines.jesque.utils.ResqueConstants.COLON;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import net.greghaines.jesque.Config;
import net.greghaines.jesque.utils.VersionUtils;
import net.greghaines.jesque.web.KeyInfo;
import net.greghaines.jesque.web.KeyType;
import net.greghaines.jesque.web.QueueInfo;
import net.greghaines.jesque.web.WorkerInfo;
import net.greghaines.jesque.web.dao.FailureDAO;
import net.greghaines.jesque.web.dao.KeysDAO;
import net.greghaines.jesque.web.dao.QueueInfoDAO;
import net.greghaines.jesque.web.dao.WorkerInfoDAO;
import net.greghaines.jesque.web.utils.RedisDateFormatThreadLocal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller("jesqueController")
public class JesqueController
{
	private static final List<String> tabs = Arrays.asList("Overview", "Working", "Failed", "Queues", "Workers", "Stats");
	private static final List<String> statsSubTabs = Arrays.asList("resque", "redis", "keys");
	private static final int count = 20;
	
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
	private String redisURI;
	
	@PostConstruct
	public void buildRedisURI()
	{
		this.redisURI = "redis://" + this.config.getHost() + ":" + this.config.getPort() + "/" + this.config.getDatabase();
	}
	
	@RequestMapping(value = "/failed", method = RequestMethod.GET)
	public String failed(@RequestParam(value="start", defaultValue="0") final int offset, final ModelMap modelMap)
	{
		addHeaderAttributes(modelMap, "Failed", null, null);
		modelMap.addAttribute("start", offset);
		modelMap.addAttribute("count", count);
		modelMap.addAttribute("fullFailCount", this.failureDAO.getCount());
		modelMap.addAttribute("failures", this.failureDAO.getFailures(offset, count));
		return "failed";
	}
	
	@RequestMapping(value = "/failed/clear", method = RequestMethod.POST)
	public String failedClear()
	{
		this.failureDAO.clear();
		return "redirect:/failed";
	}
	
	@RequestMapping(value = "/failed/requeue/{index}", method = RequestMethod.GET)
	public String failedRequeue(@PathVariable("index") final int index)
	{
		this.failureDAO.requeue(index);
		return "redirect:/failed";
	}
	
	@RequestMapping(value = "/failed/requeue/{index}", method = RequestMethod.GET, headers = "X-Requested-With=XMLHttpRequest")
	public void failedRequeueXHR(@PathVariable("index") final int index, final HttpServletResponse resp)
	throws IOException
	{
		final Date retriedAt = this.failureDAO.requeue(index);
		final PrintWriter pw = resp.getWriter();
		pw.print((retriedAt == null) ? "ERROR" : RedisDateFormatThreadLocal.getInstance().get().format(retriedAt));
		pw.flush();
		pw.close();
	}
	
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
	
	@RequestMapping(value = "/queues/{queueName}", method = RequestMethod.GET)
	public String queues(@PathVariable("queueName") final String queueName, 
			@RequestParam(value="start", defaultValue="0") final int offset, 
			final ModelMap modelMap)
	{
		addHeaderAttributes(modelMap, "Queues", this.queueInfoDAO.getQueueNames(), queueName);
		modelMap.addAttribute("start", offset);
		modelMap.addAttribute("count", count);
		final QueueInfo queueInfo = this.queueInfoDAO.getQueueInfo(queueName, offset, count);
		if (queueInfo == null)
		{
			modelMap.addAttribute("queueName", queueName);
		}
		else
		{
			modelMap.addAttribute("queue", queueInfo);
		}
		return "queues-detail";
	}
	
	@RequestMapping(value = "/queues/{queueName}/remove", method = RequestMethod.POST)
	public String queues(@PathVariable("queueName") final String queueName)
	{
		this.queueInfoDAO.removeQueue(queueName);
		return "redirect:/queues";
	}
	
	@RequestMapping(value = "/stats", method = RequestMethod.GET)
	public String stats(final ModelMap modelMap)
	{
		return "redirect:/stats/resque";
	}
	
	@RequestMapping(value = "/stats/{statType}", method = RequestMethod.GET)
	public String stats(@PathVariable("statType") final String statType, final ModelMap modelMap)
	{
		if ("resque".equals(statType))
		{
			addHeaderAttributes(modelMap, "Stats", statsSubTabs, "resque");
			modelMap.addAttribute("title", "Resque Client connected to " + this.redisURI);
			modelMap.addAttribute("stats", createResqueStats());
		}
		else if ("redis".equals(statType))
		{
			addHeaderAttributes(modelMap, "Stats", statsSubTabs, "redis");
			modelMap.addAttribute("title", this.redisURI);
			modelMap.addAttribute("stats", this.keysDAO.getRedisInfo());
		}
		else if ("keys".equals(statType))
		{
			addHeaderAttributes(modelMap, "Stats", statsSubTabs, "keys");
			modelMap.addAttribute("title", "Keys owned by Resque Client connected to " + this.redisURI);
			modelMap.addAttribute("subTitle", "(All keys are actually prefixed with \"" + this.config.getNamespace() + COLON + "\")");
			modelMap.addAttribute("keys", this.keysDAO.getKeyInfos());
		}
		return "stats";
	}
	
	@RequestMapping(value = "/stats.txt", method = RequestMethod.GET)
	public void statsTxt(final HttpServletResponse resp)
	throws IOException
	{
		final Map<String,Object> resqueStats = createResqueStats();
		final List<QueueInfo> queueInfos = this.queueInfoDAO.getQueueInfos();
		resp.setContentType("text/html");
		final PrintWriter pw = resp.getWriter();
		pw.println("resque.pending=" + resqueStats.get("pending"));
		pw.println("resque.processed=" + resqueStats.get("processed"));
		pw.println("resque.failed=" + resqueStats.get("failed"));
		pw.println("resque.workers=" + resqueStats.get("workers"));
		pw.println("resque.working=" + resqueStats.get("working"));
		for (final QueueInfo queueInfo : queueInfos)
		{
			pw.printf("queues.%s=%d%n", queueInfo.getName(), queueInfo.getSize());
		}
		pw.flush();
		pw.close();
	}
	
	private Map<String,Object> createResqueStats()
	{
		final Map<String,Object> resqueStats = new LinkedHashMap<String,Object>();
		resqueStats.put("environment", "development");
		resqueStats.put("failed", this.failureDAO.getCount());
		resqueStats.put("pending", this.queueInfoDAO.getPendingCount());
		resqueStats.put("processed", this.queueInfoDAO.getProcessedCount());
		resqueStats.put("queues", this.queueInfoDAO.getQueueNames().size());
		resqueStats.put("servers", "[\"" + this.redisURI + "\"]");
		resqueStats.put("workers", this.workerInfoDAO.getWorkerCount());
		resqueStats.put("working", this.workerInfoDAO.getActiveWorkerCount());
		return resqueStats;
	}

	@RequestMapping(value = "/stats/keys/{key}", method = RequestMethod.GET)
	public String statsKey(@PathVariable("key") final String key, 
			@RequestParam(value="start", defaultValue="0") final int offset, 
			final ModelMap modelMap)
	{
		addHeaderAttributes(modelMap, "Stats", statsSubTabs, "keys");
		modelMap.addAttribute("start", offset);
		modelMap.addAttribute("count", count);
		final KeyInfo keyInfo = this.keysDAO.getKeyInfo(this.config.getNamespace() + COLON + key, offset, count);
		if (keyInfo == null)
		{
			modelMap.addAttribute("keyName", key);
		}
		else
		{
			modelMap.addAttribute("key", keyInfo);
		}
		return (keyInfo == null || KeyType.STRING.equals(keyInfo.getType())) ? "key-string" : "key-sets";
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
		modelMap.addAttribute("redisUri", this.redisURI);
		modelMap.addAttribute("version", VersionUtils.getVersion());
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
