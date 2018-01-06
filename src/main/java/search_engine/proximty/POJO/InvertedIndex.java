package search_engine.proximty.POJO;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import search_engine.proximty.tools.TextFile;

/**
 * 
 * @author Divyavijay Sahay
 *
 */
public class InvertedIndex extends TreeMap<String, InvertedList> {

	/**
	 * Constant representing version id
	 */
	private static final long serialVersionUID = 4266405447842091463L;

	// Constant for TermFrequency tableTF
	private static final String TF = "TermFrequency";

	// Constant for DocumentFrequency tableTF
	private static final String DF = "DocumentFrequency";

	// name of the n-gram inverted index
	private String name = "";

	// Output: path of the output directory
	private String tableDirectory = "output" + File.separator + "TASK 3";

	// an object of InverteList
	private InvertedList invertedList = new InvertedList();

	// the term present in inverted index
	private String term;

	// Value representing the totalTermFrequency of a term
	private int totalTermFrequency = 0;

	// a general set of keys of a map
	private Set<String> keySet;

	// a line in the document frequency tableTF
	private StringBuilder lineDFTable = new StringBuilder();

	// map representing a TermFrequency table
	private LinkedHashMap<String, Integer> tableTF;

	// map representing a sorted table
	private static Map<String, Integer> sortedTable = new LinkedHashMap<String, Integer>();

	// count representing document frequency for a term
	private int docIDCount = 0;

	// File representing the inverted Index.
	private TextFile indexFile;

	// BufferedReader for invertedIndex file
	private BufferedReader fileContent;

	/**
	 * @return the docIDCount
	 */
	private int getDocIDCount() {
		return docIDCount;
	}

