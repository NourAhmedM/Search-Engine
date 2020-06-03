
package crawling;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

import java.util.List;


public class wordValue 
	{
	public double idf;
	public Map<Integer, List<Double> > tdfDictionary;
	public wordValue()
	{}
	public wordValue(double idf, Map<Integer, List<Double> > tdfDic)
	{
	this.idf = idf;
	this.tdfDictionary = tdfDic;
    }

	public double getidf()
	{
		return idf;
	}
	public Map<Integer, List<Double> > gettdfDictionary()
	{
		return tdfDictionary;
	}
	
	
	void print() 
    {
		 System.out.println("idf: " 
                 + idf);
		 
		 for (Map.Entry <Integer, List<Double> > entry :tdfDictionary .entrySet())
		 {
			 System.out.println("url index : " 
	                 + entry.getKey());			
			 System.out.println("priority list: "
	    	                 + entry.getValue());
		 }
		
    	
    }
  
}
