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
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.SERVICE_UNAVAILABLE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import net.greghaines.jesque.Config;
import net.greghaines.jesque.meta.KeyInfo;
import net.greghaines.jesque.meta.KeyType;
import net.greghaines.jesque.meta.QueueInfo;
import net.greghaines.jesque.meta.WorkerInfo;
import net.greghaines.jesque.meta.dao.FailureDAO;
import net.greghaines.jesque.meta.dao.KeysDAO;
import net.greghaines.jesque.meta.dao.QueueInfoDAO;
import net.greghaines.jesque.meta.dao.WorkerInfoDAO;
import net.greghaines.jesque.utils.JesqueUtils;
import net.greghaines.jesque.utils.ResqueDateFormatThreadLocal;
import net.greghaines.jesque.utils.VersionUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;
import redis.clients.jedis.exceptions.JedisConnectionException;

/** Controller for the Jesque Web application. */
@Controller("jesqueController")
public class JesqueController {

  private static final List<String> tabs = List.of("Overview", "Working", "Failed", "Queues", "Workers", "Stats");
  private static final List<String> statsSubTabs = List.of("resque", "redis", "keys");
  private static final Pattern whitespacePattern = Pattern.compile("\\s+");

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
  public void buildRedisURI() {
    this.redisURI = this.config.getURI();
  }

  @ExceptionHandler
  @ResponseStatus(SERVICE_UNAVAILABLE)
  public ModelAndView connectError(final JedisConnectionException exception) {
    return errorModelAndView("error", exception, SERVICE_UNAVAILABLE);
  }

  @ExceptionHandler
  @ResponseStatus(INTERNAL_SERVER_ERROR)
  public ModelAndView genericError(final Exception exception) {
    return errorModelAndView("error", exception, INTERNAL_SERVER_ERROR);
  }

  private static ModelAndView errorModelAndView(
      final String viewName, final Throwable t, final HttpStatus status) {
    final ModelAndView model = new ModelAndView(viewName);
    model.addObject("errorCode", status.value());
    model.addObject("errorName", toNiceCase(status.name()));
    model.addObject("errorType", t.getClass().getName());
    model.addObject("errorMessage", t.getMessage());
    model.addObject("stackTrace", JesqueUtils.createBacktrace(t).toArray(new String[0]));
    return model;
  }

  private static String toNiceCase(final String orig) {
    final String[] tmpStrs = whitespacePattern.split(orig.replace('_', ' ').trim());
    final StringBuilder sb = new StringBuilder(orig.length());
    String prefix = "";
    for (final String tmpStr : tmpStrs) {
      if (tmpStr.length() > 0) {
        sb.append(prefix)
            .append(tmpStr.substring(0, 1).toUpperCase())
            .append(tmpStr.substring(1).toLowerCase());
      }
      prefix = " ";
    }
    return sb.toString();
  }

  @RequestMapping(value = "/index", method = GET)
  public String index() {
    return "redirect:/overview";
  }

  @RequestMapping(value = "/failed", method = GET)
  public String failed(
      @RequestParam(value = "start", defaultValue = "0") final long offset,
      @RequestParam(value = "count", defaultValue = "20") final long count,
      final Model model) {
    addHeaderAttributes(model, "Failed", null, null);
    model.addAttribute("start", offset);
    model.addAttribute("count", count);
    model.addAttribute("fullFailureCount", this.failureDAO.getCount());
    model.addAttribute("failures", this.failureDAO.getFailures(offset, count));
    return "failed";
  }

  @RequestMapping(value = "/failed/clear", method = POST)
  public String failedClear() {
    this.failureDAO.clear();
    return "redirect:/failed";
  }

  @RequestMapping(value = "/failed/remove/{index}", method = GET)
  public String failedRemove(@PathVariable("index") final long index) {
    this.failureDAO.remove(index);
    return "redirect:/failed";
  }

  @RequestMapping(value = "/failed/requeue/{index}", method = GET)
  public String failedRequeue(@PathVariable("index") final long index) {
    this.failureDAO.requeue(index);
    return "redirect:/failed";
  }

