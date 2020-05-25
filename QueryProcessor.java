import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.io.File;
import java.io.FileNotFoundException;

import org.jsoup.Jsoup;


public class QueryProcessor {
	public List<String> query;
	public Map<String, wordValue> wordsDictionary;
	public Map<Integer, String> documentsURLs;
	
	public QueryProcessor(String searchQuery, Map<Integer, String> documentsURLs, Map<String, wordValue> wordsDictionary) throws IOException {
		this.query = splitAndStam(searchQuery);
		this.documentsURLs = documentsURLs;
    	this.wordsDictionary = wordsDictionary;
	}
	
	public List<String> splitAndStam(String searchQuery) throws IOException{
		StopWordsRemover remover = new StopWordsRemover();
		String[] splittedQuery;
//		Object[] returnedQuery;
		List<String> queryList = new ArrayList();
		int c = 0;
		Stemmer stemmer = new Stemmer();
		splittedQuery = searchQuery.split(" ");
		for(int i = 0; i < splittedQuery.length; i++) {
			splittedQuery[i] = stemmer.stemTerm(splittedQuery[i]);
			if(remover.setup(splittedQuery[i]) != "")
			{
				queryList.add(splittedQuery[i]);
			}
		}
//		returnedQuery = queryList.toArray();
		return queryList;
	}
	
	public void runRanker() {
//		Relevance r = new Relevance(this.query);
	}
	
	public static void main(String[] args) throws IOException {
		
		Indexer indexer;
		indexer  = new Indexer();
    	indexer.getDocumentsURLs();
    	indexer.Indexing(indexer.documentsURLs);
    	
		String s = "test this testing to be tested";
		QueryProcessor qp = new QueryProcessor(s, indexer.documentsURLs, indexer.wordsDictionary);
		List<String> q = qp.query;
//		qp.getDataFromDatabase();
		for(int i = 0; i < q.size(); i++) {
			System.out.println(q.get(i)); 
		}
		
		
		
	}
}