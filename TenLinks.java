


import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;


public class TenLinks extends HttpServlet{

	public static String getDomainName(String link) throws Exception {
	    URI url = new URI(link);
	    String domain = url.getHost();
	    if (domain.startsWith("www."))
	    	domain=domain.substring(4) ;
	    else
	    	domain=domain;
	    int indexOfDot=domain.indexOf(".");
	    if (indexOfDot!=-1)
	    	domain=domain.substring(0,indexOfDot);
	    	
	    return domain;
	}
	public Map<Integer, ArrayList<String >>  getTenLinks(String searchQuery,int index)throws Exception
	{
		
		QueryProcessor Q=new QueryProcessor();
		
		
		return Q.runQueryProcessor(searchQuery, index);
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
		System.out.print(queryList);
		return queryList;
	}
	
	public boolean isPhraseSearch(String searchQuery) {
		char first = searchQuery.charAt(0);
	    char last = searchQuery.charAt(searchQuery.length()-1);
	    
	    if(first == '-' && last == '-')
	    	return true;
	    return false;
	}
    public void doGet(HttpServletRequest request, HttpServletResponse response)
    throws IOException {
    	
        String index = request.getParameter("index");
        String searchquery = request.getParameter("searchQuery");
        
        
        Map<Integer, ArrayList<String >>  LinksWithNum;
        try {
        	LinksWithNum=getTenLinks(searchquery,Integer.parseInt(index));
        }
        catch(Exception e)
        {
        	LinksWithNum=new HashMap<Integer, ArrayList<String >> ();
        }
        int numberOfLink=0;
        ArrayList<String>Links=new ArrayList<String>();
        for (Map.Entry<Integer, ArrayList<String >> entry :  	LinksWithNum.entrySet()) 
        {
        	numberOfLink=entry.getKey();
        	Links=entry.getValue();
        }
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
	
        
        searchquery=searchquery.toLowerCase();
        
        String[] queryWords=searchquery.split(" ");
        
        boolean isPhrase = isPhraseSearch(searchquery);
		if(isPhrase)
		{
			searchquery=searchquery.substring(1, searchquery.length()-1);
		}
        String responceString="{\"NumberOfLinks\":"+numberOfLink+",\"Links\":[";

        
        
        for(int i=0;i<Links.size();i++)
        {
        	try {
        	String hostname="";
        	
        	Document document = Jsoup.connect(Links.get(i)).get();
        	String title=document.title()+" | "+getDomainName(Links.get(i));
        	
        	
        	//---------get content of link----------------
        	String totalDocContent;
        	if(document.body()!=null)
        	{
            		totalDocContent = document.body().text();
            		totalDocContent=totalDocContent.toLowerCase();
        	}
        	
           	 else
            		totalDocContent="";
        	
        	
        	String content="";
        	for(int j=0;j<queryWords.length;j++)
        	{
        		int indexOfLetter=totalDocContent.indexOf(queryWords[j]);
        		if (indexOfLetter!=-1)
        		{
        			if (indexOfLetter+100<totalDocContent.length())
        				content+=totalDocContent.substring(indexOfLetter,indexOfLetter+100);
        			else
        				content+=totalDocContent.substring(indexOfLetter,totalDocContent.length());
        			content+="...";
        		}
        	}
        	
        	
        	if (content.length()<300)
        		if (totalDocContent.length()>300-content.length())
        			content+=totalDocContent.substring(0,300-content.length());
        		else
        			content+=totalDocContent.substring(0,totalDocContent.length());
        	
        	
        	String url=Links.get(i);
        	String LinkObject;
        	if (i==Links.size()-1)
        		LinkObject="{\"title\":\""+title+"\",\"url\":\""+url+"\",\"content\":\""+content+"\"}";
        	else
        		LinkObject="{\"title\":\""+title+"\",\"url\":\""+url+"\",\"content\":\""+content+"\"},";
        	responceString+=LinkObject;

        	}catch(Exception e)
        	{}
        	
			
		}
       
        responceString+="]}";
       
         response.getWriter().println(responceString );
        
        
    }

}
