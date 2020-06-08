

import java.util.Map;
import java.util.Map.Entry;

import com.mongodb.DBCollection;
import com.mongodb.DBObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

public class PageRank {
 
 public static int[][] graph;
 public static int size;
 public Map<Integer, String> documentsURLs;
 
 
 public static Map<String, ArrayList<String>> dumDataURLs() {
	 HashMap<String,ArrayList<String>>LinksAndRefers=new HashMap<String,ArrayList<String>>();
		DBManager db = DBManager.getinstance();
		DBCollection seedsCollection = db.getLinksAndRefers().getCollection();
		Iterator<DBObject> objects = seedsCollection.find().iterator();
		while (objects.hasNext()) {
			Map oneLink = objects.next().toMap();

			String link = (String) oneLink.get("link");
			ArrayList<String> cont_visit = (ArrayList<String>) oneLink.get("refers");

			LinksAndRefers.put(link, cont_visit);
		}
		return LinksAndRefers;
 }
 public static Map<Integer, String> dumDataDocument() {
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
 public PageRank(Map<String, ArrayList<String>> URLs, Map<Integer, String> documentsURLs){
	 this.documentsURLs = documentsURLs;
	 size = documentsURLs.size();
	 graph = new int[size][size];
	 fillGraph(URLs, documentsURLs);
 }
 
 //uncomment this function
 
// public static HashMap<String,ArrayList<String>> readLinks() {
//		HashMap<String,ArrayList<String>>LinksAndRefers=new HashMap<String,ArrayList<String>>();
//		DBManager db = DBManager.getinstance();
//		DBCollection seedsCollection = db.getLinksAndRefers().getCollection();
//		Iterator<DBObject> objects = seedsCollection.find().iterator();
//		while (objects.hasNext()) {
//			Map oneLink = objects.next().toMap();
//
//			String link = (String) oneLink.get("link");
//			ArrayList<String> cont_visit = (ArrayList<String>) oneLink.get("refers");
//
//			LinksAndRefers.put(link, cont_visit);
//		}
//		return LinksAndRefers;
//	}
 
 // this function swap the key and the value for the map
 static Map<String, Integer> swapKeyValue(Map<Integer, String> documentsURLs){
	Map<String, Integer> documentsIndices = new LinkedHashMap<String, Integer>();
	
	for (Entry<Integer, String> entry : documentsURLs.entrySet())
		documentsIndices.put(entry.getValue(), entry.getKey());
	
	return documentsIndices;
 }
 
 // this function fills the graph with numbers corresponding to the URLs
 static void fillGraph(Map<String, ArrayList<String>> URLs, Map<Integer, String> documentsURLs) {
	 Map<String, Integer> documentsIndices = swapKeyValue(documentsURLs);
	 List<String> refere;
	 for (Entry<String, ArrayList<String>> entry : URLs.entrySet()) {
		 refere = entry.getValue();
		 for(int i = 0; i < refere.size(); i++)
		 {
			 graph[documentsIndices.get(refere.get(i))][documentsIndices.get(entry.getKey())] = 1;
		 }
	 }
 }


static void normalize(double[] rank) {   
	 double sum = 0;
	 for(int i = 0; i < rank.length; i++)
		 sum += rank[i];
		 
	 for(int i = 0; i < rank.length; i++)
		 rank[i] /= sum;
 }
 
 public static void main(String[] args) {
//	 HashMap<String,ArrayList<String>> urls = readLinks(); //getting the urls and the refered urls from the database
	 Map<String, ArrayList<String>> URLs = new HashMap<String, ArrayList<String>>();
	 Map<Integer, String> documentsURLs = new HashMap<Integer, String>();
	 //----------------------- dum data ----------------------------------------
	 documentsURLs = dumDataDocument();
	 URLs = dumDataURLs();
	//----------------------- dum data ----------------------------------------
	 Map<String , Double> pagesRank = new HashMap<String , Double>();
	 
	 PageRank p = new PageRank(URLs, documentsURLs);
	 double[] rank0, rank1;
	 int[] L = new int[p.size];
	 
	 rank0 = new double[p.size];
	 rank1 = new double[p.size];
	 
	 
	 for(int i = 0; i < p.size; i++)
	 {
		//initial itr all ranks are equal in the 0's iteration
		 rank0[i] = 1/(double)p.size;
		 // how many pages each page refer to "calculating the number of outgoing arrows from each page"
		 int count = 0;
		 for(int j = 0; j < p.size; j++)
		 {
			 if (p.graph[i][j] == 1)
				 count++;
		 }
		 L[i] = count;
	 }
	 
	 // printing the matrix
	 for(int i = 0; i < p.size; i++)
	 {
		 for(int j = 0; j < p.size; j++)
		 {
			 System.out.print(p.graph[i][j]);
		 }
		 System.out.print("\n");
	 }
	 
	 double lampda = 0.85f;
	 //-----------------------------------------------------------------------------------
	 for(int i = 0; i < 6; i++)
	 {
		 for(int j = 0; j < p.size; j++)
		 {
			 double pr = 0;
			 for(int k = 0; k < p.size; k++)
			 {
				 if(p.graph[k][j] == 1)
				 {
					 pr = pr + (rank0[k]/L[k]);
				 }
			 }
//			 rank1[j] = pr;
			 rank1[j] = (1-lampda)+lampda*pr;
//			 rank1[j] = (1-lampda)*rank0[j]+lampda*pr;
//				 System.out.println(rank1[j]);
		 }
			 normalize(rank1);
		 rank0 = rank1;
	 }
	 
	 for(int j = 0; j < p.size; j++)
	 {
			 System.out.println(rank1[j]);
			 pagesRank.put(p.documentsURLs.get(j), rank1[j]);
	 }
	 System.out.println(pagesRank);
//	 savePagesRank(pagesRank);
	 DBManager db = DBManager.getinstance();
	 db.savePagesRank(pagesRank);
	 
	}
}
