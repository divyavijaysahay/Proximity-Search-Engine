package run;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

import search_engine.proximty.POJO.Querys;
import search_engine.proximty.resource.Constants;
import search_engine.proximty.resource.RetrievalModel;
import search_engine.proximty.retrieval_module.SearchEngine;


public class App {

	// Start time of the execution
	private static long startTime = System.currentTimeMillis();

	public static void main(String[] args) {

		// Querys containing all the query present in the file at the location
		// specified by the Query_path constant.
		Querys querys = new Querys(Constants.QUERY_PATH);


		System.out.println("***************************************");
		System.out.println("  SEARCH ENGINE, PROXIMITY SCORE RETRIEVAL MODEL  ");
		System.out.println("***************************************");
		runSearchEngine(querys, RetrievalModel.PROXIMITY_SCORE);

	}

	private static void runSearchEngine(Querys querys, RetrievalModel model) {

		SearchEngine searchEngine = new SearchEngine(model);
		searchEngine.setDisplayResults(true);
		searchEngine.search(querys);

		displayTime();
	}

	/**
	 * Display the execution time till present.
	 */
	private static void displayTime() {
		System.out.println();
		System.out.println("Execution time: " + ((long) System.currentTimeMillis() - startTime) / 1000f + " sec");
	}
}
