<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="false" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://greghaines.net/jesque/web/tags/jsq" prefix="jsq" %>
<c:if test="${not poll and empty param.partial}"><jsp:include page="header.jsp" /></c:if>
<c:choose>
<c:when test="${empty queue}">
<h1>Queue <span class="hl"><c:out value="${queueName}" /></span> is not a valid queue</h1>
</c:when>
<c:otherwise>
<h1>Pending jobs on <span class="hl"><c:out value="${queue}" /></span></h1>
<form method="POST" action="<c:url value="/queues/${queue}/remove" />" class="remove-queue">
	<input type="submit" name="" value="Remove Queue" />
</form>
<p class="sub">Showing <c:out value="${start + 1}" /> to <c:out value="${jsq:min(start + count, queue.size)}" /> of <b><c:out value="${queue.size}" /></b> jobs</p>
<table class="jobs">
	<tr>
		<th>Class</th>
		<th>Args</th>
	</tr>
	<c:forEach items="${queue.jobs}" var="job">
	<tr>
		<td class="class"><c:out value="${job.className}" /></td>
		<td class="args"><c:out value="${jsq:toJson(job.args)}" /></td>
	</tr>
	</c:forEach>
	<c:if test="${empty queue.jobs}">
	<tr>
		<td class="no-data" colspan="2">There are no pending jobs in this queue</td>
	</tr>
	</c:if>
</table>
<jsp:include page="pagination.jsp">
	<jsp:param name="start" value="${start}" />
	<jsp:param name="count" value="${count}" />
	<jsp:param name="size" value="${queue.size}" />
	<jsp:param name="currentPage" value="${pageContext.request.contextPath}/queues/${queue}" />
</jsp:include>
</c:otherwise>
</c:choose>
<c:if test="${not poll and empty param.partial}"><jsp:include page="footer.jsp" /></c:if>
