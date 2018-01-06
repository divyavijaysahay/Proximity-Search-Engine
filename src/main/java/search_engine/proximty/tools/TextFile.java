package search_engine.proximty.tools;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import search_engine.proximty.POJO.InvertedIndex;
import search_engine.proximty.POJO.InvertedList;
import search_engine.proximty.POJO.PostingData;
import search_engine.proximty.POJO.RankedDocuments;
import search_engine.proximty.resource.Constants;
import search_engine.proximty.resource.RetrievalModel;

/**
 * 
 * @author Divyavijay Sahay
 *
 */
public class TextFile {

	// FILE_NAME : Name of the file created.
	private String FILE_NAME;

	// FILE_PATH : Path of the output directory.
	private String FILE_PATH = "output";

	// fileWriter : Writer for adding data to file.
	private FileWriter fileWriter;

	// writer : BufferedWriter for editing the file and entering data in it
	private BufferedWriter writer;

	// file : It represents the File accessed in the class.
	private File file;

	/**
	 * Constructor for the TextFile
	 * 
	 * @param FILE_NAME
	 *            : the name of the file.
	 * @param extension
	 *            : either .html or .txt
	 */
	public TextFile(String FILE_NAME, String FILE_PATH, String extension) {
		this.FILE_PATH = FILE_PATH;
		this.FILE_NAME = FILE_NAME + extension;
		file = createFile();
	}

	/**
	 * Initializes the writer for editing the files
	 * 
	 * @param file
	 *            : the file to be edited.
	 */
	public void initialiseWriter(File file) {
		try {
			fileWriter = new FileWriter(file, true);
		} catch (IOException e) {
			e.printStackTrace();
		}
		writer = new BufferedWriter(fileWriter);
	}

	/**
	 * Closes the open writer & fileWriter linked to a file
	 * 
	 * @param writer
	 *            : the BufferedWriter too be closed.
	 */
	public void close(BufferedWriter writer) {
		try {

			if (writer != null)
				writer.close();

			if (fileWriter != null)
				fileWriter.close();

		} catch (IOException ex) {

			ex.printStackTrace();

		}
	}

	/**
	 * Adds all the given URLs to a file.
	 * 
	 * @param urlList
	 *            : The document containing the URLs
	 * @return : True is all URLs have been added.
	 */
	public boolean addURLToFile(String urlList) {

		if (file != null) {
			try {
				StringTokenizer urlTokenizer = new StringTokenizer(urlList.replaceAll(", ", " "), "[] ");
				initialiseWriter(file);

				// add first URL to the file.
				writer.write(urlTokenizer.nextToken().toString());

				// add rest URLs with a new line
				while (urlTokenizer.hasMoreTokens()) {

					writer.newLine();
					writer.write(urlTokenizer.nextToken().toString());
				}
				return true;
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				close(writer);
			}
		}

		return false;
	}

	/**
	 * It saves a file with the name:URL and data:document
	 * 
	 * @param URL
	 *            : the name of the file
	 * @param content
	 *            : the data of the file
	 * @return : True if the file is successfully edited.
	 */
	public boolean saveFile(String URL, String content) {

		if (file != null) {
			try {
				initialiseWriter(file);
				writer.write(content.toString());
				writer.flush();
				return true;
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				close(writer);
			}
		} else {
			System.out.println("Fill is null for URL: " + URL);
		}
		return false;
	}

	public void appendFile(String content) {
		if (file != null) {
			try {
				initialiseWriter(file);
				writer.write(content);
				writer.flush();
			} catch (Exception e) {
				System.out.println("File append failed.");
				e.printStackTrace();
			} finally {
				close(writer);
			}
		} else {
			System.out.println("File append failed. File is null.");
		}
	}

	/**
	 * Creates a new file.
	 * 
	 * @return the file created
	 */
	private File createFile() {

		File file = null;
		File directory = new File(FILE_PATH);

		if (!directory.exists())
			directory.mkdirs();

		if (directory.exists()) {
			file = new File(directory + File.separator + encodeFileName(this.FILE_NAME));

			if (file.exists()) {
				file.delete();
			}

			try {
				if (file.createNewFile()) {
					return file;
				}
			} catch (IOException e) {
				e.printStackTrace();
				System.out.println("File creation failed.");
			}

		} else {
			System.out.println("Directory not created for: " + directory.toString());
		}

		return null;
	}

	private static String encodeFileName(String file_name) {
		if (file_name.contains("/"))
			return file_name.replaceAll("/", "%");
		else
			return file_name;
	}

	public static File getFile(String filename, String extension) {
		return (new File(filename + extension));
	}

