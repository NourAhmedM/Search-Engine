import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public class Relevance {

	Set<String> words;
	public Relevance (Set<String> words) {
		this.words = words;
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
	
	 public static void main(String[] args) {
		 wordValue w = tempCreatData();
		 w.print();
	 }
}
