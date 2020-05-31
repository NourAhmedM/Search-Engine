import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import javax.swing.text.html.HTMLDocument.Iterator;

import com.mongodb.DBCollection;
import com.mongodb.DBObject;

public class Relevance {

	public Map<Integer, String> documentsURLs; //fill it from database
	
	public Relevance () {
	}
	
	//uncomment this function
	
//	public  static HashMap<String,Double> readPageRanks() {
//		HashMap<String,Double> pageranks=new HashMap<String,Double>();
//		DBManager db = DBManager.getinstance();
//		DBCollection seedsCollection = db.getPageRanks().getCollection();
//		Iterator<DBObject> objects = seedsCollection.find().iterator();
//		while (objects.hasNext()) {
//			Map onepage = objects.next().toMap();
//
//			String link = (String) onepage.get("link");
//			Double rank = (Double) onepage.get("rank");
//
//			pageranks.put(link, rank);
//
//		}
//		return pageranks;
//	}
	//------------------------------------------------------------------------------------------------------------------
	//------------------------------- creating temp data for testing ---------------------------------------------------
	//------------------------------------------------------------------------------------------------------------------

	public static wordValue tempCreatData() {
		Map<Integer, List<Float> > tempTdfDictionary;
		List<Float> priorityList;

		int headerCount = ThreadLocalRandom.current().nextInt(0, 10);
		int titleCount = ThreadLocalRandom.current().nextInt(0, 10);
		int bodyCount = ThreadLocalRandom.current().nextInt(0, 10);

		 float idf = ThreadLocalRandom.current().nextFloat();
//		 System.out.println("the random float number is "+idf);
		 
		 float tdf = ThreadLocalRandom.current().nextFloat();
//		 System.out.println("the 2nd random float number is "+tdf);
		
		 priorityList = new ArrayList<Float>();
		 priorityList.add(tdf);
		 priorityList.add((float)titleCount); 
		 priorityList.add((float)headerCount);
		 priorityList.add((float)bodyCount);
		 
		 tempTdfDictionary = new LinkedHashMap<Integer, List<Float>>() ;
		 tempTdfDictionary.put(1, priorityList);

		 wordValue wordVal = new wordValue(idf, tempTdfDictionary);
		return wordVal;
	}

	//------------------------------------------------------------------------------------------------------------------
	//--------------------------------------- method to sort the map ---------------------------------------------------
	//------------------------------------------------------------------------------------------------------------------
	public List<Integer> sortMap(Map<Integer, Float> rankValues) {
		Map<Integer, Float> unSortedMap = rankValues;
        
//		System.out.println("Unsorted Map : " + unSortedMap);
		 
		LinkedHashMap<Integer, Float> sortedMap = new LinkedHashMap<>();
		 
		unSortedMap.entrySet()
		    .stream()
		    .sorted(Map.Entry.comparingByValue())
		    .forEachOrdered(x -> sortedMap.put(x.getKey(), x.getValue()));
		 
//		System.out.println("Sorted Map   : " + sortedMap);
		
		List<Integer> sortedList = new ArrayList<Integer>();
		// store the indices of the urls
		for (Entry<Integer, Float> entry : sortedMap.entrySet())
		{
			sortedList.add(entry.getKey());
		}
		return sortedList;
	}

	//------------------------------------------------------------------------------------------------------------------
	//---------------------------------------- the ranking algorithm ---------------------------------------------------
	//------------------------------------------------------------------------------------------------------------------
	public List<Integer> ranker(Map<String, wordValue> wordsDictionary) { // return type may change to list of strings if url is used
		Map<Integer, Float> rankValues = new Hashtable<Integer, Float>();    //if we used url instead of index this will be <string, float>
		wordValue wordVal;
		float idf;
		Map<Integer, List<Float> > tdfDictionary;
		List<Float> priorityList;
		int index;
		float tf;
		float tf_idf;
		double pageRank = 1;
		String Link;
//		HashMap<String,Double> pageRankValues = readPageRanks();
		for (Entry<String, wordValue> entry2 : wordsDictionary.entrySet()) // iterate on each word
		{
			wordVal = wordsDictionary.get(entry2.getKey());
			System.out.println("");
			System.out.println("the word: "+entry2.getKey()+" has word value:");
			wordVal.print();
			idf = wordVal.idf;
			tdfDictionary = wordVal.tdfDictionary;
			for (Entry<Integer, List<Float>> entry : tdfDictionary.entrySet())  // iterate on priority list
			{
				index = entry.getKey();
//				Link = documentsURLs.get(index); // get the URL link
//				pageRank = pageRankValues.get(Link);
				priorityList = entry.getValue();
				tf = priorityList.get(0);
				tf_idf = rank(tf, idf, pageRank);

				if(rankValues.get(index) == null)  // if index is not in the map add it
				{
					rankValues.put(index, tf_idf);
				}
				else                              // if in the map sum the prev tf/idf and the new and replace it
				{
					float prev_tf_idf = rankValues.get(index);
					rankValues.remove(index);
					rankValues.put(index, prev_tf_idf+tf_idf);
				}
			}
		}
		List<Integer> rankedIndices = new ArrayList<Integer>();
		rankedIndices = sortMap(rankValues);
		return rankedIndices;
	}
	

	//------------------------------------------------------------------------------------------------------------------
	//------------------------------------ calculating the rank value --------------------------------------------------
	//------------------------------------------------------------------------------------------------------------------
	public float rank(float TF, float IDF, double pageRank) { // fore now just tf/idf
		if (TF > 0.5)
			return 0;
		float IDF_log = (float) Math.log(IDF);
		return IDF_log*TF+(float)pageRank;
	}
	

	//------------------------------------------------------------------------------------------------------------------
	//------------------------------------ main function for testing ---------------------------------------------------
	//------------------------------------------------------------------------------------------------------------------
	 public static void main(String[] args) {
		wordValue w = tempCreatData();
		w.print();
		 
		Set<String> hash_Set = new HashSet<String>(); 
		hash_Set.add("Geeks"); 
		hash_Set.add("For"); 
		hash_Set.add("Geeks"); 
		hash_Set.add("Example"); 
		hash_Set.add("Set"); 
		hash_Set.toArray();
		String[] queryWords = hash_Set.toArray(new String[0]);
		System.out.println(queryWords.length); 
		
		Map<String, wordValue> wordsDictionary = new HashMap<String, wordValue>();
		wordsDictionary.put("Geeks", w);
		
		int[] vals= {1, 5, 2, 4, 5, 1, 1};
		Map<Integer, Float> test = new HashMap<Integer, Float>();
		for(int i=0; i<7; i++) {
			if(test.get(vals[i]) == null)
			{
				test.put(vals[i], (float)i);
			}
			else
			{
				Float tempo = test.get(vals[i]);
				test.remove(vals[i]);
				test.put(vals[i], tempo+(float)i);
			}
		}
//		sortMap(test);
		
	 }
}
