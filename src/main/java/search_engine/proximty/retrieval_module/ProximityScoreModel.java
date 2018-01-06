package search_engine.proximty.retrieval_module;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import search_engine.proximty.POJO.MetaData;
import search_engine.proximty.POJO.QueryTermMap;
import search_engine.proximty.POJO.Querys;
import search_engine.proximty.POJO.RankedDocuments;

/**
 * @Date : Dec 7, 2017
 *
 * @Author: Divyavijay Sahay
 */

public class ProximityScoreModel {

	private BM25 bm25;

	private final Indexer indexer;

	private QueryTermMap queryTermMap;

	// ranked documents on the basis of score.
	private RankedDocuments rankedBM25Documents;

	// ranked documents on the basis of score.
	private RankedDocuments rankedDocuments;

	private LinkedHashSet<String[]> queryPairs;

	// Meta data for the indexer.
	private final MetaData metadata;

	private static final int MAX_DISTANCE = 3;

	public ProximityScoreModel(Indexer indexer) {
		this.indexer = indexer;
		this.metadata = this.indexer.getMetaData();
		this.bm25 = new BM25(indexer);
		this.rankedDocuments = new RankedDocuments();
	}

	/**
	 * Returns the ranked documents, ranked based on their BM25 score.
	 * 
	 */
	public RankedDocuments getRankedDocuments(String queryString, QueryTermMap queryTermMap) {

		this.queryPairs = Querys.bigramTerms(queryString);

		this.queryTermMap = queryTermMap;

		this.rankedBM25Documents = this.bm25.getRankedDocuments(this.queryTermMap);

		// Put all ranked documents in final result
		this.rankedDocuments.putAll(rankedBM25Documents);

		return generateRankedDocuments();
	}

	/**
	 * Generates the ranked document set by calculating the Proximity score for each document present in the relevant
	 * document set. the documents are sorted on the basis of their BM25 score.
	 * 
	 * Query processing approach : Document-at-a-time
	 * 
	 */
	private RankedDocuments generateRankedDocuments() {

		// Calculate the score for only top 100 documents
		Set<String> rankedDocIDs = this.rankedBM25Documents.getTop(100).keySet();

		Iterator<String> iterator = rankedDocIDs.iterator();
		String docID;

		while (iterator.hasNext()) {
			docID = iterator.next();
			this.rankedDocuments.put(docID, getProximityScore(docID));
		}

		this.rankedDocuments = RankedDocuments.sort(this.rankedDocuments);

		this.rankedBM25Documents.clear();

		return this.rankedDocuments;
	}

	private Float getProximityScore(String docID) {
		float score1 = this.rankedBM25Documents.get(docID);
		float score2 = generateProximityScore(docID);
		return score1 * score2;
	}

	private Float generateProximityScore(String docID) {

		Float score = 1.0f;
		float minValue = 0;
		Iterator<String[]> iterator = this.queryPairs.iterator();

		while (iterator.hasNext()) {
			String[] pair = iterator.next();
			minValue = Math.min(this.bm25.getQWI(this.queryTermMap.get(pair[0])),
					this.bm25.getQWI(this.queryTermMap.get(pair[1])));
			float weight = calculateTermPairWeight(pair, docID);
			score += weight * minValue;
			// System.out.print(weight+" ");
		}

//		if (display_count <= 14) {
//			display_count++;
//			System.out.print(docID + " " + minValue + " " + score + "\n");
//		}
		
		if(score.isInfinite() || score.isNaN())
			return 1.0f;
		
		return score;
	}

	private float calculateTermPairWeight(String[] pair, String docID) {
		float tpiTotal = getTotaltpi(pair, docID);
		float weight = (this.bm25.getK1() + 1.0f) * (tpiTotal / (this.bm25.getK(this.metadata.get(docID)) + tpiTotal));

//		if (display_count <= 7) {
//			display_count++;
//			System.out.println(docID + " " + pair[0] + " " + pair[1] + " " + tpiTotal + " " + weight);
//		}

		return weight;
	}

	private float getTotaltpi(String[] pair, String docID) {

		float totalTPI = 0.0f;

		if (indexContains(pair) && pairContains(pair, docID)) {
			LinkedHashSet<Integer> positions1 = this.indexer.getInvertedIndex().get(pair[0]).get(docID).getPositions();
			LinkedHashSet<Integer> positions2 = this.indexer.getInvertedIndex().get(pair[1]).get(docID).getPositions();

			Iterator<Integer> iterator1 = positions1.iterator();
			Iterator<Integer> iterator2;

			int pos1, pos2, distance;
			boolean matchNotFound;

			while (iterator1.hasNext()) {

				pos1 = iterator1.next();
				matchNotFound = true;
				iterator2 = positions2.iterator();

				while (iterator2.hasNext() && matchNotFound) {
					pos2 = iterator2.next();
					distance = pos2 - pos1;

					if (distance > 0 && distance <= MAX_DISTANCE) {
						totalTPI += getTPI(distance);
						matchNotFound = false;
					} else if (distance > MAX_DISTANCE) {
						matchNotFound = false;
					}

				}
			}
		}

		return totalTPI;
	}

	private boolean pairContains(String[] pair, String docID) {
		return this.indexer.getInvertedIndex().get(pair[0]).containsKey(docID)
				&& this.indexer.getInvertedIndex().get(pair[1]).containsKey(docID);
	}

	private boolean indexContains(String[] pair) {
		return this.indexer.getInvertedIndex().containsKey(pair[0])
				&& this.indexer.getInvertedIndex().containsKey(pair[1]);
	}

	private float getTPI(int distance) {
		return (1.0f / (float) (distance * distance));
	}

}
