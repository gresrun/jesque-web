<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="false" trimDirectiveWhitespaces="true" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<c:if test="${not poll and empty param.partial}"><jsp:include page="header.jsp" /></c:if>
<jsp:include page="queues.jsp"><jsp:param name="partial" value="true" /></jsp:include>
<hr />
<jsp:include page="working.jsp"><jsp:param name="partial" value="true" /></jsp:include>
${pollController}
<c:if test="${not poll and empty param.partial}"><jsp:include page="footer.jsp" /></c:if>
