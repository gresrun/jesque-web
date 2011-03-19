<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="false" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://greghaines.net/jesque/web/tags/jsq" prefix="jsq" %>
<c:if test="${not poll and empty param.partial}"><jsp:include page="header.jsp" /></c:if>
<h1>Failed Jobs</h1>
<c:if test="${not empty failures}">
<form method="POST" action="<c:url value="/failed/clear" />" class="clear-failed">
	<input type="submit" name="" value="Clear Failed Jobs" />
</form>
</c:if>
<p class="sub">Showing <c:out value="${start + 1}" /> to <c:out value="${jsq:min(start + count, fullFailCount)}" /> of <b><c:out value="${fullFailCount}" /></b> jobs</p>
<ul class="failed">
	<c:set var="index" value="0" />
	<c:forEach items="${failures}" var="job">
	<li>
		<dl>
			<dt>Worker</dt>
			<dd>
				<a href="<c:url value="/workers/${job.worker}" />"><c:out value="${jsq:workerShortName(job.worker)}" /></a> on <b class="queue-tag"><c:out value="${job.queue}" /></b> at <b><span class="time"><c:out value="${jsq:formatDate(job.failedAt)}" /></span></b>
				<div class="retry">
				<c:choose>
				<c:when test="${not empty job.retriedAt}">
					Retried <b><span class="time"><c:out value="${jsq:formatDate(job.retriedAt)}" /></span></b>
				</c:when>
				<c:otherwise>
					<a href="<c:url value="/failed/requeue/${start + index}" />" rel="retry">Retry</a>
				</c:otherwise>
				</c:choose>
				</div>
			</dd>
			<dt>Class</dt>
			<dd><code><c:out value="${(not empty job.payload) ? job.payload.className : 'null'}" /></code></dd>
			<dt>Arguments</dt>
			<dd><code><c:out value="${(not empty job.payload) ? jsq:toJson(job.payload.args) : 'null'}" /></code></dd>
			<dt>Exception</dt>
			<dd><code><c:out value="${job.exception.class.name}" /></code></dd>
			<dt>Error</dt>
			<dd class="error">
				<a href="#" class="backtrace"><c:out value="${job.exception.message}" /></a>
				<pre style="display: none;"><c:out value="${jsq:asBacktrace(job.exception)}" /></pre>
			</dd>
		</dl>
		<div class="r"></div>
	</li>
	<c:set var="index" value="${index + 1}" />
	</c:forEach>
</ul>
<jsp:include page="pagination.jsp">
	<jsp:param name="start" value="${start}" />
	<jsp:param name="count" value="${count}" />
	<jsp:param name="size" value="${fullFailCount}" />
	<jsp:param name="currentPage" value="${pageContext.request.contextPath}/failed" />
</jsp:include>
<c:if test="${not poll and empty param.partial}"><jsp:include page="footer.jsp" /></c:if>
