<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
</head>
<body>
<h1>Generic Sparql Query EndPoint</h1>
<form action="SimilarityServlet">
  <label for="fname">Dataset:</label>
  <input type="text" id="dataset" name="dataset" value="https://dbpedia.org/sparql"><br><br>
  <label for="lname">SPARQL query:</label>
  <textarea id="query" name="query" rows="4" cols="50">select distinct ?Concept where {[] a ?Concept} LIMIT 100</textarea><br><br>
  <input type="submit" value="Submit">
  <br/><a href="http://141.57.11.86:8082/DatasetMatchingWeb/genericEndPointQuery.jsp">Query HDT files or another RDF format</a>
  <br/><a href="index.jsp">Back to Dataset similarity...</a>
  <br/><a href="http://w3id.org/relod">Go to ReLOD, the Dataset
				Similarity index</a>
</form>
</body>
</html>