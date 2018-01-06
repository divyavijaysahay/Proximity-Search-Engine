package search_engine.proximty.retrieval_module;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import search_engine.proximty.POJO.MetaData;
import search_engine.proximty.POJO.QueryTerm;
import search_engine.proximty.POJO.QueryTermMap;
import search_engine.proximty.POJO.RankedDocuments;

public class BM25 {

	// ranked documents on the basis of BM25 score.
	private RankedDocuments rankedDocuments;

	// Details of the query
	private QueryTermMap queryTermMap;

	// Relevant documents set for the given query.
	private LinkedHashSet<String> relevantDocumentSet;

	// Indexer for the search engine
	private Indexer indexer = null;

	// Corpus Size
	private final float N;

	// Average document length
	private final float avdl;

	// no of relevant documents for the given term. 0 since no relevance info
	// present.
	private final float ri = 0.0f;

	// no of total relevant documents. 0 since no relevance info present.
	private final float R = 0.0f;

	// Meta data for the indexer.
	private final MetaData metadata;

	// k1 constant
	private float k1 = 1.2f;

	// k2 constant
	private float k2 = 100.0f;

	// k3 constant for proximity score
	private float k = 2.0f;
	// b constant
	private float b = 0.75f;

	// iterator for query term set.
	private Iterator<String> queryTermSetIterator;

	private boolean enableProximityScore = false;

	/**
	 * Getters and Setters for: k1, k2, b , K
	 */
	public float getK1() {
		return k1;
	}

	public void setK1(float k1) {
		this.k1 = k1;
	}

	public float getK2() {
		return k2;
	}

	public void setK2(float k2) {
		this.k2 = k2;
	}

	public float getB() {
		return b;
	}

	public void setB(float b) {
		this.b = b;
	}

	public float getK3() {
		return k;
	}

	public void setK3(float k3) {
		this.k = k3;
	}

	public float getK(int dl) {
		if (!this.enableProximityScore)
			return (getK1() * ((1 - getB()) + (getB() * ((float) dl / this.avdl))));
		else
			return (getK3() * ((1 - getB()) + (getB() * ((float) dl / this.avdl))));
	}

	/**
	 * Constructor for BM25 - the values N, avdl, document frequency for each term were generated during index creation
	 * itself. - query term frequency was generated while processing each query form the query file. - term frequency
	 * and K is generated while calculating the score for the document.
	 */
	public BM25(Indexer indexer) {
		this.indexer = indexer;
		this.N = this.indexer.getCorpusSize();
		this.avdl = calculateAvdl(this.indexer.totalDocumentLength(), this.N);
		this.metadata = this.indexer.getMetaData();
		this.rankedDocuments = new RankedDocuments();

		// Display values for BM25
//		System.out.println("index : " + this.indexer.size());
//		System.out.println("N : " + this.N);
//		System.out.println("total doc length:" + this.indexer.totalDocumentLength());
//		System.out.println("avdl : " + this.avdl);
//		System.out.println("Metadata : " + this.metadata.size());
	}

	/**
	 * Constructor for BM25 Supporting proximity search score- the values N, avdl, document frequency for each term were
	 * generated during index creation itself. - query term frequency was generated while processing each query form the
	 * query file. - term frequency and K is generated while calculating the score for the document.
	 */
	public BM25(Indexer indexer, boolean enableProximityScore) {
		this(indexer);
		this.enableProximityScore = enableProximityScore;
		setB(0.9f);
//		setK2(1000.0f);
	}

	/**
	 * Returns the value of Average document length of all the documents present in the coprus: avdl
	 * 
	 */
	private float calculateAvdl(int totalDocumentLength, float corpusSize) {
		return ((float) totalDocumentLength / corpusSize);
	}

	/**
	 * Returns the ranked documents, ranked based on their BM25 score.
	 * 
	 */
	public RankedDocuments getRankedDocuments(QueryTermMap queryTermSet) {

		this.queryTermMap = queryTermSet;
		this.relevantDocumentSet = getRelevantDocumentSet(this.queryTermMap);

//		System.out.println("Total Relevant Documents : " + this.relevantDocumentSet.size());

		return generateRankedDocuments();
	}

	/**
	 * Generates the ranked document set by calculating the BM25 score for each document present in the relevant
	 * document set. the documents are sorted on the basis of their BM25 score.
	 * 
	 * Query processing approach : Document-at-a-time
	 * 
	 */
	private RankedDocuments generateRankedDocuments() {

		for (String docID : this.relevantDocumentSet) {
			this.rankedDocuments.put(docID, getBM25Score(docID));
		}

		this.rankedDocuments = RankedDocuments.sort(this.rankedDocuments);

		return this.rankedDocuments;
	}

