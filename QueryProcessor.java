package crawling;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.mongodb.DBCollection;
import com.mongodb.DBObject;



public class QueryProcessor {
	public Map<String, wordValue> allWordsDictionary;
	public List<String> query;
	public Map<String, wordValue> wordsDictionary;
	public Map<Integer, String> documentsURLs;
	
	public Map<Integer, String> readLinksWithIndecies()
	{
		Map<Integer, String> documentsURLs= new LinkedHashMap<Integer, String>();
		DBManager db = DBManager.getinstance();
		DBCollection seedsCollection = db.getdocumentsURLs().getCollection();
		Iterator<DBObject> objects = seedsCollection.find().iterator();
		while (objects.hasNext()) {
			Map onelink = objects.next().toMap();
	
			String link = (String) onelink.get("link");
			int index = (Integer) onelink.get("index");
	
			documentsURLs.put(index, link);
	
		}
		return documentsURLs;
	}
	
	public Map<String, wordValue> readWordsDic()
	{
		Map<String, wordValue> wordsDictionary=new LinkedHashMap<String, wordValue>();
		
		DBManager db = DBManager.getinstance();
		DBCollection seedsCollection = db.getwordsDictionary().getCollection();
		Iterator<DBObject> objects = seedsCollection.find().iterator();
		while (objects.hasNext()) {
			Map oneword = objects.next().toMap();
	
			String word = (String) oneword.get("word");
			
			double idfd = (double) oneword.get("idf");
			double idf=(double)idfd;
			ArrayList<Integer>linksIndecies=(ArrayList<Integer>)oneword.get("linksIndecies");
			ArrayList<List<Double>>listCorespondsToIndecies=(ArrayList<List<Double>>)oneword.get("listCorespondsToIndecies");
	
			Map<Integer, List<Double> >wordVaueMap=new LinkedHashMap<Integer, List<Double> >();
			for(int i=0;i<linksIndecies.size();i++)
			{
				wordVaueMap.put(linksIndecies.get(i), listCorespondsToIndecies.get(i));
				
			}
			wordValue wordvlaue=new wordValue(idf,wordVaueMap);
			wordsDictionary.put(word, wordvlaue);
	
		}
		return wordsDictionary;
	}
	// in constructor, initialize wordsDictionary and documentsURLs from database
	public QueryProcessor() throws IOException { //remove string 
		
		this.documentsURLs = readLinksWithIndecies(); // this two
    	this.allWordsDictionary = readWordsDic(); // initialize from database
    	this.wordsDictionary=new LinkedHashMap<String, wordValue>();
	}
	
	// this method runs the ranker to rank all links and return the number of links found
	int runQueryProcessor(String searchQuery) throws IOException{
		this.query = splitAndStam(searchQuery);
		getDocuments();
		List<Integer> rankedIndicies = runRanker();
		ArrayList<String> rankedURLs = new ArrayList<String>();
		if (rankedIndicies!=null)
		{
			for(int i = 0; i < rankedIndicies.size(); i++)
			{
				rankedURLs.add(documentsURLs.get(rankedIndicies.get(i)));  //getting the URLs corresponding to the indices
			}
		}
		DBManager db = DBManager.getinstance();
		db.saveSearchQueryLinks( rankedURLs);
		return rankedURLs.size();
	}
	
	// method to get ten links
	ArrayList<String> getTenLinks(int index){
		ArrayList<String> rankedURLs ;
		DBManager db = DBManager.getinstance();
		rankedURLs=db.getTenOfSearchQueryLinks(index);
		return rankedURLs;
	}
	
	// method to get documents contains words in the query
	public void getDocuments() { // takes map from indexer for now, remove map
		for(int i = 0; i < query.size(); i++) {
			//if the word in database put it in dictionary
			if(allWordsDictionary.containsKey(query.get(i)))
			{
				String s=query.get(i);
				wordValue w=allWordsDictionary.get(query.get(i));
 				
				this.wordsDictionary.put(s,w ); 
			}
			//if not print it
			else
				System.out.println("this word in not exsist: " + query.get(i)); 
				
		}
	}
	
	// method to split the query and stem it
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
		System.out.print(queryList);
		return queryList;
	}
	
	public List<Integer> runRanker() {
		getDocuments();
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
		
		QueryProcessor q=new QueryProcessor();
		q.runQueryProcessor("sql");
		
	}
}