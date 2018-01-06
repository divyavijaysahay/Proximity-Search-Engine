package search_engine.proximty.retrieval_module;

import java.io.File;

import search_engine.proximty.POJO.InvertedIndex;
import search_engine.proximty.POJO.InvertedList;
import search_engine.proximty.POJO.MetaData;
import search_engine.proximty.POJO.PostingData;
import search_engine.proximty.resource.Constants;
import search_engine.proximty.tools.TextFile;

public class Indexer {

	// Constant representing Term Frequency Table
	public static final String TF = "Term Frequency Table";

	// Constant representing Document Frequency Table
	public static final String DF = "Document Frequency Table";

	private static final String CORPUS_SIZE = "CORPUS_SIZE";
	private static final String TOTAL_TOKENS = "TOTAL_NO_OF_TOKENS";

	// Output: Path of the Index Directory
	private String indexDirectory = "Index";

	// String representing file content
	private String fileContent = "";

	// variable representing InvertedIndex
	private InvertedIndex invertedIndex;

	// variable representing InvertedList of the term
	private InvertedList invertedList;

	// variable representing Posting of an InvertedList supporting Position
	private PostingData postingData;

	// StringBuilder representing n-gram term
	private StringBuilder term;

	// represents an Array of string of the file content
	private String[] contentArray;

	// represents the Document ID
	private String docID;

	// represents the number of n-gram Tokens in a file
	private int noOfTokens = 0;

	// represents the number of n-gram Tokens in a file
	private int totalNoOfTokens = 0;

	// a variable representing the MetaData of the n-gram index
	private MetaData metaData;

	// String representing the n-gram index name
	private String indexName = "index";

	// represents the n in n-gram
	private int ngramLength = 1;

	// represents the execution state
	private boolean executionState = false;

	private int corpusSize = 0;
	private String metadataName;
	private String invertedIndexFileName;
	private String metaDataFileName;
	private File invertedIndexFile;
	private File metaDataFile;

	/**
	 * @return the docID
	 */
	private String getDocID() {
		return docID;
	}

	/**
	 * @param docID
	 *            the docID to set
	 */
	private void setDocID(String docID) {
		this.docID = docID;
	}

	/**
	 * @return the ngramLength
	 */
	private int getNgramLength() {
		return ngramLength;
	}

	/**
	 * @param ngramLength
	 *            the ngramLength to set
	 */
	private void setNgramLength(int ngramLength) {
		this.ngramLength = ngramLength;
	}

	/**
	 * Constructor Initializes the invertedIndex and its MetaData
	 */
	public Indexer() {
		super();
		invertedIndex = new InvertedIndex();
		metaData = new MetaData();
	}

	/**
	 * Generate the n-gram inverted index if not already generated, otherwise generated the inverted index from the
	 * present file. It may or may not support proximity search depending on the supportPrxoimitySearch value
	 * 
	 * @param ngram
	 *            : value of n
	 * @param supportPrxoimitySearch
	 *            : true if proximity search enabled
	 */
	public void generate(int n, boolean supportPrxoimitySearch) {

		// Initialize data structures
		invertedIndex.clear();
		metaData.clear();

		// Set the name of inveretdIndex and MetaData for the given n
		invertedIndex.setName(indexName + n);
		metadataName = "MetaData_" + invertedIndex.getName();

		invertedIndexFileName = this.indexDirectory + File.separator + this.invertedIndex.getName();
		metaDataFileName = this.indexDirectory + File.separator + this.metadataName;

		if (this.isCreated()) {

			// Create inverted index from present file.

			this.invertedIndexFile = TextFile.getFile(invertedIndexFileName, Constants.EXTENSION_TXT);
			this.metaDataFile = TextFile.getFile(metaDataFileName, Constants.EXTENSION_TXT);

			this.invertedIndex.createFromFile(invertedIndexFile);
			this.metaData.creatFromFile(metaDataFile);
			this.totalNoOfTokens = this.metaData.get(TOTAL_TOKENS);
			this.corpusSize = this.metaData.get(CORPUS_SIZE);

		} else {

			// Create inverted index from corpus.

			setNgramLength(n);

			// Input: File representing the corpus directory
			File corpus = TextFile.getFile(Constants.INDEXER_DATA_PATH, "");

			executionState = true;

			// For each file in corpus update the invertedIndex
			for (File file : corpus.listFiles()) {
				setDocID(file.getName().replaceAll("(.txt)$", ""));
				fileContent = TextFile.getFileContent(file);
				generateIndexer(fileContent, supportPrxoimitySearch);
			}

			this.corpusSize = corpus.listFiles().length;

			if (this.corpusSize > 0) {
				// Save inverted index on disk
				invertedIndex.saveFile(indexDirectory);

				metaData.add(CORPUS_SIZE, this.corpusSize);
				metaData.add(TOTAL_TOKENS, this.totalNoOfTokens);
				// Save metaData on disk
				metaData.saveFile(metadataName, indexDirectory);
			}

		}

	}

