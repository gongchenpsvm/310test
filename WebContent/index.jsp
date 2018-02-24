<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
	<head>
		<link rel="stylesheet" href="SecondPageCSS.css">
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<title>Insert title here</title>
	</head>
	<body>
		<p>Collage for topic X</p>
	</body>
	<body>
		<p>Collage PlaceHolder</p>
	</body>
	<body class = buildCollegeAttributes>
		<div style="text-align:center">
			<form name="topicForm" method="post" action="gramaServlet">
	  			<input type="text" name="topicEntered" placeholder = "Enter Topic" style = "border:3px solid grey">
	  			<input type = "submit" value = "Build Another Collage" style = "background-color:grey; color: white " >
	  			<input type = "submit" value = "Export Collage" style = "background-color:grey; color: white">	
			</form>
		</div>
	</body>
	</br>
	</br>
	<div class="horizontal-scroll-wrapper squares">
  		<div>item 1</div>
 		<div>item 2</div>
  		<div>item 3</div>
  		<div>item 4</div>
  		<div>item 5</div>
  		<div>item 6</div>
  		<div>item 7</div>
  		<div>item 8</div>
	</div>
</html>