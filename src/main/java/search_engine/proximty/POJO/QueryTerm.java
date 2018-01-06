package search_engine.proximty.POJO;

/**
 * 
 * @author Divyavijay Sahay
 *
 */
public class QueryTerm {

	// The string representation of the term.
	private final String text;

	// term frequency with respect to the query
	private final int queryTermFrequency;

	// document frequency for the term in the corpus.
	private final int documentFrequency;

	/**
	 * Returns the document frequency for this term
	 */
	public int getDocumentFrequency() {
		return documentFrequency;
	}

	/**
	 * Returns the string value of this term.
	 * 
	 * @return
	 */
	public String getText() {
		return text;
	}

	/**
	 * Returns the query term frequency of this term.
	 * 
	 * @return
	 */
	public int getQueryTermFrequency() {
		return queryTermFrequency;
	}

	/**
	 * Constructor for generating new query term with the given properties.
	 */
	public QueryTerm(String term, int queryTermFrequency,
			int documentFrequency) {
		this.text = term;
		this.queryTermFrequency = queryTermFrequency;
		this.documentFrequency = documentFrequency;
	}

	/**
	 * toString representation of this term.
	 */
	@Override
	public String toString() {
		return getText() + ", qfi : " + getQueryTermFrequency() + ", ni: "
				+ getDocumentFrequency();
	}

}
