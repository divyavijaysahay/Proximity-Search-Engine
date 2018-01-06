package search_engine.proximty.tools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

/**
 * @Date : Dec 10, 2017
 *
 * @Author: Divyavijay Sahay
 */

public class Soundex {

	/**
	 * Returns the soundex: phonetic encoding for the given string
	 * 
	 */
	public static String get(String word) {

		word = word.substring(0, 1).toUpperCase() + word.substring(1);
		word = word.replaceAll("[a|e|i|o|u|y|h|w]", "-");
		word = word.replaceAll("[b|f|p|v]", "1");
		word = word.replaceAll("[c|g|j|k|q|s|x|z]", "2");
		word = word.replaceAll("[d|t]", "3");
		word = word.replaceAll("[l]", "4");
		word = word.replaceAll("[m|n]", "5");
		word = word.replaceAll("[r]", "6");
		word = word.replaceAll("(\\d)(\\1)+", "");
		word = word.replaceAll("-", "");
		if (word.length() < 4) {
			word = word.concat("0000");
		}
		return word.substring(0, 4);
	}
	
	/**
	 * Returns a map representation of soundex values and its associated terms
	 * Key : soundexValue
	 * Value : each term separated by space : "term1 term2 term3 ..."
	 * 
	 */
	public static HashMap<String, ArrayList<String>> generateMap(Set<String> indexKeys) {
		
		HashMap<String, ArrayList<String>> map = new HashMap<>();
		
		Iterator<String> iterator = indexKeys.iterator();
		String term;
		String soundexValue;
		while(iterator.hasNext()) {
			term = iterator.next();
			soundexValue = get(term);
			
			if(map.containsKey(soundexValue)) {
				ArrayList<String> terms = map.get(soundexValue);
				terms.add(term);
				map.put(soundexValue, terms);
			} else {
				ArrayList<String> termList = new ArrayList<String>();
				termList.add(term);
				map.put(soundexValue, termList);
			}
			
		}
		
		return map;
	}
	
	public static String getCorrectQuery(String query, HashMap<String, ArrayList<String>> map) {
		StringBuilder correctQuery = new StringBuilder();
		
		String[] terms = query.split(" ");
		for (int i = 0; i < terms.length; i++) {
			String soundexValue = get(terms[i]);
			if(map.containsKey(soundexValue)) {
				ArrayList<String> termList = map.get(soundexValue);
				if(termList.contains(terms[i]))
					correctQuery.append(terms[i] + " ");
				else
					correctQuery.append(getTerms(terms[i],termList));
			}
		}
		
		return correctQuery.toString();
	}
	

	private static Object getTerms(String term, ArrayList<String> termList) {

		StringBuilder terms = new StringBuilder();
		terms.append(term);
		terms.append(" ");
		
		for (int i = 0; i < termList.size(); i++) {
			terms.append(termList.get(i));
			terms.append(" ");
		}
		
		return terms.toString();
	}

	/**
	 * Test
	 */
	public static void main(String[] args) {

		System.out.println(Soundex.get("jeet"));
		System.out.println(Soundex.get("jeetomnjeetcxbr"));
		System.out.println(Soundex.get("marshmellow"));
		System.out.println(Soundex.get("marshmallow"));
		System.out.println(Soundex.get("brimingham"));
		System.out.println(Soundex.get("birmingham"));
		System.out.println(Soundex.get("poiner"));
		System.out.println(Soundex.get("pointer"));

	}

}
