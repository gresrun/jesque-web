<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="false" trimDirectiveWhitespaces="true" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<c:if test="${not poll and empty param.partial}"><jsp:include page="header.jsp" /></c:if>
<h1><c:out value="${title}" /></h1>
<c:if test="${not empty subTitle}"><p class="sub"><c:out value="${subTitle}" /></p></c:if>
<table class="stats">
	<c:if test="${not empty stats}">
	<c:forEach items="${stats}" var="entry">
	<tr>
		<th><c:out value="${entry.key}" /></th>
		<td><c:out value="${entry.value}" /></td>
	</tr>
	</c:forEach>
	</c:if>
	<c:if test="${not empty keys}">
	<tr>
		<th class="statHeader">Key</th>
		<th class="statHeader">Type</th>
		<th class="statHeader">Size</th>
    </tr>
    <c:forEach items="${keys}" var="key">
	<tr>
		<th><a href="<c:url value="/stats/keys/${key}" />"><c:out value="${key}" /></a></th>
		<td><c:out value="${key.type}" /></td>
		<td><c:out value="${key.size}" /></td>
    </tr>
	</c:forEach>
	</c:if>
</table>
<c:if test="${not poll and empty param.partial}"><jsp:include page="footer.jsp" /></c:if>
