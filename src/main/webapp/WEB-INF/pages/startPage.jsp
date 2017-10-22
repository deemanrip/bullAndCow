<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<html>
<head>
	<title>Start Page</title>
	<style type="text/css">
    		th {
    			background: #31B404;
    		}
    </style>
</head>
<body>
<a href="/startGame">Start Game</a>
<p>Statistics:</p>
<table border="1" cellpadding="1" width="40%" height="100px">
	<tr>
		<th>Login</th>
		<th>Games</th>
		<th>Average Number Of Attempts</th>
	</tr>
	<c:forEach items="${usersList}" var="user">
		<tr>
			<td>${user.login}</td>
			<td>${user.statistics.games}</td>
			<td>${user.statistics.averageNumberOfAttempts}</td>
		</tr>
	</c:forEach>
</table>
</body>
</html>