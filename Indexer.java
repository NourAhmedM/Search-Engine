package crawling;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import javax.swing.text.html.HTML;

import com.mongodb.DBCollection;
import com.mongodb.DBObject;

public class Indexer 
{
    public Map<Integer, String> documentsURLs;
    private Map<Integer, Set<String> > documentsDictionary;
    public Map<String, wordValue> wordsDictionary;
    HTMLParser htmlDoc ;
    
    public Indexer()
    {
    	documentsURLs = new LinkedHashMap<Integer, String>();
    	wordsDictionary = new LinkedHashMap<String, wordValue>();
    	documentsDictionary = new LinkedHashMap<Integer,  Set<String> >();
    	
    }
    public void outInDatabase()
    {

		 DBManager db = DBManager.getinstance();
		 db.savedocumentsDictionary(documentsDictionary);
		 db.savedocumentsURLs(documentsURLs);
		 db.savewordsDictionary(wordsDictionary);
    	
    }
    public static void main(String args[]) throws IOException
    {
		Indexer indexer;
		indexer  = new Indexer();
    	indexer.getDocumentsURLs();
    	System.out.println(indexer.documentsURLs.get(0));
    	indexer.Indexing(indexer.documentsURLs);
    	indexer.outInDatabase();
    	/*for (Map.Entry<String, wordValue>  entry : indexer.wordsDictionary .entrySet())
		 {	
   		 System.out.println("word : " 
                    + entry.getKey());
   		 
   		 System.out.println("word value : ");
         entry.getValue().print();
        }*/
    }
	 public void getDocumentsURLs() throws IOException 
	 {
		 DBManager db = DBManager.getinstance();
		 DBCollection seedsCollection = db.getLinks().getCollection();
		Iterator<DBObject> objects = seedsCollection.find().iterator();
		int i=0;
		while (objects.hasNext()) {
			Map oneLink = objects.next().toMap();
		
			String link = (String) oneLink.get("link");
		
			documentsURLs.put(i, link);
			i+=1;

			}
	    	
	 }
	 void Indexing(  Map<Integer, String> documentsURLs)
	 
	 {
		 Integer totalSize = documentsURLs.size();
		 
		 for (Map.Entry<Integer, String> entry : documentsURLs.entrySet())
		 {
			 /*System.out.println("url : " 
                     + entry.getValue());*/
			 htmlDoc = new HTMLParser(entry.getValue());
			List <String> header = htmlDoc.get_header1();
			List <String> title = htmlDoc.get_title();
			List <String> body = htmlDoc.get_body();
			List <String> fullText = htmlDoc.get_fullText();
	        Set<String> headerSet = new HashSet<String>(header); 
	        headerSet.addAll(header); 
	        Set<String> titleSet = new HashSet<String>(title); 
	        titleSet.addAll(title); 
	        Set<String> bodySet = new HashSet<String>(body); 
	        bodySet.addAll(body); 
	        Set<String> fullTextSet = new HashSet<String>(fullText); 
	        fullTextSet.addAll(fullText); 
	        Integer headerCount  = 0;
	        Integer titleCount  = 0;
	        Integer bodyCount  = 0;
	        Integer fullTextCount  = 0;
			List<String>wordList = htmlDoc.get_fullText();
			documentsDictionary.put(entry.getKey(), fullTextSet);
			 for (String word : fullTextSet) 
				{
				
				     wordValue wordVal;
				     Map<Integer, List<Double>> dataOfEachUrl;
				     List<Double> priorityList;
				     headerCount = Collections.frequency(header,word);
					 titleCount = Collections.frequency(title,word);
					 bodyCount = Collections.frequency(body,word) - headerCount;
					 /*System.out.println("counts : " 
			                    + headerCount+","+titleCount+","+ bodyCount);*/
					 /////
					 double idf = 1;//getIdf(documentsURLs,word);
					 int docSize = fullText.size();
					 int occurrences = Collections.frequency(fullText,word);
					 double tdf = (double)occurrences/ (double)docSize;
					 /*System.out.println("occurrences : " 
			                    + occurrences);
					 System.out.println("docSize : " 
			                    + docSize);*/
					// float tdf =  0;//getTDF(entry.getValue(), word);
					 ////	 
					 priorityList = new ArrayList();
					 priorityList.add(tdf);
					 priorityList.add((double)titleCount); 
					 priorityList.add((double)headerCount);
					 priorityList.add((double)bodyCount);
				    
				    if (wordsDictionary.get(word) == null) 
					{
					 
					 dataOfEachUrl = new LinkedHashMap() ;
					 dataOfEachUrl.put(entry.getKey(), priorityList);
					 ////
					 wordVal = new wordValue(idf/totalSize, dataOfEachUrl);
					 ////
					 wordsDictionary.put(word, wordVal);
					}
					else 
					{
						wordVal = wordsDictionary.get(word);
						idf = wordVal.idf * totalSize ;
						dataOfEachUrl = wordVal.tdfDictionary;
						dataOfEachUrl.put(entry.getKey(), priorityList);
						wordVal.idf = (idf+1)/totalSize;// = new wordValue(idf, dataOfEachUrl);
						wordVal.tdfDictionary = dataOfEachUrl;
						wordsDictionary.replace(word, wordVal);
					
					}
				
				}
		 }
	           
	 }
	 double getIdf ( Map<Integer, String> documentsURLs, String word) 
	 {
		 HTMLParser htmlDoc ;
		 Integer totalSize = documentsURLs.size();
		 Integer targetDocCount = 0;
		 for (Map.Entry<Integer, String> entry : documentsURLs.entrySet())
		 {
			 htmlDoc = new HTMLParser(entry.getValue());
			 
			 if (htmlDoc.get_fullText().contains(word))
				 targetDocCount ++;
				 
		 }
		 return totalSize/ targetDocCount;
		 
	 }
	 double getTDF ( String url, String word) 
	 {
		 HTMLParser htmlDoc  ;
		 htmlDoc = new HTMLParser(url);
		 Integer totalSize = htmlDoc.get_fullText().size();
		 int occurrences = Collections.frequency(htmlDoc.get_fullText(),word);		 
		 return occurrences/ totalSize; 
	 }
}
