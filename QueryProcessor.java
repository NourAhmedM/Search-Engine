import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;



public class QueryProcessor {
	public List<String> query;
	public Map<String, wordValue> wordsDictionary;
	public Map<Integer, String> documentsURLs;
	public ArrayList<String> allRankedURLs;
	
	// in constructor, initialize wordsDictionary and documentsURLs from database
	public QueryProcessor(String searchQuery, Map<Integer, String> documentsURLs) throws IOException { //remove string 
		this.query = splitAndStam(searchQuery);
		this.documentsURLs = documentsURLs;
    	this.wordsDictionary = new LinkedHashMap<String, wordValue>();
	}
	
	// this method runs the ranker to rank all links and return the number of links found
	int runQueryProcessor(String searchQuery) throws IOException{
		this.query = splitAndStam(searchQuery);
		List<Integer> rankedIndicies = runRanker(wordsDictionary);
		ArrayList<String> rankedURLs = new ArrayList<String>();
		for(int i = 0; i < rankedIndicies.size(); i++)
		{
			rankedURLs.add(documentsURLs.get(rankedIndicies.get(i)));  //getting the URLs corresponding to the indices
		}
		allRankedURLs = rankedURLs;
		return rankedURLs.size();
	}
	
	// method to get ten links
	ArrayList<String> getTenLinks(int index){
		ArrayList<String> rankedURLs = new ArrayList<String>();
		int startingIndex = (index-1)*10;
		for(int i = 0; i < 10; i++)
		{
			if(allRankedURLs.get(startingIndex+i) == null)
				break;
			
			rankedURLs.add(allRankedURLs.get(startingIndex+i));
		}
		return rankedURLs;
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
	
	public List<Integer> runRanker(Map<String, wordValue> wordsDictionary) {
		getDocuments(wordsDictionary);
		if(this.wordsDictionary.isEmpty()) {
			System.out.println("no result");
			return null;
		}
		
		List<Integer> rankedIndicies;
		Relevance r = new Relevance();
		rankedIndicies = r.ranker(this.wordsDictionary);
		return rankedIndicies;
		
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
		List<Integer> l = qp.runRanker(indexer.wordsDictionary);
		System.out.println(l);
		
		
	}
}