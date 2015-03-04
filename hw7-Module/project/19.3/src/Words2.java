import java.util.*;
import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.IOException;
import static java.nio.file.Files.readAllBytes;
import static java.nio.file.Paths.get;


public class Words2 implements IWords{
    
    public List<String> extractWords(String filename){
        System.out.println("Implementing the second set of word implementations");
        if(filename.length() ==0 || filename == null){
            System.out.println("Error: Input Filename");
        }
        List<String> wordList = null;
        String fileText = null;
        
        try {
            fileText = new String(readAllBytes(get(filename)));
        } catch (IOException e) {
            System.out.println("No file found.");
            return null;
        }
        wordList = new ArrayList<String>(Arrays.asList(fileText.toLowerCase().replaceAll("[^a-zA-Z]+"," ").split(" ")));
        String stopStr=null;
        try {
            stopStr = new String(readAllBytes(get("../stop_words.txt")));
        } catch (IOException e) {
            System.out.println("Stop Words file not found or not opening.");
            e.printStackTrace();
        }
        List<String> stopWords = new ArrayList<String>(Arrays.asList(stopStr.split(",")));
        wordList.removeAll(stopWords);
        return wordList;
    }
}