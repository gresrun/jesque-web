<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="false" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://greghaines.net/jesque/web/tags/jsq" prefix="jsq" %>
<c:if test="${not poll and empty param.partial}"><jsp:include page="header.jsp" /></c:if>
<c:choose>
<c:when test="${not empty worker}">
<h1>Worker <c:out value="${worker}" /></h1>
<table class="workers">
	<tr>
		<th>&nbsp;</th>
		<th>Host</th>
		<th>Pid</th>
		<th>Started</th>
		<th>Queues</th>
		<th>Processed</th>
		<th>Failed</th>
		<th>Processing</th>
	</tr>
	<tr>
		<td class="icon"><img src="<c:url value="/images/${fn:toLowerCase(worker.state)}.png" />" alt="<c:out value="${fn:toLowerCase(worker.state)}" />" title="<c:out value="${fn:toLowerCase(worker.state)}" />"></td>
		<td><c:out value="${worker.host}" /></td>
		<td><c:out value="${worker.pid}" /></td>
		<td><span class="time"><c:out value="${jsq:formatDate(worker.started)}" /></span></td>
		<td class="queues"><c:forEach items="${worker.queues}" var="queue"><a class="queue-tag" href="<c:url value="/queues/${queue}" />"><c:out value="${queue}" /></a></c:forEach></td>
		<td><c:out value="${worker.processed}" /></td>
		<td><c:out value="${worker.failed}" /></td>
		<td class="process">
		<c:choose>
		<c:when test="${not empty worker.status}">
			<code><c:out value="${worker.status.payload.className}" /></code>
			<small><a class="queue time" href="<c:url value="/working/${worker}" />"><c:out value="${jsq:formatDate(worker.status.runAt)}" /></a></small>
		</c:when>
		<c:otherwise>
			<span class="waiting">Waiting for a job...</span>
		</c:otherwise>
		</c:choose>
		</td>
	</tr>
</table>
</c:when>
<c:otherwise>
<h1>Worker doesn't exist</h1>
</c:otherwise>
</c:choose>
<c:if test="${not poll and empty param.partial}"><jsp:include page="footer.jsp" /></c:if>
