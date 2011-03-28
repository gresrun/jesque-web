<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="false" trimDirectiveWhitespaces="true" isErrorPage="true" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://greghaines.net/jesque/web/tags/jsq" prefix="jsq" %>
<!DOCTYPE html>
<html lang="en">
	<head>
		<meta charset="UTF-8" />
		<title>Jesque.</title>
		<link rel="stylesheet" type="text/css" href="<c:url value="/css/reset.css" />" />
		<link rel="stylesheet" type="text/css" href="<c:url value="/css/style.css" />" />
	</head>
	<body>
		<div class="header">
			<ul class="nav">
				<li class="current"><a href="#">Error</a></li>
			</ul>
		</div>
		<div id="main">
			<h1 class="error"><c:out value="${exception}" /></h1>
			<pre class="error"><c:out value="${jsq:asBacktrace(exception)}" /></pre>
		</div>
		<div id="footer">
			<p>Powered by <a href="https://github.com/gresrun/jesque">Jesque</a> - Inspired by <a href="https://github.com/defunkt/resque">Resque</a></p>
		</div>
	</body>
</html>
