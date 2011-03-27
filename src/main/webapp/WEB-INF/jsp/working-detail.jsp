<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="false" trimDirectiveWhitespaces="true" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %> 
<%@ taglib uri="http://greghaines.net/jesque/web/tags/jsq" prefix="jsq" %> 
<c:if test="${not poll and empty param.partial}"><jsp:include page="header.jsp" /></c:if>
<c:choose>
<c:when test="${not empty worker}">
<h1><c:out value="${worker}" />'s job</h1>
<table>
	<tr>
		<th>&nbsp;</th>
		<th>Where</th>
		<th>Queue</th>
		<th>Started</th>
		<th>Class</th>
		<th>Args</th>
	</tr>
	<tr>
		<td><img src="<c:url value="/images/working.png" />" alt="working" title="working"></td>
		<td><a href="<c:url value="/workers/${worker}" />"><c:out value="${worker.host}:${worker.pid}" /></a></td>
		<td><a class="queue" href="<c:url value="/queues/${worker.status.queue}" />"><c:out value="${worker.status.queue}" /></a></td>
		<td><span class="time"><c:out value="${jsq:formatDate(worker.status.runAt)}" /></span></td>
		<td><code><c:out value="${worker.status.payload.className}" /></code></td>
		<td><c:out value="${jsq:toJson(worker.status.payload.args)}" /></td>
	</tr>
</table>
</c:when>
<c:otherwise>
<h1>Worker doesn't exist</h1>
</c:otherwise>
</c:choose>
<c:if test="${not poll and empty param.partial}"><jsp:include page="footer.jsp" /></c:if>
