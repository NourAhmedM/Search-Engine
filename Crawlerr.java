

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.mongodb.DBCollection;
import com.mongodb.DBObject;

public class Crawlerr {

	// link ,[content,visited]
	private HashMap<String, ArrayList<String>> seeds;
	// link ,[links refer to my link .....]
	private HashMap<String, HashSet<String>> linkesReferMe;
	// links ,["Disalloewed or allowed , link to match with"]
	private HashMap<String, ArrayList<ArrayList<String>>> robotsMap;

	private static final int MAX_CRAWELED_PAGES = 5000;

	public Crawlerr() {
		seeds = new HashMap<String, ArrayList<String>>();
		linkesReferMe = new HashMap<String, HashSet<String>>();
		robotsMap = new HashMap<String, ArrayList<ArrayList<String>>>();
	}

	public void readSeeds() {
		DBManager db = DBManager.getinstance();
		DBCollection seedsCollection = db.getSeedSet().getCollection();
		Iterator<DBObject> objects = seedsCollection.find().iterator();
		while (objects.hasNext()) {
			Map oneSeed = objects.next().toMap();

			String link = (String) oneSeed.get("link");
			ArrayList<String> cont_visit = (ArrayList<String>) oneSeed.get("ContentAndVisited");

			seeds.put(link, cont_visit);

		}
	}
	
	

	public void updatevisitedAndContent(String link, ArrayList<String> contentAndVisited) {
		DBManager db = DBManager.getinstance();
		db.UpdateOneSeed(link, contentAndVisited);
	}

	public void Updatelinks(String link) {
		DBManager db = DBManager.getinstance();
		db.Updatelinks(link);
	}

	public void UpdatelinksAndRefernce(String link, HashSet<String> refers) {
		DBManager db = DBManager.getinstance();
		db.UpdatelinksAndRefernce(link, refers);
	}

	public boolean imageExtension(String link)
	{
		if (link.contains(".png")||link.contains(".jpeg")||link.contains(".gif")||
				link.contains(".pdf")||link.contains(".jpg"))
			return true;
		return false;
	}
	
	public void getPageLinks(int fromSeeds, String content, String link, String parentLink) {

		//to ensure that no two urls go to same two pages
		
		link=link.toLowerCase();
		
		link=link.split("#")[0];
		if(!link.isEmpty()&&link.charAt(link.length()-1)=='/')
		{
			
			link=link.substring(0, link.length()-1);
			
		}
		if (link.length()>5&&!link.substring(0,5).equals("https"))
			link="https"+link.subSequence(5, link.length());
		
		
		int temp=link.indexOf("/?ref");
		if (temp!=-1)
			link=link.substring(0, temp);
		
		if (linkesReferMe.size() < MAX_CRAWELED_PAGES &&!imageExtension(link)&& RobotsAllow(link))
			
			if (!linkesReferMe.containsKey(link)) {
				try {
					
					// 2. Fetch the HTML code
					Document document = Jsoup.connect(link).get();
					String docContent = document.toString();

					
					if (fromSeeds == 0 || (fromSeeds == 1 && !content.equals(docContent))) {

						
						// 3. Parse the HTML to extract links to other URLs
						Elements linksOnPage = document.select("a[href]");

						// 4. (i) If not add it to the index
						HashSet<String> parents = new HashSet<String>();
						if (fromSeeds == 0)
							parents.add(parentLink);
						linkesReferMe.put(link, parents);
						//////////// update database////////
						Updatelinks(link);

						UpdatelinksAndRefernce(link, parents);
						////////////////////////////////
						// 5. For each extracted URL... go back to Step 4.
						for (Element page : linksOnPage) {
							getPageLinks(0, "", page.attr("abs:href"), link);

						}
						if (fromSeeds == 1) {

							ArrayList<String> con_vis = new ArrayList<String>();
							con_vis.add(docContent);
							con_vis.add("visited");
							seeds.put(link, con_vis);
							updatevisitedAndContent(link, con_vis);
						}

					}
				} catch (IOException e) {
					System.err.println("exeptionnnnnnnnnnnnnnnnnnnnnnn");
				}
			} else {
				if (fromSeeds == 0) {
					linkesReferMe.get(link).add(parentLink);
					UpdatelinksAndRefernce(link, linkesReferMe.get(link));
				}
			}
	}