	/**
	 * Checks of the indexer has already genreated the inverted index AND the meta data.
	 * 
	 */
	private boolean isCreated() {

		if (this.invertedIndex.isFileCreated(this.invertedIndexFileName + ".txt")
				&& this.metaData.isFileCreated(this.metaDataFileName + ".txt"))
			return true;

		return false;
	}

	/**
	 * Generate InvertedIndex with the terms in given file content.
	 * 
	 * @param content
	 * @param supportPrxoimitySearch
	 */
	private void generateIndexer(String content, boolean supportPrxoimitySearch) {

		int position = 0;
		noOfTokens = 0;

		contentArray = content.split(" ");

		// Update the invertedIndex with each term
		for (int i = 0; i < contentArray.length; i++) {
			// Generate the uni-gram
			term = new StringBuilder();
			term.append(contentArray[i]);

			// Generate the Bi-gram or Tri-gram depending on the ngramLength,
			// continue as uni-gram if ngramLength is 1
			for (int j = 0; j < getNgramLength() - 1; j++) {
				position = i + j + 1;
				if ((position) <= contentArray.length - 1) {
					term.append(" ");
					term.append(contentArray[position]);
				} else {
					term = null;
				}
			}

			// Update the Inverted Index for the term
			if (term != null) {
				noOfTokens++;
				updateIndexer(term.toString());
			}

			term = null;
		}
		contentArray = null;

		// Update the MetaData with the count of tokens for each file
		metaData.add(getDocID(), noOfTokens);
		totalNoOfTokens += noOfTokens;
	}

	/**
	 * Update InvertedIndex for the given term
	 * 
	 * @param term
	 *            : the n-gram term to be updated
	 */
	private void updateIndexer(String term) {

		// If term is already present than update its invertedList
		// else create a new invertedList for the term and add the document
		// with TF as 1.
		if (invertedIndex.containsKey(term)) {
			invertedList = invertedIndex.get(term);

			// If document is already present, increment TF by 1
			// else add document to invertedList with TF = 1
			if (invertedList.containsKey(getDocID())) {
				
				//Update the posting data for this docID
				postingData = invertedList.get(getDocID());
				postingData.setTermFrequency(invertedList.get(getDocID()).getTermFrequency() + 1);
				postingData.setNoOfPositions(invertedList.get(getDocID()).getNoOfPositions() + 1);
				postingData.getPositions().add(noOfTokens);
			} else {
				
				//Initialize the posting data for this docID
				postingData = new PostingData();
				postingData.setTermFrequency(1);
				postingData.setNoOfPositions(1);
				postingData.getPositions().add(noOfTokens);
			}

		} else {
			
			//Initialize the inverted list for this term.
			invertedList = new InvertedList();
			
			//Initialize the posting data for this docID.
			postingData = new PostingData();
			postingData.setTermFrequency(1);
			postingData.setNoOfPositions(1);
			postingData.getPositions().add(noOfTokens);
		}
		
		invertedList.put(getDocID(),postingData);
		invertedIndex.put(term, invertedList);
	}

	/**
	 * TASK3
	 * 
	 * create the term frequency or document frequency table depending on the table name
	 * 
	 * @param tableName
	 */
	public void createTable(String tableName) {
		boolean state = true;
		String name = "";
		if (executionState) {
			if (tableName.equals(TF)) {
				this.invertedIndex.createTermFrequencyTable();
				name = TF;
			} else if (tableName.equals(DF)) {
				this.invertedIndex.createDocumentFrequencyTable();
				name = DF;
			} else {
				state = false;
				System.out.println("Invalid table name.");
			}

			if (state)
				System.out.println(name + " Created");
		}
	}

	/**
	 * Returns the size of the inverted index.
	 */
	public int size() {
		return this.invertedIndex.size();
	}

	/**
	 * Returns the total number of tokens present in the documents of the corpus.
	 */
	public int totalDocumentLength() {
		return this.totalNoOfTokens;
	}

	/**
	 * Returns the size of the corpus.
	 */
	public int getCorpusSize() {
		return this.corpusSize;
	}

	/**
	 * Returns the MetaData for the inverted index.
	 */
	public MetaData getMetaData() {
		return this.metaData;
	}

	/**
	 * Returns the inverted index produced by this indexer.
	 */
	public InvertedIndex getInvertedIndex() {
		return this.invertedIndex;
	}

}
