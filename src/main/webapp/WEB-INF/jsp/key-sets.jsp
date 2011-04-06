<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="false" trimDirectiveWhitespaces="true" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://greghaines.net/jesque/web/tags/jsq" prefix="jsq" %>
<c:if test="${not poll and empty param.partial}"><jsp:include page="header.jsp" /></c:if>
<p class="sub">Showing <c:out value="${(key.size eq 0) ? 0 : start + 1}" /> to <c:out value="${jsq:min(start + count, key.size)}" /> of <b><c:out value="${key.size}" /></b> elements</p>
<h1>Key "<c:out value="${key}" />" is a <c:out value="${key.type}" /></h1>
<table>
	<c:forEach items="${key.arrayValue}" var="val">
	<tr>
		<td><c:out value="${val}" /></td>
	</tr>
	</c:forEach>
</table>
<jsp:include page="pagination.jsp">
	<jsp:param name="start" value="${start}" />
	<jsp:param name="count" value="${count}" />
	<jsp:param name="size" value="${key.size}" />
	<jsp:param name="currentPage" value="${pageContext.request.contextPath}/stats/keys/${key}" />
</jsp:include>
<c:if test="${not poll and empty param.partial}"><jsp:include page="footer.jsp" /></c:if>
