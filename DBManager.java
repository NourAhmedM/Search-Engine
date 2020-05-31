package crawling;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.MongoClient;

public class DBManager {
	
	private DB database;
	private static DBManager instance;
	

	private DBManager()
	{
		MongoClient mongo = new MongoClient("localhost" , 27017);
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
	
	
	
	
	
	
	
	
	
	
	////////////////////////////ranker/////////////////////////////////////////////////
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static void main(String[] args) {
		
	}

}
