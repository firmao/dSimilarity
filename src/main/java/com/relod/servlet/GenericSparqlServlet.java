package com.relod.servlet;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.eclipse.rdf4j.query.QueryLanguage;
import org.eclipse.rdf4j.query.TupleQuery;
import org.eclipse.rdf4j.query.TupleQueryResult;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.sparql.SPARQLRepository;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import web.servlet.matching.Util;
import web.servlet.matching.WIMUDataset;

/**
 * Servlet implementation class SimilarityServlet
 */
@WebServlet("/SimilarityServlet")
public class GenericSparqlServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * Default constructor.
	 */
	public GenericSparqlServlet() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		if (request.getParameter("dataset") != null) {
			Map<String, Set<String>> ret = new HashMap<String, Set<String>> ();
			String endPoint = request.getParameter("dataset");
			String query = request.getParameter("query");
//			SPARQLRepository repo = new SPARQLRepository(endPoint);
//			RepositoryConnection conn = repo.getConnection();
//			try {
//				TupleQuery tQuery = conn.prepareTupleQuery(QueryLanguage.SPARQL, query);
//				TupleQueryResult rs = tQuery.evaluate();
//				//response.getWriter().append("Results:\n");
//				Set<String> setRet = new LinkedHashSet<String>();
//				while (rs.hasNext()) {
//					setRet.add(rs.next().toString());
//				}
//				ret.put(endPoint, setRet);
//			} finally {
//				conn.close();
//			}
			ret.put(endPoint, execSparql(query, endPoint));
			Gson gson = new Gson();
			Type gsonType = new TypeToken<HashMap>(){}.getType();
	        String gsonString = gson.toJson(ret,gsonType);
	        response.getWriter().append(gsonString);
		} else if (request.getParameter("datasets") != null) {
			Map<String, Set<String>> ret = null;
			String datasets = request.getParameter("datasets");
			String str[] = datasets.split(",");
			if (str.length > 1) {
				Set<String> setDs = new LinkedHashSet<String>();
				for (String p : str) {
					setDs.add(p.trim());
				}
				try {

					ret = generateDatasetSimilarity(setDs);
					Gson gson = new Gson();
					Type gsonType = new TypeToken<HashMap>() {
					}.getType();
					String gsonString = gson.toJson(ret, gsonType);
					response.getWriter().append(gsonString);
//			        int nProps = 0;
//					for (Map.Entry<String, Set<String>> entry : ret.entrySet()) {
//						response.getWriter().append("\nDataset pair: " + entry.getKey());
//						nProps += entry.getValue().size();
//						response.getWriter().append("\nProperty/classes in common: " + entry.getValue().toString().replaceAll("p=", ""));
//						response.getWriter().append("\n\nDatasets: ").append(datasets).append("\n");
//						response.getWriter().append("\nNumber of properties and classes they share: ")
//								.append("" + nProps);
//						//System.out.println(entry.getKey() + ":" + entry.getValue());
//				    }

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

	public static Map<String, Set<String>> generateDatasetSimilarity(Set<String> datasets) {
		Map<String, Set<String>> mapExactMatch = new LinkedHashMap<String, Set<String>>();
		String[] array = datasets.stream().toArray(String[]::new);
		for (int i = 0; i < array.length; i++) {
			for (int j = i; j < array.length; j++) {
				try {
					if (array[i].equalsIgnoreCase(array[j]))
						continue;
					mapExactMatch.putAll(getExactMatches(array[i], array[j]));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		}
		if(datasets.size() > 1) {
			return getOnlyMatchProps(datasets, mapExactMatch);
		}
		return mapExactMatch;
	}

	private static Map<String, Set<String>> getOnlyMatchProps(Set<String> datasets,
			Map<String, Set<String>> mapExactMatch) {
		Map<String, Set<String>> ret = new LinkedHashMap<String, Set<String>>();
		ret.put(datasets.toString(), new HashSet<String>());
		Set<String> sRetain = new HashSet<String>();
		for (Map.Entry<String, Set<String>> entry : mapExactMatch.entrySet()) {
			sRetain.addAll(entry.getValue());
			sRetain.retainAll(entry.getValue());
//			for(String s: entry.getValue()) {
//				if(!sRetain.add(s)) {
//					ret.get(datasets.toString()).add(s);
//				}
//			}
		}
		ret.get(datasets.toString()).addAll(sRetain);
		return ret;
	}

	private static Map<String, Set<String>> getExactMatches(String source, String target)
			throws FileNotFoundException, UnsupportedEncodingException {
		final Set<String> propsSource = new LinkedHashSet<String>();
		final Set<String> propsTarget = new LinkedHashSet<String>();
		final Set<String> propsMatched = new LinkedHashSet<String>();
		final Map<String, Set<String>> mapExactMatch = new LinkedHashMap<String, Set<String>>();
		String s[] = source.split("/");
		String fSource = null;
		String fTarget = null;
		if (s.length > 2) {
			fSource = s[2] + "_" + s[s.length - 1];
		} else {
			fSource = s[s.length - 1];
		}
		String t[] = target.split("/");
		if (t.length > 2) {
			fTarget = t[2] + "_" + t[t.length - 1];
		} else {
			fTarget = t[t.length - 1];
		}
		// final String fileName = OUTPUT_DIR + "/" + fSource + "---" + fTarget +
		// "_Exact.txt";
		final String fileName = fSource + "---" + fTarget;
		propsSource.addAll(getProps(source, fSource));
		propsTarget.addAll(getProps(target, fTarget));
		if ((propsSource.size() < 1) || (propsTarget.size() < 1)) {
			return mapExactMatch;
		}

		propsSource.parallelStream().forEach(pSource -> {
			propsTarget.parallelStream().forEach(pTarget -> {
				if (pSource.equalsIgnoreCase(pTarget)) {
					propsMatched.add(pSource);
				}
			});
		});
		mapExactMatch.put(fileName, propsMatched);
		// writer.close();
		return mapExactMatch;
	}

	private static Set<String> getProps(String source, String fName) {
		// Put Claus approach here...
		// instead of execute the SPARQL at the Dataset, we query the Dataset Catalog
		// from Claus to obtain a list of properties and classes.
		// This should be faster then query the dataset, because there are some
		// datasets/Endpoints extremely slow, more than 3 minutes.
		// return getPropsClaus(source)
		String cSparqlP = "Select ?p where {?s ?p ?o}";
		String cSparqlC = "select distinct ?p where {[] a ?p}";
		Set<String> ret = new LinkedHashSet<String>();
		ret.addAll(execSparql(cSparqlP, source));
		ret.addAll(execSparql(cSparqlC, source));
		return ret;
	}

	private static Set<String> execSparql(String cSparql, String source) {
		final Set<String> ret = new LinkedHashSet<String>();
		try {
			if (source.indexOf("sparql") > 0) {
				SPARQLRepository repo = new SPARQLRepository(source);
				RepositoryConnection conn = repo.getConnection();
				try {
					TupleQuery tQuery = conn.prepareTupleQuery(QueryLanguage.SPARQL, cSparql);
					TupleQueryResult rs = tQuery.evaluate();
					while (rs.hasNext()) {
						ret.add(rs.next().toString().replaceAll("p=", "").replace("[", "").replace("]", ""));
					}
				} finally {
					conn.close();
				}
			} else {
				// ret.addAll(Util.execQueryRDFRes(cSparql, source, -1));
				String sJson = execSparqlFile(cSparql, source);
				sJson = sJson.replaceAll("=p", "");
				Type mapType = new TypeToken<Map<String, Set<String>>>(){}.getType();  
				Map<String, Set<String>> son = new Gson().fromJson(sJson, mapType);
				for (Map.Entry<String, Set<String>> entry : son.entrySet()) {
//					for(String s: entry.getValue()) {
//						ret.add("[" + s + "]");
//					}
					ret.addAll(entry.getValue());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ret;
	}

	private static String execSparqlFile(String cSparql, String source) throws InterruptedException, IOException {
		String sJson = null;

		String nURI = URLEncoder.encode(cSparql, "UTF-8");

		URL urlSearch = new URL(
				"http://141.57.11.86:8082/DatasetMatchingWeb/sparqlservlet?dataset=" + source + "&query=" + nURI);
		InputStreamReader reader = null;
		try {
			reader = new InputStreamReader(urlSearch.openStream());
		} catch (Exception e) {
			Thread.sleep(5000);
			reader = new InputStreamReader(urlSearch.openStream());
		}
		sJson = IOUtils.toString(reader);
		return sJson;
	}
}
