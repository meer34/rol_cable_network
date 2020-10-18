<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1" isELIgnored="false"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
	<meta name="viewport" content="width=device-width, initial-scale=1">
	<title>Login RCN</title>
	<!--===============================================================================================-->
	<link rel="icon" type="image/png" href="images/icons/favicon.ico"/>
	<link rel="stylesheet" type="text/css" href="css/index.css">
	<link rel="stylesheet" type="text/css" href="css/neon.css">
	<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.5.1/jquery.min.js"></script>
	<!--===============================================================================================-->
</head>
<body>
	</br>
	<div class="title">
		<div class="neon">
			<span class="text" data-text="Rol Cable Network">Rol Cable Network</span>
			<span class="gradient">
			</span> <span class="spotlight"></span>
		</div>
	</div>
	<div class="card3">
		<div class="login-pic" data-tilt>
			<img src="images/image.png" alt="IMG">
		</div>
		<div class="login-box">
			<div class="login-header">User Login</div>
			<form action="login" method="post">
				<div class="login-input">
					<div class="username">
						<svg fill="#999" viewBox="0 0 1024 1024">
						<path class="path1"
							d="M896 307.2h-819.2c-42.347 0-76.8 34.453-76.8 76.8v460.8c0 42.349 34.453 76.8 76.8 76.8h819.2c42.349 0 76.8-34.451 76.8-76.8v-460.8c0-42.347-34.451-76.8-76.8-76.8zM896 358.4c1.514 0 2.99 0.158 4.434 0.411l-385.632 257.090c-14.862 9.907-41.938 9.907-56.802 0l-385.634-257.090c1.443-0.253 2.92-0.411 4.434-0.411h819.2zM896 870.4h-819.2c-14.115 0-25.6-11.485-25.6-25.6v-438.566l378.4 252.267c15.925 10.618 36.363 15.925 56.8 15.925s40.877-5.307 56.802-15.925l378.398-252.267v438.566c0 14.115-11.485 25.6-25.6 25.6z"></path></svg>
						<input type="text" class="user-input" name="username"
							placeholder="username" autocomplete="off" />
					</div>
					<div class="password">
						<svg fill="#999" viewBox="0 0 1024 1024">
						<path class="path1"
							d="M742.4 409.6h-25.6v-76.8c0-127.043-103.357-230.4-230.4-230.4s-230.4 103.357-230.4 230.4v76.8h-25.6c-42.347 0-76.8 34.453-76.8 76.8v409.6c0 42.347 34.453 76.8 76.8 76.8h512c42.347 0 76.8-34.453 76.8-76.8v-409.6c0-42.347-34.453-76.8-76.8-76.8zM307.2 332.8c0-98.811 80.389-179.2 179.2-179.2s179.2 80.389 179.2 179.2v76.8h-358.4v-76.8zM768 896c0 14.115-11.485 25.6-25.6 25.6h-512c-14.115 0-25.6-11.485-25.6-25.6v-409.6c0-14.115 11.485-25.6 25.6-25.6h512c14.115 0 25.6 11.485 25.6 25.6v409.6z"></path></svg>
						<input type="password" class="pass-input" name="password"
							placeholder="password" />
					</div>
				</div>
				<button class="signin-button" type="submit">Login</button>
			</form>
			<div class="error-msg">
				<h4>${SPRING_SECURITY_LAST_EXCEPTION.message}</h4>
			</div>
			<div class="logout-msg">
      			<p>${logout_message}</p>
			</div>
			<div class="link">
				<a href="#">Forgot password?</a> or <a href="#">Sign up</a>
			</div>
		</div>
	</div>

	<script src="js/tilt.jquery.min.js"></script>
	
</body>
</html>