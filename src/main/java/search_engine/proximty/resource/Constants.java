package search_engine.proximty.resource;

import java.io.File;

/**
 * 
 * @author Divyavijay Sahay
 *
 */
public class Constants {

	// Document constants
	public static final String DOCUMENT_CONTENT = "content";
	public static final String DOCUMENT_NAME = "name";

	// path constants
	public static String INDEXER_OUTPUT_PATH = "output" + File.separator + "Indexer";
	public static String INDEXER_DATA_PATH = "input" + File.separator + "Corpus";
	public static String Data_Source = "input" + File.separator + "DataSource";
	public static String INDEXER_TEST_DATA_PATH = "input" + File.separator + "test_Corpus";
	public static String QUERY_PATH = "input" + File.separator + "Query" + File.separator + "queries";
	public static String TEST_QUERY_PATH = "input" + File.separator + "Query" + File.separator + "test_queries";
	public static String RESULTS_DIRECTORY = "output" + File.separator + "Results";
	public static String WIKIPEDIA_CORPUS_PARSED = "input" + File.separator + "W-Corpus" + File.separator + "PARSED";
	public static String WIKIPEDIA_CORPUS_RAW = "input" + File.separator + "W-Corpus" + File.separator + "RAW";
	public static String STEMMED_CORPUS_PATH = "input" + File.separator + "Corpus";
	public static String BM25_Index = "Index";

	// File constants
	public static final String EXTENSION_TXT = ".txt";

}
