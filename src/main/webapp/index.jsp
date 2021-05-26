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
		<textarea id="datasets" name="datasets" rows="4" cols="80">http://pt.dbpedia.org/sparql, https://dbpedia.org/sparql</textarea>
		<br> <br> <input type="submit" value="Submit">
	</form>
	<br>
	<h3>Or</h3>
	<ol style="margin-top: 0px">
	<li><a href="http://141.57.11.86:8082/DatasetMatchingWeb/comparehdt.jsp">Compare HDT files or another type of RDF files</a></li>
		<li><a href="genericEndPointQuery.jsp">Query your prefered
				dataset</a></li>
		<li><a href="http://w3id.org/relod">Go to ReLOD, the Dataset
				Similarity index</a></li>
	</ol>
</body>
</html>