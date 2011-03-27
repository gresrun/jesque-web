<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="false" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:if test="${(param.start - param.count >= 0) or (param.start + param.count < param.size)}">
<p class="pagination">
	<c:if test="${param.start - param.count >= 0}">
	<a href="<c:out value="${param.currentPage}" />?start=<c:out value="${param.start - param.count}" />&count=<c:out value="${param.count}"/>" class="less">&laquo; less</a>
	</c:if>
	<c:if test="${param.start + param.count < param.size}">
	<a href="<c:out value="${param.currentPage}" />?start=<c:out value="${param.start + param.count}" />&count=<c:out value="${param.count}"/>" class="more">more &raquo;</a>
	</c:if>
</p>
</c:if>