	public static String getFileContent(File file) {
		try {
			return (new String(Files.readAllBytes(file.toPath())));
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Unable to read the given file: " + file.getPath());
		}
		return null;
	}

	private static String decodeFileName(String filename) {

		if (filename.contains("%"))
			return filename.replaceAll("%", "/");

		return filename;
	}

	public void saveGraph(LinkedHashSet<String> graphValues) {
		Iterator<String> iterator = graphValues.iterator();
		while (iterator.hasNext()) {
			if (file != null) {
				try {
					initialiseWriter(file);
					writer.write(iterator.next());
					if (iterator.hasNext())
						writer.newLine();
					writer.flush();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					close(writer);
				}
			}
		}

	}

	public void saveTable(Map<String, Integer> table) {

		Set<String> keySet = table.keySet();
		int counter = 0;
		Iterator<String> iterator = keySet.iterator();
		String key = "";
		StringBuilder content = new StringBuilder();
		while (iterator.hasNext()) {
			counter++;
			key = iterator.next();
			content.append(key + " | " + table.get(key));
			if (iterator.hasNext())
				content.append("\n");

			if (counter == 100000) {
				saveFile("", content.toString());
				content.setLength(0);
				counter = 0;
			}

		}
		if (content != null) {
			saveFile("", content.toString());
			content.setLength(0);
		}

	}

	/**
	 * Save the inverted index
	 */
	public void saveIndex(InvertedIndex invertedIndex) {

		Set<String> keySet = invertedIndex.keySet();
		int counter = 0;
		Iterator<String> iterator = keySet.iterator();
		String key = "";
		StringBuilder content = new StringBuilder();
		InvertedList invertedList;

		while (iterator.hasNext()) {
			counter++;
			key = iterator.next();
			invertedList = invertedIndex.get(key);
			content.append(key + " ");
			content.append(getInvertedListContent(invertedList));
			// System.out.print(getInvertedListContent(invertedList) + " ");
			if (iterator.hasNext())
				content.append("\n");

			if (counter == 100000) {
				saveFile("", content.toString());
				content.setLength(0);
				counter = 0;
			}

		}
		if (content != null) {
			saveFile("", content.toString());
			content.setLength(0);
		}

	}

	private StringBuilder getInvertedListContent(InvertedList invertedList) {
		Set<String> keys = invertedList.keySet();
		Iterator<String> iterator = keys.iterator();
		String docID;
		PostingData postingData;
		StringBuilder invertedListContent = new StringBuilder();
		while (iterator.hasNext()) {
			docID = iterator.next();
			invertedListContent.append(docID + " ");
			postingData = invertedList.get(docID);
			invertedListContent.append(postingData.getTermFrequency());
			invertedListContent.append(" ");
			invertedListContent.append(postingData.getNoOfPositions());
			invertedListContent.append(" ");
			invertedListContent.append(getPositionsContent(postingData.getPositions()));
		}
		return invertedListContent;
	}

	private String getPositionsContent(LinkedHashSet<Integer> positions) {
		Iterator<Integer> iterator = positions.iterator();
		StringBuilder positionsContent = new StringBuilder();
		while (iterator.hasNext()) {
			positionsContent.append(iterator.next());
			positionsContent.append(" ");
		}
		return positionsContent.toString();
	}

	/**
	 * Save the results of the search engine. Name of the file depends on the given Retrieval model.
	 */
	public void saveResults(RankedDocuments documents, RetrievalModel model, int queryID) {
		Set<String> keySet = documents.keySet();
		Iterator<String> iterator = keySet.iterator();
		String key;
		StringBuilder line = new StringBuilder();

		while (iterator.hasNext()) {
			key = iterator.next();
			line.append(key + "\t" + documents.get(key));

			if (iterator.hasNext())
				line.append("\n");
		}

		saveFile("", line.toString());
	}

	/**
	 * Checks of the file is present at the path represented by the given string.
	 */
	public static boolean isFilePresent(String indexFilePath) {
		File testFile = new File(indexFilePath);
		if (testFile.exists() && !testFile.isDirectory())
			return true;

		return false;
	}

	public static File[] getFiles(RetrievalModel model) {

		if (model.equals(RetrievalModel.BM25))
			return new File(Constants.BM25_FOLDER).listFiles();
		else
			return new File(Constants.PROXIMITY_SCORE_FOLDER).listFiles();
	}

	public static File getModelQueryFile(RetrievalModel model, String queryId) {

		if (model.equals(RetrievalModel.BM25))
			return getFile(Constants.BM25_FOLDER + File.separator + queryId, ".txt");
		else
			return getFile(Constants.PROXIMITY_SCORE_FOLDER + File.separator + queryId, ".txt");

	}

}
