
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
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
	
	//====================================================================================================
	//-------------------------------------- database methods --------------------------------------------
	//====================================================================================================
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
	
	//====================================================================================================
	//-------------------------------------- the constructor ---------------------------------------------
	//====================================================================================================
	// in constructor, initialize wordsDictionary and documentsURLs from database
	public QueryProcessor() throws IOException { //remove string 
		
		this.documentsURLs = readLinksWithIndecies(); // this two
    	this.allWordsDictionary = readWordsDic(); // initialize from database
    	this.wordsDictionary=new LinkedHashMap<String, wordValue>();
	}
	
	//====================================================================================================
	//------------------------------- is the search is phrase search -------------------------------------
	//====================================================================================================
	public boolean isPhraseSearch(String searchQuery) {
		char first = searchQuery.charAt(0);
	    char last = searchQuery.charAt(searchQuery.length()-1);
	    
	    if(first == '-' && last == '-')
	    	return true;
	    return false;
	}
	
	//====================================================================================================
	//----------------------------- method to run the query processor ------------------------------------
	//====================================================================================================
	// this method runs the ranker to rank all links and return 10 the number of links
	public Map<Integer, ArrayList<String >> runQueryProcessor(String searchQuery,int index) throws IOException{
		boolean isPhrase = isPhraseSearch(searchQuery);
		if(isPhrase)
		{
			searchQuery=searchQuery.substring(1, searchQuery.length()-1);
		}
		this.query = splitAndStam(searchQuery);
		getDocuments();
		System.out.println( this.wordsDictionary);
		
		PhraseSearch ps = new PhraseSearch(searchQuery, this.query, this.wordsDictionary);
		List<Integer> indicesPhrase = new ArrayList();
		Map<Integer, ArrayList<String >> TenLinksWithNum=new HashMap<Integer, ArrayList<String >>();
		//checks if phrase search then get url indices
		if(isPhrase) {
			indicesPhrase = (List<Integer>) ps.search();
			if (indicesPhrase.isEmpty())      //if phrase search but no result
				return TenLinksWithNum;
		}
		List<Integer> rankedIndicies = runRanker(isPhrase, indicesPhrase);
		ArrayList<String> rankedURLs = new ArrayList<String>();
		//if there is no indices then no result return empty map
		if (rankedIndicies==null)
		{
			return TenLinksWithNum;
		}
		
		for(int i = 0; i < rankedIndicies.size(); i++)
		{
			rankedURLs.add(documentsURLs.get(rankedIndicies.get(i)));  //getting the URLs corresponding to the indices
		}
		
		DBManager db = DBManager.getinstance();
		//------------------------------------------------------------------------------------------
		int URLsSize = rankedURLs.size();
		ArrayList<String >TenLinks=new ArrayList<String >();
		for(int i=index*10-10;i<index*10;i++)
		{
			if (i >= URLsSize)
				break;
			TenLinks.add(rankedURLs.get(i));
		}
		
		TenLinksWithNum.put(URLsSize, TenLinks);
		
		return TenLinksWithNum;
	}
	
	//====================================================================================================
	//---------------------------------- select the needed documents -------------------------------------
	//====================================================================================================
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
	
	//====================================================================================================
	//----------------------------- split, stem and remove stop words ------------------------------------
	//====================================================================================================
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
	
	//====================================================================================================
	//--------------------------------------- run the ranger ---------------------------------------------
	//====================================================================================================
	public List<Integer> runRanker(boolean phrase, List<Integer> indicesPhrase) {
		if(this.wordsDictionary.isEmpty()) {
			System.out.println("no result");
			return null;
		}
		
		List<Integer> rankedIndicies;
		Relevance r = new Relevance();
		rankedIndicies = r.ranker(this.wordsDictionary, indicesPhrase); //edit this
		Collections.reverse(rankedIndicies);
		return rankedIndicies;
		
	}
	
	public static void main(String[] args) throws IOException {
		
		QueryProcessor q=new QueryProcessor();
		System.out.println(q.runQueryProcessor("-binary search-",1));
		
	}
}
