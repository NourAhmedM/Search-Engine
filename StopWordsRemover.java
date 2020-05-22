//import org.openjdk.jmh.annotations.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class StopWordsRemover {
	private String data;
    
    private List<String> stopwords;

    private String stopwordsRegex;
    
    

   
    String setup (String s)throws IOException 
    {
    	data = s;
        data = data.toLowerCase();
        data = data.replaceAll("\\p{Punct}", "");
        stopwords = Files.readAllLines(Paths.get("./assets/stopwords.txt"));
        stopwordsRegex = stopwords.stream().collect(Collectors.joining("|", "\\b(", ")\\b\\s?"));
        String result  = removeAll();
        return result;
    }
    public static void main(String[] args) throws IOException
    {
    	
    }
    public String removeAll() {
    	
    	ArrayList<String> allWords = 
          Stream.of(data.split(" "))
                .collect(Collectors.toCollection(ArrayList<String>::new));
    	stopwords.add(" ");
        allWords.removeAll(stopwords);
        return allWords.stream().collect(Collectors.joining(" "));
    }

    public String removeManually() {
        String[] allWords = data.split(" ");
        StringBuilder builder = new StringBuilder();
        for(String word : allWords) {
            if(!stopwords.contains(word)) {
                builder.append(word);
                builder.append(' ');
            }
        }
        return builder.toString().trim();
    }
    public String replaceRegex() {
        return data.replaceAll(stopwordsRegex, "");
    }

}
