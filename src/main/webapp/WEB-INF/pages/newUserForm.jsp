<!DOCTYPE html>
<html>
<head>
	<title>Registration</title>
</head>
<body>
<h2>Create new account</h2>
<form action="/createNewUser" method="post">
	<p>Login:</p>
	<p><input type="text" name="login"></p>
	<p>Password:</p>
	<p><input type="password" name="password"></p>
	<p>Confirm Password:</p>
	<p><input type="password" name="confirmPassword"></p>
	<input type="submit" value="Create Account">
	<a href="/login.html">Sign in</a>
</form>
</body>
</html>