  @RequestMapping(value = "/failed/requeue/{index}", method = GET, headers = "X-Requested-With=XMLHttpRequest")
  public void failedRequeueXHR(
      @PathVariable("index") final long index, final HttpServletResponse resp) throws IOException {
    final Date retriedAt = this.failureDAO.requeue(index);
    final PrintWriter pw = resp.getWriter();
    pw.print(
        (retriedAt == null)
            ? "ERROR"
            : ResqueDateFormatThreadLocal.getInstance().format(retriedAt));
    pw.flush();
    pw.close();
  }

  @RequestMapping(value = "/overview", method = GET)
  public String overview(final Model model) {
    addHeaderAttributes(model, "Overview", null, null);
    addQueuesAttributes(model);
    addWorkingAttributes(model);
    addPollController(model, "overview", false);
    return "overview";
  }

  @RequestMapping(value = "/overview.poll", method = GET)
  public String overviewPoll(final Model model) {
    addQueuesAttributes(model);
    addWorkingAttributes(model);
    addPollController(model, "overview", true);
    return "overview";
  }

  @RequestMapping(value = "/queues", method = GET)
  public String queues(final Model model) {
    addHeaderAttributes(model, "Queues", null, null);
    addQueuesAttributes(model);
    return "queues";
  }

  @RequestMapping(value = "/queues/{queueName}", method = GET)
  public String queues(
      @PathVariable("queueName") final String queueName,
      @RequestParam(value = "start", defaultValue = "0") final long offset,
      @RequestParam(value = "count", defaultValue = "20") final long count,
      final Model model) {
    addHeaderAttributes(model, "Queues", this.queueInfoDAO.getQueueNames(), queueName);
    model.addAttribute("start", offset);
    model.addAttribute("count", count);
    final QueueInfo queueInfo = this.queueInfoDAO.getQueueInfo(queueName, offset, count);
    if (queueInfo == null) {
      model.addAttribute("queueName", queueName);
    } else {
      model.addAttribute("queue", queueInfo);
    }
    return "queues-detail";
  }

  @RequestMapping(value = "/queues/{queueName}/remove", method = POST)
  public String queues(@PathVariable("queueName") final String queueName) {
    this.queueInfoDAO.removeQueue(queueName);
    return "redirect:/queues";
  }

  @RequestMapping(value = "/stats", method = GET)
  public String stats(final Model model) {
    return "redirect:/stats/resque";
  }

  @RequestMapping(value = "/stats/{statType}", method = GET)
  public String stats(@PathVariable("statType") final String statType, final Model model) {
    if ("resque".equals(statType)) {
      addHeaderAttributes(model, "Stats", statsSubTabs, "resque");
      model.addAttribute("title", "Resque Client connected to " + this.redisURI);
      model.addAttribute("stats", createResqueStats());
    } else if ("redis".equals(statType)) {
      addHeaderAttributes(model, "Stats", statsSubTabs, "redis");
      model.addAttribute("title", this.redisURI);
      model.addAttribute("stats", this.keysDAO.getRedisInfo());
    } else if ("keys".equals(statType)) {
      addHeaderAttributes(model, "Stats", statsSubTabs, "keys");
      model.addAttribute("title", "Keys owned by Resque Client connected to " + this.redisURI);
      model.addAttribute(
          "subTitle",
          "(All keys are actually prefixed with \"" + this.config.getNamespace() + COLON + "\")");
      model.addAttribute("keys", this.keysDAO.getKeyInfos());
    }
    return "stats";
  }

  @RequestMapping(value = "/stats.txt", method = GET)
  public void statsTxt(final HttpServletResponse resp) throws IOException {
    final Map<String, Object> resqueStats = createResqueStats();
    final List<QueueInfo> queueInfos = this.queueInfoDAO.getQueueInfos();
    resp.setContentType("text/html");
    final PrintWriter pw = resp.getWriter();
    pw.println("resque.pending=" + resqueStats.get("pending"));
    pw.println("resque.processed=" + resqueStats.get("processed"));
    pw.println("resque.failed=" + resqueStats.get("failed"));
    pw.println("resque.workers=" + resqueStats.get("workers"));
    pw.println("resque.working=" + resqueStats.get("working"));
    for (final QueueInfo queueInfo : queueInfos) {
      pw.printf("queues.%s=%d%n", queueInfo.getName(), queueInfo.getSize());
    }
    pw.flush();
    pw.close();
  }

