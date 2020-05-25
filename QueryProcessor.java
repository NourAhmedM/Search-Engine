import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;



public class QueryProcessor {
	public List<String> query;
	public Map<String, wordValue> wordsDictionary;
	public Map<Integer, String> documentsURLs;
	
	public QueryProcessor(String searchQuery, Map<Integer, String> documentsURLs) throws IOException {
		this.query = splitAndStam(searchQuery);
		this.documentsURLs = documentsURLs;
    	this.wordsDictionary = new LinkedHashMap<String, wordValue>();;
	}
	
	// method to get documents contains words in the query
	public void getDocuments(Map<String, wordValue> wordsDictionary) { // takes map from indexer for now
		for(int i = 0; i < query.size(); i++) {
			//if the word in database put it in dictionary
			if(wordsDictionary.containsKey(query.get(i)))
				this.wordsDictionary.put(query.get(i), wordsDictionary.get(query.get(i))); 
			//if not print it
			else
				System.out.println("this word in not exsist: " + query.get(i)); 
				
		}
	}
	public List<String> splitAndStam(String searchQuery) throws IOException{
		StopWordsRemover remover = new StopWordsRemover();
		String[] splittedQuery;
		List<String> queryList = new ArrayList<String>();
		Stemmer stemmer = new Stemmer();
		
		// split the phrase into words
		splittedQuery = searchQuery.split(" ");
		
		for(int i = 0; i < splittedQuery.length; i++) {
			splittedQuery[i] = stemmer.stemTerm(splittedQuery[i]);  // stem each word
			if(remover.setup(splittedQuery[i]) != "")               // remove stop words
			{
				// If this string is not present in newList add it
	            if (!queryList.contains(splittedQuery[i]))
	            	queryList.add(splittedQuery[i]); 
	            
			}
			
		}
		return queryList;
	}
	
	public void runRanker(Map<String, wordValue> wordsDictionary) {
		getDocuments(wordsDictionary);
		if(this.wordsDictionary.isEmpty()) {
			System.out.println("no result"); 
		}
		else {
			List<Integer> rankedIndicies;
			Relevance r = new Relevance();
			rankedIndicies = r.ranker(this.wordsDictionary);
			System.out.println(rankedIndicies); 
		}
	}
	
	public static void main(String[] args) throws IOException {
		
		Indexer indexer;
		indexer  = new Indexer();
    	indexer.getDocumentsURLs();
    	indexer.Indexing(indexer.documentsURLs);
    	
		String s = "uploaded results golden halfed energy parameters methods";
		QueryProcessor qp = new QueryProcessor(s, indexer.documentsURLs);
		List<String> q = qp.query;
		for(int i = 0; i < q.size(); i++) {
			System.out.println(q.get(i)); 
		}
		qp.runRanker(indexer.wordsDictionary);
		
		
	}
}