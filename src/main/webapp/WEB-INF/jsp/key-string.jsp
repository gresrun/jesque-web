<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="false" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://greghaines.net/jesque/web/tags/jsq" prefix="jsq" %> 
<c:if test="${not poll and empty param.partial}"><jsp:include page="header.jsp" /></c:if>
<c:choose>
<c:when test="${empty key}">
<h1>Key "<c:out value="${keyName}" />" is not a valid key</h1>
</c:when>
<c:otherwise>
<h1>Key "<c:out value="${key}" />" is a <c:out value="${key.type}" /></h1>
<h2>size: <c:out value="${key.size}" /></h2>
<table>
	<tr>
		<td><c:out value="${jsq:toJson(key.arrayValue)}" /></td>
	</tr>
</table>
</c:otherwise>
</c:choose>
<c:if test="${not poll and empty param.partial}"><jsp:include page="footer.jsp" /></c:if>
