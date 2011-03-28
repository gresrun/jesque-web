<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="false" trimDirectiveWhitespaces="true" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
		</div>
		<div id="footer">
			<p>Powered by <a href="https://github.com/gresrun/jesque">Jesque</a> v<c:out value="${version}" /> - Inspired by <a href="https://github.com/defunkt/resque">Resque</a></p>
			<p>Connected to Redis namespace <c:out value="${namespace}" /> on <c:out value="${redisUri}" /></p>
		</div>
	</body>
</html>