	/**
	 * Returns the BM25 score for the given docID
	 */
	private float getBM25Score(String docID) {
		float score = 0.0f;
		int fi = 0;
		float k = getK(this.metadata.get(docID));

		Set<String> queryTermSet = this.queryTermMap.keySet();
		Iterator<String> queryTermStringIterator = queryTermSet.iterator();
		QueryTerm queryTerm;

		while (queryTermStringIterator.hasNext()) {
			queryTerm = this.queryTermMap.get(queryTermStringIterator.next());
			// Calculate value of fi, if present in the document else 0.
			if (queryTerm.getDocumentFrequency() != 0 && termContainsDocument(queryTerm, docID)) {
				fi = this.indexer.getInvertedIndex().get(queryTerm.getText()).get(docID).getTermFrequency();
			} else
				fi = 0;


			float value = calculateBM25(queryTerm, fi, k);
			score += value;
		}

		return score;
	}

	/**
	 * Checks if the document was present in the inverted list of the given term
	 */
	private boolean termContainsDocument(QueryTerm queryTerm, String docID) {
		return this.indexer.getInvertedIndex().get(queryTerm.getText()).containsKey(docID);
	}

	/**
	 * Calculate the BM25 score.
	 */
	private float calculateBM25(QueryTerm queryTerm, int fi, float k) {

		float score = 0.0f;

		if (!this.enableProximityScore) {
			score = (float) (Math.log(getIDFWeight(queryTerm)) * getDocumentWeight(fi, k)
					* getQueryTermWeight(queryTerm.getQueryTermFrequency()));
		} else {
			score = getDocumentWeight(fi, k) * getQWI(queryTerm);
		}

		return score;
	}

	/**
	 * Calculates qwi for the BM25 score using proximity enabled search.
	 * 
	 */
	public float getQWI(QueryTerm queryTerm) {
		return (getQueryTermWeight(queryTerm.getQueryTermFrequency())
				* getProximityIDF(queryTerm.getDocumentFrequency()));
	}

	/**
	 * IDF weight for this query term.
	 * 
	 * Proximity search is enabled or not for this BM25.
	 */
	private float getProximityIDF(int documentFrequency) {
		return (float) Math.log((this.N - (float) documentFrequency) / (float) documentFrequency);
	}

	/**
	 * Calculate the query term weight of the BM25 score.
	 * 
	 * The value depends on whether proximity search is enabled or not for this BM25.
	 */
	private float getQueryTermWeight(int qfi) {
		float numerator = 0.0f;
		float denominator = 0.0f;

		if (!this.enableProximityScore)
			numerator = ((this.k2 + 1.0f) * (float) qfi);
		else
			numerator = (float) qfi;

		denominator = (this.k2 + (float) qfi);

		return (numerator / denominator);
	}

	/**
	 * Calculate the term frequency weight of the BM25 score.
	 */
	private float getDocumentWeight(int fi, float k) {
		float numerator = 0.0f;
		float denominator = 0.0f;

		numerator = ((this.k1 + 1.0f) * fi);
		denominator = (k + (float) fi);

		return (numerator / denominator);
	}

	/**
	 * Calculate the idf weight of the BM25 score.
	 * 
	 * @param queryTerm
	 * @return
	 */
	private float getIDFWeight(QueryTerm queryTerm) {
		float numerator = 0.0f;
		float denominator = 0.0f;
		int ni = queryTerm.getDocumentFrequency();

		numerator = ((this.ri + 0.5f) / (this.R - this.ri + 0.5f));
		denominator = ((ni - this.ri + 0.5f) / (this.N - ni - this.R + this.ri + 0.5f));

		return (numerator / denominator);
	}

	/**
	 * Returns the LinekdHashSet containing the relevant documents. Generated by adding all the document present in the
	 * inverted list of each term of the query in the LinekdHashSet.
	 */
	private LinkedHashSet<String> getRelevantDocumentSet(QueryTermMap queryTermMap) {

		LinkedHashSet<String> documentSet = new LinkedHashSet<String>();
		queryTermSetIterator = queryTermMap.keySet().iterator();

		while (queryTermSetIterator.hasNext()) {
			QueryTerm queryTerm = queryTermMap.get(queryTermSetIterator.next());
			if (queryTerm.getDocumentFrequency() != 0)
				documentSet.addAll(this.indexer.getInvertedIndex().get(queryTerm.getText()).keySet());
			this.indexer.getInvertedIndex().containsKey(queryTerm.getText());
		}

		return documentSet;
	}

}
