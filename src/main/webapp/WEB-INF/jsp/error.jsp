<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="false" trimDirectiveWhitespaces="true" isErrorPage="true" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://greghaines.net/jesque/web/tags/jsq" prefix="jsq" %>
<!DOCTYPE html>
<!--[if lt IE 7 ]> <html class="no-js ie6" lang="en"> <![endif]-->
<!--[if IE 7 ]>    <html class="no-js ie7" lang="en"> <![endif]-->
<!--[if IE 8 ]>    <html class="no-js ie8" lang="en"> <![endif]-->
<!--[if (gte IE 9)|!(IE)]><!--> <html class="no-js" lang="en"> <!--<![endif]-->
	<head>
		<meta charset="UTF-8" />
		<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1" />
		<meta name="apple-mobile-web-app-capable" content="yes" />
		<meta name="apple-mobile-web-app-status-bar-style" content="black" />
		<title>Resque.</title>
		<link rel="shortcut icon" href="<c:url value="/favicon.ico" />" />
		<link rel="apple-touch-icon-precomposed" href="apple-touch-icon-precomposed.png" />
		<link rel="apple-touch-icon-precomposed" sizes="72x72" href="apple-touch-icon-72x72-precomposed.png" />
		<link rel="apple-touch-icon-precomposed" sizes="114x114" href="apple-touch-icon-114x114-precomposed.png" />
		<link rel="stylesheet" type="text/css" href="<c:url value="/css/reset.css" />" />
		<link rel="stylesheet" type="text/css" href="<c:url value="/css/style.css" />" />
	</head>
	<body>
		<div class="header">
			<ul class="nav">
				<li class="current"><a href="#"><c:out value="${errorCode} - ${errorName}" /></a></li>
			</ul>
		</div>
		<div id="main">
			<h1 class="error"><c:out value="${errorType}: ${errorMessage}" /></h1>
			<pre class="error"><c:out value="${fn:join(stackTrace, jsq:newLine())}" /></pre>
		</div>
		<div id="footer">
			<p>Powered by <a href="https://github.com/gresrun/jesque">Jesque</a> - Inspired by <a href="https://github.com/defunkt/resque">Resque</a></p>
		</div>
	</body>
</html>
