<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %> 
<%@ taglib uri="http://greghaines.net/jesque/web/tags/jsq" prefix="jsq" %> 
<c:if test="${not poll and empty param.partial}"><jsp:include page="header.jsp" /></c:if>
<h1 class="wi"><c:out value="${fn:length(workers)}" /> Workers</h1>
<p class="intro">The workers listed below are all registered as active on your system.</p>
<table class="workers">
	<tr>
		<th>&nbsp;</th>
		<th>Where</th>
		<th>Queues</th>
		<th>Processing</th>
	</tr>
	<c:forEach items="${workers}" var="worker">
	<tr>
		<td class="icon"><img src="<c:url value="/images/${fn:toLowerCase(worker.state)}.png" />" alt="<c:out value="${fn:toLowerCase(worker.state)}" />" title="<c:out value="${fn:toLowerCase(worker.state)}" />"></td>
		<td class="where"><a href="<c:url value="/workers/${worker}" />"><c:out value="${worker.host}:${worker.pid}" /></a></td>
		<td class="queues"><c:forEach items="${worker.queues}" var="queue"><a class="queue-tag" href="<c:url value="/queues/${queue}" />"><c:out value="${queue}" /></a></c:forEach></td>
		<td class="process">
		<c:choose>
		<c:when test="${not empty worker.status}">
			<code><c:out value="${worker.status.payload.className}" /></code>
			<small><a class="queue time" href="<c:url value="/working/${worker}" />"><c:out value="${worker.status.runAt}" /></a></small>
		</c:when>
		<c:otherwise>
			<span class="waiting">Waiting for a job...</span>
		</c:otherwise>
		</c:choose>
		</td>
	</tr>
	</c:forEach>
	<c:if test="${empty workers}">
	<tr>
		<td colspan="4" class="no-data">There are no registered workers</td>
	</tr>
	</c:if>
</table>
${pollController}
<c:if test="${not poll and empty param.partial}"><jsp:include page="footer.jsp" /></c:if>
