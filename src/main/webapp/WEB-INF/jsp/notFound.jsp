<?xml version="1.0" encoding="UTF-8"?>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="false" trimDirectiveWhitespaces="true" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %> 
<!DOCTYPE html>
<html lang="en">
	<head>
		<meta charset="UTF-8" />
		<title>Jesque.</title>
		<link rel="stylesheet" type="text/css" href="<c:url value="/css/reset.css" />" />
		<link rel="stylesheet" type="text/css" href="<c:url value="/css/style.css" />" />
		<style>
			div.fourOhFourTable {
				display: table;
			}
			div.fourOhFourRow {
				display: table-row;
			}
			div.fourOhFourCell {
				display: table-cell;
				vertical-align: middle;
				padding: 5px 5px 5px 25px;
			}
			span.big404 {
				font-weight: bold;
				font-size: 172px;
				color: #CE1212;
				text-shadow: #DDDDDD 2px 2px 3px;
			}
			span.fourOhFourText {
				color: #000000;
				font-size: 24px;
				font-weight: bold;
			}
		</style>
	</head>
	<body>
		<div class="header">
			<ul class="nav">
				<li class="current"><a href="#">Not Found</a></li>
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
			<p>Powered by <a href="http://github.com/gresrun/jesque">Jesque</a> based on <a href="http://github.com/defunkt/resque">Resque</a></p>
		</div>
	</body>
</html>
