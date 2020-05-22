import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import javax.swing.text.html.HTML;

public class Indexer 
{
//get the documents out of the URLS
    private Map<Integer, String> documentsURLs;
    private Map<Integer, Set<String> > documentsDictionary;
    private Map<String, wordValue> wordsDictionary;
    HTMLParser htmlDoc ;
    
    public Indexer()
    {
    	documentsURLs = new LinkedHashMap<Integer, String>();
    	wordsDictionary = new LinkedHashMap<String, wordValue>();
    	
    }
    public static void main(String args[]) throws IOException
    {
		Indexer indexer;
		indexer  = new Indexer();
    	indexer.getDocumentsURLs();
    	indexer.Indexing(indexer.documentsURLs);
    	for (Map.Entry<String, wordValue>  entry : indexer.wordsDictionary .entrySet())
		 {	
   		 System.out.println("word : " 
                    + entry.getKey());
   		 System.out.println("word value : ");
         entry.getValue().print();
  	     }
    }
	 public void getDocumentsURLs() throws IOException 
	 {
	        /* TODO : GET Documents URLS From DB */
	    	File file = new File("assets/urls.txt");   
	    	Scanner sc = new Scanner(file , "UTF-8");     //file to be scanned
	    	int temp = 0;  
	    	
	    	while(sc.hasNextLine())  
	    	{  String url  = sc.nextLine();
	    		documentsURLs.put(temp,url);
	    		temp++;
	    		
	    	}
	    	
	 }
	 void Indexing(  Map<Integer, String> documentsURLs)
	 
	 {
		 
		 for (Map.Entry<Integer, String> entry : documentsURLs.entrySet())
		 {
			 System.out.println("url : " 
                     + entry.getValue());
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
	        System.out.println("title : " 
                    + titleSet);
	        System.out.println("body : " 
                    + bodySet);
	        System.out.println("header : " 
                    + headerSet);
			 List<String>wordList = htmlDoc.get_fullText();
			 System.out.println("word : " 
                     + wordList.get(1));
			 for (String word : fullTextSet) 
				{
				
				     wordValue wordVal;
				     Map<Integer, List<Float>> dataOfEachUrl;
				     List<Float> priorityList;
				     headerCount = Collections.frequency(header,word);
					 titleCount = Collections.frequency(title,word);
					 bodyCount = Collections.frequency(body,word) - headerCount;
					 System.out.println("counts : " 
			                    + headerCount+","+titleCount+","+ bodyCount);
					 /////
					 float idf = 0;//getIdf(documentsURLs,word);
					 float tdf =  0;//getTDF(entry.getValue(), word);
					 ////	 
					 priorityList = new ArrayList();
					 priorityList.add(tdf);
					 priorityList.add((float)titleCount); 
					 priorityList.add((float)headerCount);
					 priorityList.add((float)bodyCount);
				    
				    if (wordsDictionary.get(word) == null) 
					{
					 
					 dataOfEachUrl = new LinkedHashMap() ;
					 dataOfEachUrl.put(entry.getKey(), priorityList);
					 ////
					 wordVal = new wordValue(idf, dataOfEachUrl);
					 ////
					 wordsDictionary.put(word, wordVal);
					}
					else 
					{
						wordVal = wordsDictionary.get(word);
						dataOfEachUrl = wordVal.tdfDictionary;
						dataOfEachUrl.put(entry.getKey(), priorityList);
						wordVal = new wordValue(idf, dataOfEachUrl);
						wordsDictionary.replace(word, wordVal);
					
					}
				
				}
		 }
	           
	 }
	 float getIdf ( Map<Integer, String> documentsURLs, String word) 
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
	 float getTDF ( String url, String word) 
	 {
		 HTMLParser htmlDoc  ;
		 htmlDoc = new HTMLParser(url);
		 Integer totalSize = htmlDoc.get_fullText().size();
		 int occurrences = Collections.frequency(htmlDoc.get_fullText(),word);
		 
		 return occurrences/ totalSize;
		 
	 }
}
