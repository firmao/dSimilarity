# dSimilarity
Dataset Similarity

http://141.57.11.86:8083/dSimilarity


Upload data to the triplestore using rdf4j:

- Download latest version of Tom-cat server (here we use tomcat 9.0.45)
- Download the rdf4j(Full SDK) from: http://www.eclipse.org/downloads/download.php?file=/rdf4j/eclipse-rdf4j-3.7.1-sdk.zip
- copy rdf4j-server and rdf4j-workbench from rdf4jDir/war to apache-tomcat/webapps
- Start tomcat normally.
- Check if it's running accessing http://localhost:8080/rdf4j-workbench and http://localhost:8080/rdf4j-server/
- Java code example: on https://github.com/firmao/dSimilarity/blob/main/src/main/java/com/relod/servlet/UploadSparqlrdf4j.java
