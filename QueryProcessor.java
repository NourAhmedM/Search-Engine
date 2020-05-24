import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;

public class QueryProcessor {
	public String[] query;
	
	public QueryProcessor(String searchQuery) {
		this.query = splitAndStam(searchQuery);
	}
	
	public String[] splitAndStam(String searchQuery){
		String[] splittedQuery;
		Stemmer stemmer = new Stemmer();
		splittedQuery = searchQuery.split(" ");
		for(int i = 0; i < splittedQuery.length; i++) {
			splittedQuery[i] = stemmer.stemTerm(splittedQuery[i]);
		}
		return splittedQuery;
	}
	

	public static void main(String[] args) {
		String s = "test this testing to be tested";
		QueryProcessor qp = new QueryProcessor(s);
		String[] q = qp.query;
		for(int i = 0; i < q.length; i++) {
			System.out.println(q[i]); 
		}
	}
}