	public boolean RobotsAllow(String url) {
		
		String[] splittedUrl = url.split("/");
		if(splittedUrl.length<3)
			return false;
		String rootUrl = splittedUrl[0] + "//" + splittedUrl[2];
		String robots = splittedUrl[0] + "//" + splittedUrl[2] + "/robots.txt";

		
		if (robotsMap.containsKey(robots)) {
			
			boolean allowed = true;
			url = url.replace("?", "؟");
			for (int i = 0; i < robotsMap.get(robots).size(); i++) {
				
				if (robotsMap.get(robots).get(i).get(0).equals("Disallow:")) {
					if (url.matches(robotsMap.get(robots).get(i).get(1)))
						allowed = false;
				} else {
					if (url.matches(robotsMap.get(robots).get(i).get(1)))
						allowed = true;
				}
			}

			return allowed;
		} else {
			try {
				
				URL robotsURL = new URL(robots);
				URLConnection urlcon = robotsURL.openConnection();
				InputStream inputStream = urlcon.getInputStream();
				InputStreamReader isReader = new InputStreamReader(inputStream);
				BufferedReader reader = new BufferedReader(isReader);
				String str;

				ArrayList<ArrayList<String>> robotsData = new ArrayList<ArrayList<String>>();
				robotsMap.put(robots, robotsData);

				boolean foundUserAgent = false;

				boolean allowed = true;

				url = url.replace("?", "؟");

				while ((str = reader.readLine()) != null) {

					if (foundUserAgent == true) {
						if (str.contains("Disallow:")) {
							String path = str.substring(9, str.length());
							path = path.trim();
							path = path.replace("*", "(.*)");
							path = path.replace("?", "؟");
							path = rootUrl + path;
							if (!path.endsWith("$"))
								path += "(.*)";

							if (url.matches(path)) {

								allowed = false;

							}
							ArrayList<String> robotsEntry = new ArrayList<String>();
							robotsEntry.add("Disallow:");
							robotsEntry.add(path);
							robotsMap.get(robots).add(robotsEntry);

						} else if (str.contains("Allow:")) {

							String path = str.substring(6, str.length());

							path = path.trim();
							path = path.replace("*", "(.*)");
							path = path.replace("?", "؟");
							path = rootUrl + path;
							if (!path.endsWith("$"))
								path += "(.*)";

							if (url.matches(path)) {

								allowed = true;

							}
							ArrayList<String> robotsEntry = new ArrayList<String>();
							robotsEntry.add("Allow:");
							robotsEntry.add(path);
							robotsMap.get(robots).add(robotsEntry);

						} else if (str.contains("User-agent:"))
							foundUserAgent = false;
					} else if (str.contains("User-agent: *")) {

						foundUserAgent = true;
					}
				}

				return allowed;
			} catch (Exception e) {

				System.out.println("eeeeeeeeeeeeeeeeeeeeeeeeeeeee");
				return false;
			}
		}

	}

	public void InitialCrawl() {
		readSeeds();
		if (!seeds.isEmpty()) {
			boolean allVisited = true;
			for (Map.Entry<String, ArrayList<String>> entry : seeds.entrySet()) {

				if (entry.getValue().get(1).equals("notvisited")) {
					allVisited = false;

				}
			}
			if (allVisited) {

				for (Map.Entry<String, ArrayList<String>> entry : seeds.entrySet()) {
					entry.getValue().set(1, "notvisited");
					updatevisitedAndContent(entry.getKey(), entry.getValue());

				}
			}

		}
	}

	
	class CrawlerRunnable implements Runnable {
		private Crawlerr crawler;
		private int seedsPerThread;
		private int id;
		private int extra;
		
		
		public CrawlerRunnable (Crawlerr c,int n,int mid ,int extraN) {
			this.crawler = c;
			this.seedsPerThread=n;
			this.id=mid;
			this.extra=extraN;
			
		}

		public void run () {
			int ptr=1;
			
			int end=seedsPerThread*id;
			int begin=end-seedsPerThread+1;
			end+=extra;
			System.out.println(id);
			for (Map.Entry<String, ArrayList<String>> entry : seeds.entrySet()) {
				if(ptr>=begin&&ptr<=end)
				{
					System.out.println("--------------------------------");
					System.out.println(id);
					System.out.println("--------------------------------");
					if (entry.getValue().get(1).equals("notvisited") && linkesReferMe.size() < MAX_CRAWELED_PAGES) {
					
						getPageLinks(1, entry.getValue().get(0), entry.getKey(), "");

					}
				}
				ptr+=1;
				
			}
		}
	}
	
	public static void main(String[] args) throws InterruptedException {

		Crawlerr crawler1 = new Crawlerr();
		crawler1.InitialCrawl();
		
		Scanner sc= new Scanner(System.in);    
		System.out.print("Enter Number of threads:  ");  
		int threadsNum= sc.nextInt();  
		
		int seedsSize=crawler1.seeds.size();
		
		int seedsPerThread=seedsSize/threadsNum;
		int extraSeeds=seedsSize%threadsNum;
		
		
		if (threadsNum>seedsSize)
		{
			extraSeeds=0;
			threadsNum=seedsSize;
			seedsPerThread=1;
		}
		ArrayList<Thread>threads=new ArrayList<Thread>();
		for(int i=1;i<=threadsNum;i++)
		{
			int extra =0;
			if(i==threadsNum)
				extra=extraSeeds;
			System.out.println("create thread");
			Thread t = new Thread (crawler1.new CrawlerRunnable(crawler1,seedsPerThread,i,extra));
			t.start(); 
			threads.add(t);
			
		}
		
		for(int i=0;i<threads.size();i++)
		{
			threads.get(i).join();;
		}
		
		

	}
}
