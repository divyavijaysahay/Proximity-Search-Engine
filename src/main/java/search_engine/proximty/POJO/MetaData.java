package search_engine.proximty.POJO;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.StringTokenizer;

import search_engine.proximty.tools.TextFile;

/**
 * 
 * @author Divyavijay Sahay
 *
 */
public class MetaData extends LinkedHashMap<String, Integer> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1097294904802907890L;
	private String docID;
	private int noOfTokens;
	private Set<String> set;
	private Iterator<String> iterator;
	// BufferedReader for meta data file
	private BufferedReader fileContent;

	/**
	 * @return the docID
	 */
	public String getDocID() {
		return docID;
	}

	/**
	 * @param docID
	 *            the docID to set
	 */
	public void setDocID(String docID) {
		this.docID = docID;
	}

	/**
	 * @return the noOfTokens
	 */
	public int getNoOfTokens() {
		return noOfTokens;
	}

	/**
	 * @param noOfTokens
	 *            the noOfTokens to set
	 */
	public void setNoOfTokens(int noOfTokens) {
		this.noOfTokens = noOfTokens;
	}

	public void add(String docID, int noOfTokens) {
		this.put(docID, noOfTokens);
	}

	public Iterator<String> getIterator() {
		set = this.keySet();
		iterator = set.iterator();
		return iterator;
	}

	/**
	 * Save this metaData at the given outputPath with the given metaDataName.
	 */
	public void saveFile(String metaDataName, String outputPath) {
		TextFile metadataFile = new TextFile(metaDataName, outputPath, ".txt");
		iterator = getIterator();
		String key = " ";
		StringBuilder content = new StringBuilder();
		while (iterator.hasNext()) {
			key = iterator.next();
			content.append(key);
			content.append(" ");
			content.append(this.get(key));
			if (iterator.hasNext())
				content.append("\n");
		}
		metadataFile.saveFile("", content.toString());
	}

	/**
	 * Checks of MetaData is already created or not.
	 */
	public boolean isFileCreated(String metaDataFilePath) {
		return TextFile.isFilePresent(metaDataFilePath);
	}

	/**
	 * Create a MetaData from the given file.
	 */
	public void creatFromFile(File metaDataFile) {

		try {
			fileContent = new BufferedReader(new FileReader(metaDataFile));
			String line = fileContent.readLine();

			while (line != null) {
				StringTokenizer stringTokenizer = new StringTokenizer(line,
						" ");
				String key = stringTokenizer.nextToken();
				int value = Integer.valueOf(stringTokenizer.nextToken());
				this.put(key, value);
				
				line = fileContent.readLine();
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
