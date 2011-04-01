<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="false" trimDirectiveWhitespaces="true" isErrorPage="true" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<!--[if lt IE 7 ]> <html class="no-js ie6" lang="en"> <![endif]-->
<!--[if IE 7 ]>    <html class="no-js ie7" lang="en"> <![endif]-->
<!--[if IE 8 ]>    <html class="no-js ie8" lang="en"> <![endif]-->
<!--[if (gte IE 9)|!(IE)]><!--> <html class="no-js" lang="en"> <!--<![endif]-->
	<head>
		<meta charset="UTF-8" />
		<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1" />
		<title>Resque.</title>
		<link rel="stylesheet" type="text/css" href="<c:url value="/css/reset.css" />" />
		<link rel="stylesheet" type="text/css" href="<c:url value="/css/style.css" />" />
	</head>
	<body>
		<div class="header">
			<ul class="nav">
				<li class="current"><a href="#">404 - Not Found</a></li>
			</ul>
		</div>
		<div id="main">
			<div class="fourOhFourTable">
				<div class="fourOhFourRow">
					<div class="fourOhFourCell"><span class="big404">404</span></div>
					<div class="fourOhFourCell">
						<span class="fourOhFourText">Whatch'a lookin' for?</span>
						<hr/>
						<span class="fourOhFourText"><a href="<c:url value="/" />">Take me home, please!</a></span>
					</div>
				</div>
			</div>
		</div>
		<div id="footer">
			<p>Powered by <a href="https://github.com/gresrun/jesque">Jesque</a> - Inspired by <a href="https://github.com/defunkt/resque">Resque</a></p>
		</div>
	</body>
</html>
