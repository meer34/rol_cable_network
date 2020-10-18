<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
	<title>Welcome to Cards</title>
	<link rel="icon" type="image/png" href="images/icons/favicon.ico"/>
	<link rel="stylesheet" href="css/card.css">
	<link rel="stylesheet" href="css/neon.css">
</head>
<body>
	<div class="page-container">
		<div class="title">
			<div class="neon">
				<span class="text" data-text="Rol Cable Network">Rol Cable Network</span>
				<span class="gradient">
				</span> <span class="spotlight"></span>
			</div>
		</div>
		<div class="c-button">
			<div class="card-login">
				<a href="/">Home</a>
			</div>
			<div class="card-logout">
				<a href="/logout">Logout</a>
			</div>
		</div>
		<section class="card-list" id="menu">
		
			<div class="container">
				<article class="card1">
					<header class="card-header">
						<br/>
						<h3><a href="#" onclick="addCustomer()">Add Customer</a></h3>
						<h3><a href="#">View Customer</a></h3>
						<h3><a href="#">Modify Customer</a></h3>
						<h3><a href="#">Delete Customer</a></h3>
					</header>
				</article>
				<article class="card2">
					<header class="card-header">
						<h2>Customer</h2>>
					</header>
				</article>
			</div>
			
			<div class="container">
				<article class="card1">
					<header class="card-header">
						<br/>
						<h3><a href="#">Add Collection</a></h3>
						<h3><a href="#">Update Collection</a></h3>
						<h3><a href="#">Delete Collection</a></h3>
					</header>
				</article>
				<article class="card2">
					<header class="card-header">
						<h2>Collection</h2>>
					</header>
				</article>
			</div>
			
			<div class="container">
				<article class="card1">
					<header class="card-header">
						<br/>
						<h3><a href="#">Add Expense</a></h3>
						<h3><a href="#">Update Expense</a></h3>
						<h3><a href="#">Delete Expense</a></h3>
					</header>
				</article>
				<article class="card2">
					<header class="card-header">
						<h2>Expense</h2>>
					</header>
				</article>
				</div>
				
			<div class="container">
				<article class="card1">
					<header class="card-header">
						<br/>
						<h3><a href="#">Link 1</a></h3>
						<h3><a href="#">Link 2</a></h3>
						<h3><a href="#">Link 3</a></h3>
						<h3><a href="#">Link 4</a></h3>
						<h3><a href="#">Link 5</a></h3>
					</header>
				</article>
				<article class="card2">
					<header class="card-header">
						<h2>Buld<br/>Inquiry</h2>>
					</header>
				</article>
			</div>
			
			<div class="container">
				<article class="card1">
					<header class="card-header">
						<br/>
						<h3><a href="#">Link 1</a></h3>
						<h3><a href="#">Link 2</a></h3>
					</header>
				</article>
				<article class="card2">
					<header class="card-header">
						<h2>Update<br/>Info</h2>>
					</header>
				</article>
			</div>
			
			<div class="container">
				<article class="card1">
					<header class="card-header">
						<br/>
						<h3><a href="#">Link 1</a></h3>
						<h3><a href="#">Link 2</a></h3>
						<h3><a href="#">Link 3</a></h3>
						<h3><a href="#">Link 4</a></h3>
					</header>
				</article>
				<article class="card2">
					<header class="card-header">
						<h2>Usage<br/>Statistics</h2>>
					</header>
				</article>
			</div>
			
			<div class="container">
				<article class="card1">
					<header class="card-header">
						<br/>
						<h3><a href="#">Link 1</a></h3>
						<h3><a href="#">Link 2</a></h3>
						<h3><a href="#">Link 3</a></h3>
					</header>
				</article>
				<article class="card2">
					<header class="card-header">
						<h2>TBD</h2>>
					</header>
				</article>
			</div>
		</section>
	</div>
</body>
</html>