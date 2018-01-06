# Welcome to Proximity Search Engine

**This search engine is based on proximity dependent retrieval model.**

Due to which the pages having more number of the query term pairs closer or within a specified distance will be ranked higher than the pages having more number of individual query terms.

Here, the query term pairs will be created in the order of their occurrence in the original query.

This search engine is implemented in Java.

## Retrieval Model

**BM25 or Okapi** is used as the base retrieval model for the **proximity retrieval model**.
The proximity retrieval model designed here is influenced by the retrieval model presented in the following research work:

*Rasolofo, Yves, and Jacques Savoy. “Term Proximity Scoring for Keyword-Based Retrieval Systems.”
Lecture Notes in Computer Science Advances in Information Retrieval, 2003, pp. 207–218.,
doi:10.1007/3-540-36618-0_15*

Must Read the [Research Paper](http://citeseerx.ist.psu.edu/viewdoc/download?doi=10.1.1.174.8359&rep=rep1&type=pdf) for better understanding.

## How to run this project

1. Clone/Download the project.
2. Import this maven project in eclipse or any IDE supporting Java.
3. Open the App.java file present under Run package.
4. Run the program.

**Note:**
1. Make sure you have your downloaded **html** pages under the **input/Corpus** folder.
2. Add the queries in the **queries.txt** file present in **input/Query** folder.

## How to use this search engine in your own project

1. Import the project as specified above.
2. Use the following code in you Java file.

```
	Querys querys = new Querys(Constants.QUERY_PATH);
	
	SearchEngine searchEngine = new SearchEngine(RetrievalModel.PROXIMITY_SCORE);
	searchEngine.setDisplayResults(true);
	searchEngine.search(querys);
```

Enjoy!

Any kind of feedback will be highly appreciated.


## [License](https://github.com/divyavijaysahay/SearchEngine/blob/master/LICENSE)
