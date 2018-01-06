package search_engine.proximty.POJO;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.StringTokenizer;

import search_engine.proximty.tools.TextFile;

/**
 * 
 * @author Divyavijay Sahay
 *
 */
public class Querys {

	// the string representation of the query.
	private String queryTermString;

	// the detailed representation of the each term of the query.
	private QueryTermMap queryTermSet;

	// file containing all the query for the search engine.
	private BufferedReader fileContent;

	// map representation of the query string and all the deatils of eacch of
	// the terms present in it.
	private LinkedHashMap<String, QueryTermMap> queryMap = new LinkedHashMap<String, QueryTermMap>();

	// represents a single term of the query.
	private QueryTerm queryTerm;

	// represents the path of the file containing the queries.
	private final String path;

	/**
	 * Constructor for the Querys
	 */
	public Querys(String path) {
		this.path = path;
	}

	/**
	 * Generate a map representation of all the query present in the file with their details stored in QuerTermSet.
	 * 
	 */
	public LinkedHashMap<String, QueryTermMap> generate(InvertedIndex invertedIndex) {

		File file = TextFile.getFile(path, ".txt");
		try {
			fileContent = new BufferedReader(new FileReader(file));
			String queryText = fileContent.readLine();

			while (queryText != null) {
				queryTermSet = getQueryTermSet(queryText, invertedIndex);
				queryMap.put(queryText, queryTermSet);

				queryText = fileContent.readLine();
			}

			return queryMap;

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Get the details for each term present in the given query string. Like: -Query term frequency -Document frequency
	 */
	private QueryTermMap getQueryTermSet(String queryText, InvertedIndex invertedIndex) {
		StringTokenizer query = new StringTokenizer(queryText, " ");
		QueryTermMap map = new QueryTermMap();

		while (query.hasMoreTokens()) {
			queryTermString = query.nextToken();
			int qf = getQueryTermFrequency(queryText, queryTermString);
			InvertedList invertedList = invertedIndex.get(queryTermString);
			if(invertedList != null) {
				queryTerm = new QueryTerm(queryTermString, qf, invertedList.size());
			} else {
				//Analysis
//				System.out.print(queryTermString + ", ");
				queryTerm = new QueryTerm(queryTermString, qf, 0);				
			}
			
			map.put(queryTermString,queryTerm);

		}
		//Analysis
//		System.out.println();
		
		return map;
	}

	/**
	 * Get the term frequency for the given term in the given query
	 */
	private int getQueryTermFrequency(String queryText, String queryTerm) {

		StringTokenizer queryTokenizer = new StringTokenizer(queryText, " ");
		String text = "";
		int count = 0;
		while (queryTokenizer.hasMoreTokens()) {
			text = queryTokenizer.nextToken();
			if (text.equals(queryTerm))
				count++;
		}

		return count;

	}
	
	/**
	 * Formulates all of the bigram terms within a query
	 * @param query the query to evaluate
	 * @return the bigram terms with no ranking
	 * 
	 * @author Nicholas Carugati
	 * 
	 */
	public static LinkedHashSet<String[]> bigramTerms(String query) {
		LinkedHashSet<String[]> terms = new LinkedHashSet<String[]>();
		String[] words = query.split(" ");
		if(words.length >= 2) {
			for(int i = 1; i < words.length; i++) {
				String[] pair = new String[2];
				pair[0] = words[i - 1];
				pair[1] = words[i];
				terms.add(pair);
			}
		}
		return terms;
	}

	/**
	 * returns a Set<String> representation for the queries present in the file.
	 * 
	 */
	public Set<String> getSet() {

		Set<String> querySet = new LinkedHashSet<String>();

		File file = TextFile.getFile(path, ".txt");
		try {
			fileContent = new BufferedReader(new FileReader(file));
			String queryText = fileContent.readLine();

			while (queryText != null) {
				querySet.add(queryText);
				queryText = fileContent.readLine();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return querySet;
	}

}
