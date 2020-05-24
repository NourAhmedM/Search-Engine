import java.util.ArrayList;
import java.util.List;

public class QueryProcessor {
	public String[] query;
	
	public QueryProcessor(String searchQuery) {
		this.query = splitting(searchQuery);
	}
	
	public String[] splitting(String searchQuery){
		String[] splittedQuery;
		splittedQuery = searchQuery.split(" ");
		return splittedQuery;
	}
}