  private Map<String, Object> createResqueStats() {
    final Map<String, Object> resqueStats = new LinkedHashMap<String, Object>();
    resqueStats.put("environment", "development");
    resqueStats.put("failed", this.failureDAO.getCount());
    resqueStats.put("pending", this.queueInfoDAO.getPendingCount());
    resqueStats.put("processed", this.queueInfoDAO.getProcessedCount());
    resqueStats.put("queues", this.queueInfoDAO.getQueueNames().size());
    resqueStats.put("servers", "[\"" + this.redisURI + "\"]");
    resqueStats.put("workers", this.workerInfoDAO.getWorkerCount());
    resqueStats.put("working", this.workerInfoDAO.getActiveWorkerCount());
    resqueStats.put("paused", this.workerInfoDAO.getPausedWorkerCount());
    return resqueStats;
  }

  @RequestMapping(value = "/stats/keys/{key}", method = GET)
  public String statsKey(
      @PathVariable("key") final String key,
      @RequestParam(value = "start", defaultValue = "0") final int offset,
      @RequestParam(value = "count", defaultValue = "20") final int count,
      final Model model) {
    addHeaderAttributes(model, "Stats", statsSubTabs, "keys");
    model.addAttribute("start", offset);
    model.addAttribute("count", count);
    final KeyInfo keyInfo = this.keysDAO.getKeyInfo(this.config.getNamespace() + COLON + key, offset, count);
    if (keyInfo == null) {
      model.addAttribute("keyName", key);
    } else {
      model.addAttribute("key", keyInfo);
    }
    return (keyInfo == null || KeyType.STRING.equals(keyInfo.getType()))
        ? "key-string"
        : "key-sets";
  }

  @RequestMapping(value = "/workers", method = GET)
  public String workers(final Model model) {
    addHeaderAttributes(model, "Workers", null, null);
    return addWorkersAttributes(model, false);
  }

  @RequestMapping(value = "/workers.poll", method = GET)
  public String workersPoll(final Model model) {
    return addWorkersAttributes(model, true);
  }

  @RequestMapping(value = "/workers/{workerName}", method = GET)
  public String workers(@PathVariable("workerName") final String workerName, final Model model) {
    final WorkerValues wv = addWorkersAttributes(workerName, model, false);
    addHeaderAttributes(model, "Workers", wv.getSubTabs(), wv.getActiveSubTab());
    return wv.getViewName();
  }

  @RequestMapping(value = "/workers/{workerName}.poll", method = GET)
  public String workersPoll(
      @PathVariable("workerName") final String workerName, final Model model) {
    return addWorkersAttributes(workerName, model, true).getViewName();
  }

  @RequestMapping(value = "/working", method = GET)
  public String working(final Model model) {
    addHeaderAttributes(model, "Working", null, null);
    addWorkingAttributes(model);
    return "working";
  }

  @RequestMapping(value = "/working/{workerName}", method = GET)
  public String working(@PathVariable("workerName") final String workerName, final Model model) {
    addHeaderAttributes(model, "Working", null, null);
    model.addAttribute("worker", this.workerInfoDAO.getWorker(workerName));
    return "working-detail";
  }

  private String addWorkersAttributes(final Model model, final boolean poll) {
    final Map<String, List<WorkerInfo>> hostMap = this.workerInfoDAO.getWorkerHostMap();
    final String viewName;
    if (hostMap.size() == 1) {
      model.addAttribute("workers", combineWorkerInfos(hostMap));
      addPollController(model, "workers", poll);
      viewName = "workers";
    } else {
      model.addAttribute("hostMap", hostMap);
      model.addAttribute("totalWorkerCount", totalWorkerInfoCount(hostMap));
      viewName = "workers-hosts";
    }
    return viewName;
  }

