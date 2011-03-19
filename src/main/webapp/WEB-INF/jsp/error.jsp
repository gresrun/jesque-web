<?xml version="1.0" encoding="UTF-8"?>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="false" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %> 
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
			<pre>
<c:forEach items="${exception.stackTrace}" var="trace">
	<c:out value="${trace}"/>
</c:forEach>
			</pre>
		</div>
		<div id="footer">
			<p>Powered by <a href="http://github.com/gresrun/jesque">Jesque</a> based on <a href="http://github.com/defunkt/resque">Resque</a></p>
		</div>
	</body>
</html>
