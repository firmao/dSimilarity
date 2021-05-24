<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
</head>
<body>
<h1>Which properties and classes the datasets have in common?</h1>
<form action="SimilarityServlet">
  <label for="fname">Datasets</label>
  <textarea id="datasets" name="datasets" rows="4" cols="50">http://pt.dbpedia.org/sparql, https://dbpedia.org/sparql</textarea><br><br>
  <input type="submit" value="Submit">
  <a href="genericEndPointQuery.jsp">Query your prefered dataset</a>
</form>
</body>
</html>