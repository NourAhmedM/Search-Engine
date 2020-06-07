package crawling;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.gt;
import static com.mongodb.client.model.Filters.gte;
import static com.mongodb.client.model.Filters.lt;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bson.Document;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Projections;

public class DBManager {
	
	private DB database;
	private static DBManager instance;
	private MongoClient mongoClient;

	private DBManager()
	{
		MongoClient mongo = new MongoClient("localhost" , 27017);
		mongoClient=mongo;
		database=mongo.getDB("SearchEngine");
		
		
	}
	
	public static DBManager getinstance()
	{
		if(instance==null)
			instance=new DBManager();
		
		return instance;
		
	}
	
	public void saveRobot( Map<String , ArrayList<String>> robots){
        DBCollection collection = database.getCollection("Robot");

        for (Map.Entry<String,ArrayList<String>> entry : robots.entrySet()) {
        	
            collection.update(new BasicDBObject("Link", entry.getKey()),
                    new BasicDBObject
                                      ( "Disallowed",  entry.getValue())
                                      .append("Link", entry.getKey())
                                      
                                       , true
                                       , false);
           
        
        }
    }
	
	public DBCursor getRobots(){
    	DBCollection collection = database.getCollection("Robot");
    	DBCursor cursor = collection.find();
    	
    	return cursor;
    	
    	
    }
	
	

	
	////////////////////////////////crawler///////////////////////////////////////////
	
	
	
	public DBCursor getSeedSet(){
    	DBCollection collection = database.getCollection("SeedSet");
    	DBCursor cursor = collection.find();
    	
    	return cursor;
    	
    }
	
	public DBCursor getLinksAndRefers(){
    	DBCollection collection = database.getCollection("LinksAndRefers");
    	DBCursor cursor = collection.find();
    	
    	return cursor;
    	
    }
	
	public DBCursor getLinks(){
    	DBCollection collection = database.getCollection("Links");
    	DBCursor cursor = collection.find();
    	
    	return cursor;
    	
    }
	
	public void UpdateOneSeed(String link,ArrayList<String> contentAndVisited){
        DBCollection collection = database.getCollection("SeedSet");
        
        collection.update(new BasicDBObject("link", link),
                new BasicDBObject
                                  ( "link", link)
                                  .append("ContentAndVisited", contentAndVisited)
                      			
                                   , true
                                   , false);
    }
	
	public void Updatelinks(String link){
        DBCollection collection = database.getCollection("Links");
        
        collection.update(new BasicDBObject("link", link),
                new BasicDBObject
                                  ( "link", link)
                                   , true
                                   , false);
    }
	
	public void UpdatelinksAndRefernce(String link,HashSet<String> refers){
        DBCollection collection = database.getCollection("LinksAndRefers");
        
        collection.update(new BasicDBObject("link", link),
                new BasicDBObject
                                  ( "link", link)
                                  .append("refers", refers)
                      			
                                   , true
                                   , false);
    }
	
	
	public void savePagesRank( Map<String , Double> pagesRank){
        DBCollection collection = database.getCollection("PageRank");
        collection.drop();
        for (Map.Entry<String,Double> entry : pagesRank.entrySet()) {
        	
            collection.update(new BasicDBObject("link", entry.getKey()),
                    new BasicDBObject
                                      ( "link",  entry.getKey())
                                      .append("rank", entry.getValue())
                                      
                                       , true
                                       , false);
           
        
        }
    }
	
	public DBCursor getPageRanks(){
    	DBCollection collection = database.getCollection("PageRank");
    	DBCursor cursor = collection.find();
    	
    	return cursor;
    	
    }
	//////////////////////////////indexer///////////////////////////////////////////////
	
	
	
	public void savedocumentsURLs( Map<Integer, String> documentsURLs){
        DBCollection collection = database.getCollection("URLWithIndex");
        collection.drop();
        for (Map.Entry<Integer, String> entry : documentsURLs.entrySet()) {
        	
            collection.update(new BasicDBObject("index", entry.getKey()),
                    new BasicDBObject
                                      ( "index",  entry.getKey())
                                      .append("link", entry.getValue())
                                      
                                       , true
                                       , false);
           
        
        }
    }
	
	
	
	
	
	public void savedocumentsDictionary(Map<Integer, Set<String> > documentsDictionary){
        DBCollection collection = database.getCollection("URLWords");
        collection.drop();
        for (Map.Entry<Integer, Set<String>> entry : documentsDictionary.entrySet()) {
        	
            collection.update(new BasicDBObject("link", entry.getKey()),
                    new BasicDBObject
                                      ( "link",  entry.getKey())
                                      .append("words", entry.getValue())
                                      
                                       , true
                                       , false);
           
        
        }
    }
	
	public void saveSearchQueryLinks( ArrayList<String> links){
        DBCollection collection = database.getCollection("SearchQueryLinks");
        collection.drop();
        for(int i=0;i<links.size();i++)
        {
            collection.update(new BasicDBObject("index", i),
                    new BasicDBObject
                                      ( "index",  i)
                                      .append("link", links.get(i))
                                      
                                       , true
                                       , false);
        }
       
    }
	
	
	public ArrayList<String> getTenOfSearchQueryLinks(int StartingIndex) {
		MongoDatabase SearchEngine = mongoClient.getDatabase("SearchEngine");
		MongoCollection<Document> collection = SearchEngine.getCollection("SearchQueryLinks");
		Iterator<Document>  objects = collection.find(and(gte("index", StartingIndex*10)
				, lt("index", (StartingIndex+1)*10))).iterator();
		
    	ArrayList<String> links = new ArrayList<String>();
    	while (objects.hasNext()) {
    		String Url = (String) objects.next().get("link");
    		links.add(Url);
    	}
    	return links;
    }
	
	
	

	public DBCursor getwordsDictionary(){
    	DBCollection collection = database.getCollection("WordsDic");
    	DBCursor cursor = collection.find();
    	
    	return cursor;
    	
    }
	public void savewordsDictionary(Map<String, wordValue> wordsDictionary){
        DBCollection collection = database.getCollection("WordsDic");
        collection.drop();
        for (Map.Entry<String, wordValue> entry : wordsDictionary.entrySet()) {
        	
            collection.update(new BasicDBObject("word", entry.getKey()),
                    new BasicDBObject
                                      ( "word",  entry.getKey())
                                      .append("idf", entry.getValue().getidf())
                                      
                                      .append("linksIndecies", entry.getValue().gettdfDictionary().keySet())
                                      .append("listCorespondsToIndecies", entry.getValue().gettdfDictionary().values())
                                      
                                       , true
                                       , false);
           
        
        }
    }
	
	
	
	
	public DBCursor getdocumentsURLs(){
    	DBCollection collection = database.getCollection("URLWithIndex");
    	DBCursor cursor = collection.find();
    	
    	return cursor;
    	
    }
	
	
	////////////////////////////ranker/////////////////////////////////////////////////
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static void main(String[] args) {
		
	}

}
