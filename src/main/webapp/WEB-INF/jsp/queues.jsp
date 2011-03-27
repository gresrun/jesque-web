<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="false" trimDirectiveWhitespaces="true" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<c:if test="${not poll and empty param.partial}"><jsp:include page="header.jsp" /></c:if>
<h1 class="wi">Queues</h1>
<p class="intro">The list below contains all the registered queues with the number of jobs currently in the queue. Select a queue from above to view all jobs currently pending on the queue.</p>
<table class="queues">
	<tr>
		<th>Name</th>
		<th>Jobs</th>
	</tr>
	<c:forEach items="${queues}" var="queue">
	<tr>
		<td class="queue"><a class="queue" href="<c:url value="/queues/${queue}" />"><c:out value="${queue}" /></a></td>
		<td class="size"><c:out value="${queue.size}" /></td>
	</tr>
	</c:forEach>
	<tr class="<c:out value="${(totalFailureCount eq 0) ? 'failed' : 'failure'}" />">
		<td class="queue failed"><a class="queue" href="<c:url value="/failed" />">failed</a></td>
		<td class="size"><c:out value="${totalFailureCount}" /></td>
	</tr>
</table>
<c:if test="${not poll and empty param.partial}"><jsp:include page="footer.jsp" /></c:if>
