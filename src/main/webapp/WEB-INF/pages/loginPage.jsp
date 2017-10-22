<!DOCTYPE html>
<html>
<head>
	<title>Login</title>
</head>
<body>
<h2>Please, log in</h2>
<form action="/validateCredentials" method="post">
	<p>Login:</p>
	<p><input type="text" name="login"></p>
	<p>Password:</p>
	<p><input type="password" name="password"></p>
	<input type="submit" value="Sign in">
	<a href="/createNewUser">Sign up</a>
</form>
</body>
</html>