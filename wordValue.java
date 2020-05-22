import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

import java.util.List;


public class wordValue 
	{
	public float idf;
	public Map<Integer, List<Float> > tdfDictionary;
	public wordValue(float idf, Map<Integer, List<Float> > tdfDic)
	{
	this.idf = idf;
	this.tdfDictionary = tdfDic;
    }

	
	
	
	void print() 
    {
		 System.out.println("idf: " 
                 + idf);
		 for (Map.Entry <Integer, List<Float> > entry :tdfDictionary .entrySet())
		 {
			 System.out.println("url index : " 
	                 + entry.getKey());			
			 System.out.println("priority list: "
	    	                 + entry.getValue());
		 }
		
    	
    }
  
}