	/**
	 * @param docIDCount
	 *            the docIDCount to set
	 */
	private void setDocIDCount(int docIDCount) {
		this.docIDCount = docIDCount;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Creates a term frequency table for the caller inverted index
	 * 
	 * each line is represented as: term | its_total_term__frequency
	 */
	public void createTermFrequencyTable() {
		tableTF = new LinkedHashMap<String, Integer>();
		Iterator<String> iterator;
		keySet = this.keySet();
		int total = 0;
		Map<String, Integer> srtedTable = new LinkedHashMap<String, Integer>();

		iterator = keySet.iterator();

		while (iterator.hasNext()) {
			term = iterator.next();
			invertedList = this.get(term);
			totalTermFrequency = calculateTotalTermFrequency(invertedList);
			tableTF.put(term, totalTermFrequency);
			total += totalTermFrequency;

		}

		srtedTable = sorted(tableTF);
		keySet = srtedTable.keySet();
		iterator = keySet.iterator();
		int stop = 0;
		String key = "";
		while (iterator.hasNext()) {
			stop++;
			if (stop == 20)
				break;
			key = iterator.next();
			System.out.println(key + " " + srtedTable.get(key));
		}

		System.out.println("Total: " + total);
		saveTable(TF, tableTF);
	}

	/**
	 * Creates a document frequency table for the caller inverted index
	 * 
	 * each line is represented as: term | (docid1, docid2,...) |
	 * total_document_frequency
	 */
	public void createDocumentFrequencyTable() {
		Iterator<String> iterator;
		keySet = this.keySet();
		iterator = keySet.iterator();
		LinkedHashMap<String, Integer> dfTable = new LinkedHashMap<String, Integer>();
		Map<String, Integer> srtedTable = new LinkedHashMap<String, Integer>();
		String docIDs = "";

		while (iterator.hasNext()) {
			term = iterator.next();
			invertedList = this.get(term);
			docIDs = getDocIDs(invertedList);
			lineDFTable.append(term + " | ");
			lineDFTable.append(docIDs);
			// dfTable.put(lineDFTable.toString(), invertedList.size());
			dfTable.put(term, invertedList.size());
			lineDFTable.setLength(0);
			setDocIDCount(0);
		}
		srtedTable = sorted(dfTable);

		keySet = srtedTable.keySet();
		iterator = keySet.iterator();
		int stop = 0;
		String key = "";
		while (iterator.hasNext()) {
			stop++;
			if (stop == 20)
				break;
			key = iterator.next();
			System.out.println(key + " " + srtedTable.get(key));
		}

		saveTable(DF, dfTable);
	}

	/**
	 * save the table on disk
	 * 
	 * @param tableName
	 * @param table
	 */
	private void saveTable(String tableName, Map<String, Integer> table) {
		TextFile tableFile = new TextFile(
				getName() + "_" + tableName + "_Table", tableDirectory, ".txt");
		tableFile.saveTable(table);
	}

	/**
	 * Returns a string representing all the document ids for a term in the
	 * following format (docid1, docid2,...)
	 * 
	 * @param invertedList
	 * @return
	 */
	private String getDocIDs(InvertedList invertedList) {
		Iterator<String> iterator;
		keySet = invertedList.keySet();
		iterator = keySet.iterator();
		StringBuilder docids = new StringBuilder();

		docids.append("(");
		while (iterator.hasNext()) {
			setDocIDCount(getDocIDCount() + 1);
			docids.append(iterator.next());
			if (iterator.hasNext())
				docids.append(", ");
		}
		docids.append(")");

		return docids.toString();
	}

	/**
	 * save the sorted table on disk
	 * 
	 * @param tableName
	 * @param table
	 */
	private void saveTable(String tableName,
			LinkedHashMap<String, Integer> table) {
		sortedTable = sorted(table);
		saveTable(tableName, sortedTable);
	}

	/**
	 * Sort the LinkedHasMap based on its value.
	 * 
	 * @param pageRankScore2
	 * @return
	 */
	private Map<String, Integer> sorted(LinkedHashMap<String, Integer> table) {

		List<Map.Entry<String, Integer>> indexEntry = new LinkedList<Map.Entry<String, Integer>>(
				table.entrySet());
		Collections.sort(indexEntry,
				new Comparator<Map.Entry<String, Integer>>() {
					public int compare(Map.Entry<String, Integer> v2,
							Map.Entry<String, Integer> v1) {
						return (v1.getValue()).compareTo(v2.getValue());
					}
				});

		Map<String, Integer> sortedTable = new LinkedHashMap<String, Integer>();
		for (Map.Entry<String, Integer> entry : indexEntry) {
			sortedTable.put(entry.getKey(), entry.getValue());
		}

		return sortedTable;
	}

	/**
	 * Calculate the total term frequency for a term
	 * 
	 * @param invertedList
	 * @return
	 */
	private int calculateTotalTermFrequency(InvertedList invertedList) {
		InvertedList tempInvertedList = new InvertedList();
		tempInvertedList = invertedList;
		Iterator<String> iterator;
		int sum = 0;
		keySet = tempInvertedList.keySet();
		iterator = keySet.iterator();
		String key = "";
		while (iterator.hasNext()) {
			key = iterator.next();
			sum += tempInvertedList.get(key).getTermFrequency();
		}
		return sum;
	}

	/**
	 * Save the inverted index in the given directory
	 * 
	 * @param indexDirectory
	 */
	public void saveFile(String indexDirectory) {
		indexFile = new TextFile(getName(), indexDirectory, ".txt");
		indexFile.saveIndex(this);
	}

	/**
	 * Checks if the inverted index is already created.
	 */
	public boolean isFileCreated(String indexFilePath) {
		return TextFile.isFilePresent(indexFilePath);
	}

	/**
	 * Create an InvertedIndex from the content of the given file.
	 * 
	 */
	public void createFromFile(File invertedIndexFile) {

		try {
			fileContent = new BufferedReader(new FileReader(invertedIndexFile));
			String line = fileContent.readLine();
			
			while (line != null) {
				List<String> data = Arrays.asList(line.split(" "));
//				LinkedHashSet<String> dataSet = new LinkedHashSet<String>(data);
				Iterator<String> lineIterator = data.iterator();
				
				this.put(lineIterator.next(), getValue(lineIterator));
				line = fileContent.readLine();
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Get the inverted list from the given String representation.
	 */
	private InvertedList getValue(Iterator<String> lineIterator) {
		return new InvertedList(lineIterator);
	}

}
