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
	public static final String INDEXER_OUTPUT_PATH = "output" + File.separator + "Indexer";
	public static final String INDEXER_DATA_PATH = "input" + File.separator + "Corpus";
	public static final String Data_Source = "input" + File.separator + "DataSource";
	public static final String INDEXER_TEST_DATA_PATH = "input" + File.separator + "test_Corpus";
	public static final String QUERY_PATH = "input" + File.separator + "Query" + File.separator + "queries";
	public static final String TEST_QUERY_PATH = "input" + File.separator + "Query" + File.separator + "test_queries";
	public static final String RESULTS_DIRECTORY = "output" + File.separator + "Results";
	public static final String WIKIPEDIA_CORPUS_PARSED = "input" + File.separator + "W-Corpus" + File.separator + "PARSED";
	public static final String WIKIPEDIA_CORPUS_RAW = "input" + File.separator + "W-Corpus" + File.separator + "RAW";
	public static final String STEMMED_CORPUS_PATH = "input" + File.separator + "Corpus";
	public static final String BM25_Index = "Index";
	public static final String BM25_FOLDER = "output" + File.separator + "BM25";
	public static final String PROXIMITY_SCORE_FOLDER = "output" + File.separator + "Proximity_Score";

	// File constants
	public static final String EXTENSION_TXT = ".txt";

}
