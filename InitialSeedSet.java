package crawling;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class InitialSeedSet {

	public static void main(String[] args) {
		DBManager db=DBManager.getinstance();
		
		ArrayList<String>value= new ArrayList<String>();
		value.add("");
		value.add("notvisited");
		ArrayList<String>keys= new ArrayList<String>();
		
		
		keys.add("https://www.geeksforgeeks.org/sorting-algorithms");
		keys.add("https://www.geeksforgeeks.org/fundamentals-of-algorithms");
		keys.add("https://www.geeksforgeeks.org/sql-tutorial");
		keys.add("https://www.geeksforgeeks.org/operating-systems");
		keys.add("https://www.geeksforgeeks.org/java");
		keys.add("https://www.geeksforgeeks.org/python-programming-language");
		keys.add("https://www.javatpoint.com/java-tutorial");
		keys.add("https://www.javatpoint.com/android-tutorial");
		keys.add("https://www.javatpoint.com/servlet-tutorial");
		keys.add("https://www.javatpoint.com/design-patterns-in-java");
		keys.add("https://www.javatpoint.com/spring-tutorial");
		keys.add("https://www.javatpoint.com/jsp-tutorial");
		
		 for(int i=0;i<keys.size();i++){    
			 db.UpdateOneSeed(keys.get(i),value);
	          }  
		
	}

}
