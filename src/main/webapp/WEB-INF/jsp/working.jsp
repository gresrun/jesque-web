<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="false" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %> 
<%@ taglib uri="http://greghaines.net/jesque/web/tags/jsq" prefix="jsq" %> 
<c:if test="${not poll and empty param.partial}"><jsp:include page="header.jsp" /></c:if>
<h1 class="wi"><c:out value="${fn:length(workers)}" /> of <c:out value="${totalWorkerCount}" /> Workers Working</h1>
<p class="intro">The list below contains all workers which are currently running a job.</p>
<table class="workers">
	<tr>
		<th>&nbsp;</th>
		<th>Where</th>
		<th>Queue</th>
		<th>Processing</th>
	</tr>
	<c:if test="${empty workers}">
	<tr>
		<td colspan="4" class="no-data">Nothing is happening right now...</td>
	</tr>
	</c:if>
	<c:forEach items="${workers}" var="worker">
	<c:set var="state">${worker.state}</c:set>
	<tr>
		<td class="icon"><img src="<c:url value="/images/${fn:toLowerCase(state)}.png" />" alt="<c:out value="${fn:toLowerCase(state)}" />" title="<c:out value="${fn:toLowerCase(state)}" />"></td>
		<td class="where"><a href="<c:url value="/workers/${worker}" />"><c:out value="${worker.host}" />:<c:out value="${worker.pid}" /></a></td>
		<td class="queues queue">
			<a class="queue-tag" href="<c:url value="/queues/${worker.status.queue}" />"><c:out value="${worker.status.queue}" /></a>
		</td>
		<td class="process">
		<c:choose>
		<c:when test="${not empty worker.status.queue}">
			<code><c:out value="${worker.status.payload.className}" /></code>
			<small><a class="queue time" href="<c:url value="/working/${worker}" />"><c:out value="${jsq:formatDate(worker.status.runAt)}" /></a></small>
		</c:when>
		<c:otherwise>
			<span class="waiting">Waiting for a job...</span>
		</c:otherwise>
		</c:choose>
		</td>
	</tr>
	</c:forEach>
</table>
<c:if test="${not poll and empty param.partial}"><jsp:include page="footer.jsp" /></c:if>