  private WorkerValues addWorkersAttributes(
      final String workerName, final Model model, final boolean poll) {
    final WorkerInfo workerInfo = this.workerInfoDAO.getWorker(workerName);
    final Map<String, List<WorkerInfo>> hostMap = this.workerInfoDAO.getWorkerHostMap();
    final String activeSubTab;
    final String viewName;
    if (workerInfo != null) { // Display workers detail
      activeSubTab = workerInfo.getHost();
      viewName = "workers-detail";
      model.addAttribute("worker", workerInfo);
    } else if (!hostMap.containsKey(workerName) && !"all".equalsIgnoreCase(workerName)) {
      // Unknown worker name
      activeSubTab = null;
      viewName = "workers-detail";
    } else { // Display a list of workers
      viewName = "workers";
      addPollController(model, workerName, poll);
      if ("all".equalsIgnoreCase(workerName)) {
        activeSubTab = null;
        model.addAttribute("workers", combineWorkerInfos(hostMap));
      } else {
        activeSubTab = workerName;
        model.addAttribute("workers", hostMap.get(workerName));
      }
    }
    final List<String> subTabs = (hostMap.size() > 1) ? new ArrayList<String>(hostMap.keySet()) : null;
    return new WorkerValues(activeSubTab, viewName, subTabs);
  }

  private void addWorkingAttributes(final Model model) {
    model.addAttribute("totalWorkerCount", this.workerInfoDAO.getWorkerCount());
    model.addAttribute("working", this.workerInfoDAO.getActiveWorkers());
  }

  private void addQueuesAttributes(final Model model) {
    model.addAttribute("queues", this.queueInfoDAO.getQueueInfos());
    model.addAttribute("totalFailureCount", this.failureDAO.getCount());
  }

  private void addHeaderAttributes(
      final Model model,
      final String activeTab,
      final List<String> subTabs,
      final String activeSubTab) {
    model.addAttribute("tabs", tabs);
    model.addAttribute("activeTab", activeTab);
    if (subTabs != null) {
      model.addAttribute("subTabs", subTabs);
      model.addAttribute("activeSubTab", activeSubTab);
    }
    model.addAttribute("namespace", this.config.getNamespace());
    model.addAttribute("redisUri", this.redisURI);
    model.addAttribute("version", VersionUtils.getVersion());
  }

  private static void addPollController(final Model model, final String path, final boolean poll) {
    final StringBuilder sb = new StringBuilder(64);
    sb.append("<p class=\"poll\">");
    if (poll) {
      sb.append("Last Updated: ").append(new SimpleDateFormat("HH:mm:ss").format(new Date()));
    } else {
      sb.append("<a href=\"").append(path).append(".poll\" rel=\"poll\">Live Poll</a>");
    }
    sb.append("</p>");
    model.addAttribute("pollController", sb.toString());
    model.addAttribute("poll", poll);
  }

  private static List<WorkerInfo> combineWorkerInfos(final Map<?, List<WorkerInfo>> hostMap) {
    final List<WorkerInfo> allWorkers = new LinkedList<WorkerInfo>();
    for (final List<WorkerInfo> hostWorkers : hostMap.values()) {
      allWorkers.addAll(hostWorkers);
    }
    return allWorkers;
  }

  private static long totalWorkerInfoCount(final Map<?, List<WorkerInfo>> hostMap) {
    long count = 0;
    for (final List<WorkerInfo> workerInfos : hostMap.values()) {
      count += workerInfos.size();
    }
    return count;
  }

  private static final class WorkerValues {
    private final String activeSubTab;
    private final String viewName;
    private final List<String> subTabs;

    public WorkerValues(
        final String activeSubTab, final String viewName, final List<String> subTabs) {
      this.activeSubTab = activeSubTab;
      this.viewName = viewName;
      this.subTabs = subTabs;
    }

    public String getActiveSubTab() {
      return this.activeSubTab;
    }

    public String getViewName() {
      return this.viewName;
    }

    public List<String> getSubTabs() {
      return this.subTabs;
    }
  }
}
