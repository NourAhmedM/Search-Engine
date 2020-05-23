import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public class Relevance {

	String[] words;
	int size;
	public Relevance (String[] words) {
		this.words = words;
		this.size = words.length;
	}
	
	public static wordValue tempCreatData() {
		float tempIdf;
		Map<Integer, List<Float> > tempTdfDictionary;
		List<Float> priorityList;

		int headerCount = ThreadLocalRandom.current().nextInt(0, 10);
		int titleCount = ThreadLocalRandom.current().nextInt(0, 10);
		int bodyCount = ThreadLocalRandom.current().nextInt(0, 10);

		 float idf = ThreadLocalRandom.current().nextFloat();
		 System.out.println("the random float number is "+idf);
		 
		 float tdf = ThreadLocalRandom.current().nextFloat();
		 System.out.println("the 2nd random float number is "+tdf);
		
		 priorityList = new ArrayList();
		 priorityList.add(tdf);
		 priorityList.add((float)titleCount); 
		 priorityList.add((float)headerCount);
		 priorityList.add((float)bodyCount);
		 
		 tempTdfDictionary = new LinkedHashMap() ;
		 tempTdfDictionary.put(1, priorityList);

		 wordValue wordVal = new wordValue(idf, tempTdfDictionary);
		return wordVal;
	}
	
	public Map<Integer, String> ranker(Map<String, wordValue> wordsDictionary) {
		wordValue wordVal;
		float idf;
		Map<Integer, List<Float> > tdfDictionary;
		for(int i = 0; i < this.size; i++)  // loop on every word in the query
		{
			wordVal = wordsDictionary.get(words[i]);
			idf = wordVal.idf;
			tdfDictionary = wordVal.tdfDictionary;
		}
		return null;
	}
	
	public float rank(float TF, float IDF) { // fore now just tf/idf
		if (TF > 0.5)
			return 0;
		float IDF_log = (float) Math.log(IDF);
		return IDF_log*TF;
	}
	
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
		
		Map<String, wordValue> wordsDictionary = new HashMap<String, wordValue>();;
		wordsDictionary.put("Geeks", w);
	 }
}
