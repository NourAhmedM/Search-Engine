
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import com.mongodb.DBCollection;
import com.mongodb.DBObject;

public class Relevance {

	public Map<Integer, String> documentsURLs; //fill it from database
	
	public Relevance () {
		
		documentsURLs=readLinksWithIndecies();
	}
	
	
	//====================================================================================================
	//-------------------------------------- database methods --------------------------------------------
	//====================================================================================================
	public  static HashMap<String,Double> readPageRanks() {
		HashMap<String,Double> pageranks=new HashMap<String,Double>();
		DBManager db = DBManager.getinstance();
		DBCollection seedsCollection = db.getPageRanks().getCollection();
		Iterator<DBObject> objects = seedsCollection.find().iterator();
		while (objects.hasNext()) {
			Map onepage = objects.next().toMap();

			String link = (String) onepage.get("link");
			double rankkk = (double) onepage.get("rank");
			double rank = (double) rankkk;

			pageranks.put(link, rank);

		}
		return pageranks;
	}
	
	public Map<Integer, String> readLinksWithIndecies()
	{
		Map<Integer, String> documentsURLs= new LinkedHashMap<Integer, String>();
		DBManager db = DBManager.getinstance();
		DBCollection seedsCollection = db.getdocumentsURLs().getCollection();
		Iterator<DBObject> objects = seedsCollection.find().iterator();
		while (objects.hasNext()) {
			Map onelink = objects.next().toMap();
	
			String link = (String) onelink.get("link");
			int index = (Integer) onelink.get("index");
	
			documentsURLs.put(index, link);
	
		}
		return documentsURLs;
	}
	
	//====================================================================================================
	//------------------------------- creating temp data for testing -------------------------------------
	//====================================================================================================
	public static wordValue tempCreatData() {
		Map<Integer, List<Double> > tempTdfDictionary;
		List<Double> priorityList;

		int headerCount = ThreadLocalRandom.current().nextInt(0, 10);
		int titleCount = ThreadLocalRandom.current().nextInt(0, 10);
		int bodyCount = ThreadLocalRandom.current().nextInt(0, 10);

		 double idf = ThreadLocalRandom.current().nextDouble();
//		 System.out.println("the random float number is "+idf);
		 
		 double tdf = ThreadLocalRandom.current().nextDouble();
//		 System.out.println("the 2nd random float number is "+tdf);
		
		 priorityList = new ArrayList<Double>();
		 priorityList.add(tdf);
		 priorityList.add((double)titleCount); 
		 priorityList.add((double)headerCount);
		 priorityList.add((double)bodyCount);
		 
		 tempTdfDictionary = new LinkedHashMap<Integer, List<Double>>() ;
		 tempTdfDictionary.put(1, priorityList);

		 wordValue wordVal = new wordValue(idf, tempTdfDictionary);
		return wordVal;
	}

	//====================================================================================================
	//------------------------------------ method to sort the map ----------------------------------------
	//====================================================================================================
	public List<Integer> sortMap(Map<Integer, Double> rankValues) {
		Map<Integer, Double> unSortedMap = rankValues;
        
//		System.out.println("Unsorted Map : " + unSortedMap);
		 
		LinkedHashMap<Integer, Double> sortedMap = new LinkedHashMap<>();
		 
		unSortedMap.entrySet()
		    .stream()
		    .sorted(Map.Entry.comparingByValue())
		    .forEachOrdered(x -> sortedMap.put(x.getKey(), x.getValue()));
		 
//		System.out.println("Sorted Map   : " + sortedMap);
		
		List<Integer> sortedList = new ArrayList<Integer>();
		// store the indices of the urls
		for (Entry<Integer, Double> entry : sortedMap.entrySet())
		{
			sortedList.add(entry.getKey());
		}
		return sortedList;
	}

	//====================================================================================================
	//------------------------------------- the ranking algorithm ----------------------------------------
	//====================================================================================================
	public List<Integer> ranker(Map<String, wordValue> wordsDictionary, List<Integer> indices) {
		Map<Integer, Double> rankValues = new Hashtable<Integer, Double>();
		wordValue wordVal;
		double idf;
		Map<Integer, List<Double> > tdfDictionary;
		ArrayList<Double> priorityList;
		int index;
		double tf;
		double tf_idf;
		double pageRank = 1;
		double title, header;
		String Link;
		HashMap<String,Double> pageRankValues = readPageRanks();
		for (Entry<String, wordValue> entry2 : wordsDictionary.entrySet()) // iterate on each word
		{
			wordVal = wordsDictionary.get(entry2.getKey());
			idf = wordVal.idf;
			tdfDictionary = wordVal.tdfDictionary;
			for (Entry<Integer, List<Double>> entry : tdfDictionary.entrySet())  // iterate on priority list
			{
				index = entry.getKey();
				// if indices is empty then it's not phrase search and execute the next lines
				// if not empty then it's phrase search, execute only if the index exists
				if((indices.isEmpty() || indices.contains(index))) {
					Link = documentsURLs.get(index); // get the URL link
					pageRank = pageRankValues.get(Link);
					priorityList = (ArrayList<Double>)entry.getValue();
					
					
					tf = priorityList.get(0);
					title = priorityList.get(1);
					header = priorityList.get(2);
					tf_idf = rank(tf, idf, pageRank, title, header);
	
					if(rankValues.get(index) == null)  // if index is not in the map add it
					{
						rankValues.put(index, tf_idf);
					}
					else                              // if in the map sum the prev tf/idf and the new and replace it
					{
						double prev_tf_idf = rankValues.get(index);
						rankValues.remove(index);
						rankValues.put(index, prev_tf_idf+tf_idf);
					}
				}
			}
		}
		List<Integer> rankedIndices = new ArrayList<Integer>();
		rankedIndices = sortMap(rankValues);
		return rankedIndices;
	}
	

	//====================================================================================================
	//---------------------------------- calculating the rank value --------------------------------------
	//====================================================================================================
	public double rank(double TF, double IDF, double pageRank, double title, double header) {
		if (TF > 0.5)
			return 0;
		double IDF_log = (double) Math.log(1/IDF);
		double headerLoad = header*0.3;
		double titleLoad = title*0.6;
		double tf_idf = IDF_log*TF;
		return tf_idf+pageRank+titleLoad+headerLoad;
	}
	

	//====================================================================================================
	//-------------------------------------- database methods --------------------------------------------
	//====================================================================================================
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
		Map<Integer, Double> test = new HashMap<Integer, Double>();
		for(int i=0; i<7; i++) {
			if(test.get(vals[i]) == null)
			{
				test.put(vals[i], (double)i);
			}
			else
			{
				double tempo = test.get(vals[i]);
				test.remove(vals[i]);
				test.put(vals[i], tempo+(double)i);
			}
		}
//		sortMap(test);
		
	 }
}
