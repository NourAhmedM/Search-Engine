
package crawling;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;


import java.io.File;
import java.io.IOException;
 
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
 

public class HTMLParser{
	 Document doc;
     String title = null;
     String header1 = null;
     String body = null;
     String fullText = null;
     StopWordsRemover remover ;
     Stemmer stemmer;	
	public HTMLParser (String url) 
	{
		remover = new StopWordsRemover();
		stemmer =  new Stemmer();
		try {
            doc = Jsoup.connect(url).get();
            fullText = doc.text();
            fullText = remover.setup(fullText);
            title = doc.title();
            title = remover.setup(title);
            header1 = doc.body().getElementsByTag("h1").text();
            header1 = remover.setup(header1);
            body = doc.body().text();
            body = remover.setup(body);

        } catch (IOException e) {
            
        }
	}
	List<String> toList(String str)
	{
		List<String> result   = new ArrayList<String>();
		if (str==null)
			return result;
		
		String[] arr = str.split(" ");   
		/*System.out.println("result : " 
                + str);*/
		for ( String s : arr) {
            s = stemmer.stemTerm(s);
			result.add(s);
		}
		
		return result;
		
	}
	List<String> get_title() {return toList(title);}
	List<String> get_header1() {return toList(header1);}
	List<String> get_body() {return toList(body);}
	List<String> get_fullText() {return toList(fullText);}
    public static void main(String args[])
    {
 
       /* System.out.println("Jsoup Can read HTML page from URL, title : " 
                                     + title);
       System.out.println("Jsoup Can read HTML page from URL, header1 : " 
                + header1);
      
        System.out.println("Jsoup Can read HTML page from URL, body : " 
                + body);*/
    }
 
}



