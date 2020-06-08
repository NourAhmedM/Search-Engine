
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.mongodb.DBCollection;
import com.mongodb.DBObject;

public class PhraseSearch {

	public String query;
	public List<String> stemmedQuery;
	public Map<String, wordValue> wordsDictionary;
//	public Map<String, wordValue> allWordsDictionary;
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
	public PhraseSearch(String query, List<String> stemmedQuery, Map<String, wordValue> wordsDictionary) {
		this.query = query;
		this.stemmedQuery = stemmedQuery;
		this.wordsDictionary = wordsDictionary;
		this.documentsURLs = readLinksWithIndecies();
		System.out.println(stemmedQuery);
		System.out.println(wordsDictionary);
    	//this.allWordsDictionary = readWordsDic();
	}
	
	//====================================================================================================
	//-------------------------------------- the main method ---------------------------------------------
	//====================================================================================================
	public List<Integer> search() {
		String[] words = split();
		List<Integer> indices = new ArrayList();
		if (isResultExists()) {
			System.out.println("nouuuuuuuuuuuuuuuur");
			indices = stringMatch();
		}
		return indices;
	}
	
	//====================================================================================================
	//-------------------------------- split the query into words ----------------------------------------
	//====================================================================================================
	public String[] split(){
		String[] words;
		words = query.split(" ");
		return words;
	}
	
	//====================================================================================================
	//------------------------------------- is result exists ---------------------------------------------
	//====================================================================================================
	// if the size of stemmed query = size of the word dictionary then all words are in the database
	public boolean isResultExists() {
		
		int querySize = stemmedQuery.size();
		int dictionarySize = this.wordsDictionary.size();
		System.out.println(stemmedQuery);
		System.out.println(wordsDictionary);
		if(querySize != dictionarySize)
			return false;
		return true;
	}
	
	//====================================================================================================
	//--------------------- get URL indices which have all the words of the query ------------------------
	//====================================================================================================
	//returns documents indices that have all words but not ordered
	public List<Integer> getIndices(){
		List<Integer> indices = new ArrayList();
		List<List<Integer>> allIndices = new ArrayList();
		List<Integer> reference = new ArrayList();
		Map<Integer, List<Double>> tdfDictionary;
		
		//loop on word dictionary to get all urls indices in 2d array list 
		for (Entry<String, wordValue> entry : wordsDictionary.entrySet())
		{
			List<Integer> tempIndices = new ArrayList();
			tdfDictionary = entry.getValue().tdfDictionary;
			//loop on tdfDictionary to get all indices in this word value
			for (Entry<Integer, List<Double>> entry2 : tdfDictionary.entrySet())
			{
				tempIndices.add(entry2.getKey());     //get index on priority list
			}
			allIndices.add(tempIndices);
		}
		//take the first list as reference and compare the rest of lists to get the common indices in all lists
		reference = allIndices.get(0);
		boolean test = true;
		//loop on all indices of the reference list 
		for(int j=0; j<reference.size(); j++) {
			// loop on all lists to check if this index exists in all lists
			for(int i=1; i<allIndices.size(); i++) {
				if(!(allIndices.get(i).contains(reference.get(j)))) { //if not exists in one of them break the loop
					test = false;
					break;
				}
			}
			//if exists in all of the lists then add it
			if (test)
				indices.add(reference.get(j));
			
			test = true;
		}
		return indices;
	}
	
	//====================================================================================================
	//----------------------------------- get body of the page -------------------------------------------
	//====================================================================================================
	public String getBody(String url) {
		Document doc;
		String body = null;
		try {
            doc = Jsoup.connect(url).get();
            body = doc.body().text();
            return body;
        } catch (IOException e) {
            
        }
		return body;
	}
	
	//====================================================================================================
	//------------------------- match the phrase with the body of the page -------------------------------
	//====================================================================================================
	//returns the indices of the pages that have the same phrase
	public List<Integer> stringMatch() {
		List<Integer> indicies = getIndices();
		String URL, urlContent;
		List<Integer> tempIndices = new ArrayList();
		if (indicies!=null)
		{
			// loop on all indices, get the url, get the body of this url and match string
			for(int i = 0; i < indicies.size(); i++)
			{
				URL = documentsURLs.get(indicies.get(i));  // getting the URLs corresponding to the indices
				urlContent = getBody(URL);                 // getting the body of the corresponding URL
				
				if(urlContent.contains(query)) // if the phrase in the body of the url then add the index of the URL
				{
					tempIndices.add(indicies.get(i));
				}
			}
		}
		//match string
		return tempIndices;
	}
	
	public static void main(String[] args) {
		String Str = new String("-Welcome to geeksforgeeks-"); 
	        
	      // Testing if regex is present 
	     System.out.println(Str ); 
	     char first = Str.charAt(0);
	     char last = Str.charAt(Str.length()-1);
	     System.out.println(first);
	     System.out.println(last);
	     if(first == '-' && last == '-')
	    	 System.out.println("skss");
		
	}
}

