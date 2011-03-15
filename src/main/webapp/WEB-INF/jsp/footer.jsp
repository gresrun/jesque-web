<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
		</div>
		<div id="footer">
			<p>Powered by <a href="http://github.com/gresrun/jesque">Jesque</a> v<c:out value="${version}" /> based on <a href="http://github.com/defunkt/resque">Resque</a></p>
			<p>Connected to Redis namespace <c:out value="${namespace}" /> on <c:out value="${redisUri}" /></p>
		</div>
	</body>
</html>
