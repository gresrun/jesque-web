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
		<script type="text/javascript" src="<c:url value="/js/jquery-1.5.1.min.js" />"></script>
		<script type="text/javascript" src="<c:url value="/js/jquery.relatize_date.js" />"></script>
		<script type="text/javascript" src="<c:url value="/js/jesque.js" />"></script>
	</head>
	<body>
		<div class="header">
			<ul class="nav">
			<c:forEach items="${tabs}" var="tab">
				<li<c:if test="${(tab eq activeTab)}"> class="current"</c:if>><a href="<c:url value="/${fn:toLowerCase(tab)}" />"><c:out value="${tab}" /></a></li>
			</c:forEach>
			</ul>
			<c:if test="${namespace ne 'resque'}"><abbr class="namespace" title="Resque's Redis Namespace"><c:out value="${namespace}" /></abbr></c:if>
		</div>
		<c:if test="${not empty subTabs}">
		<ul class="subnav">
		<c:forEach items="${subTabs}" var="subTab">
			<li<c:if test="${subTab eq activeSubTab}"> class="current"</c:if>><a href="<c:url value="/${fn:toLowerCase(activeTab)}/${fn:toLowerCase(subTab)}" />"><span><c:out value="${subTab}" /></span></a></li>
		</c:forEach>
		</ul>
		</c:if>
		<div id="main">