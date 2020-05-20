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
		keys.add("https://www.javatpoint.com/java-hashmap");
		keys.add("https://mongodb.github.io/mongo-java-driver/3.4/driver/getting-started/quick-start");
		
		 for(int i=0;i<keys.size();i++){    
			 db.UpdateOneSeed(keys.get(i),value);
	          }  
		
	}

}
