<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="false" trimDirectiveWhitespaces="true" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %> 
<%@ taglib uri="http://greghaines.net/jesque/web/tags/jsq" prefix="jsq" %> 
<c:if test="${not poll and empty param.partial}"><jsp:include page="header.jsp" /></c:if>
<h1 class="wi">Workers</h1>
<p class='intro'>The hostnames below all have registered workers. Select a hostname to view its workers, or "all" to see all workers.</p>
<table class="queues">
	<tr>
		<th>Hostname</th>
		<th>Workers</th>
	</tr>
	<c:forEach items="${hostMap}" var="entry">
	<tr>
		<td class="queue"><a class="queue" href="<c:url value="/workers/${entry.key}" />"><c:out value="${entry.key}" /></a></td>
		<td class="size"><c:out value="${entry.value.size}" /></td>
	</tr>
	</c:forEach>
	<tr class="failed">
		<td class="queue failed"><a class="queue" href="<c:url value="/workers/all" />">all workers</a></td>
		<td class="size"><c:out value="${totalWorkerCount}" /></td>
	</tr>
</table>
<c:if test="${not poll and empty param.partial}"><jsp:include page="footer.jsp" /></c:if>
