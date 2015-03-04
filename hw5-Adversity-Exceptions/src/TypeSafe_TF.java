import java.io.File;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.IOException;
import static java.nio.file.Files.readAllBytes;
import static java.nio.file.Paths.get;


/**
 * Created by swanand on 2/6/2015.
 */
public class TypeSafe_TF {

    public static void main(String args[]){
        String filename = args[0];

        Map<String, Integer> descFreqMap = descendingSort(calculateFreq(filterStopWords(extractWords(filename))));
        if(descFreqMap!=null || !descFreqMap.isEmpty()){
            int count =0;
            for(Map.Entry<String, Integer> entry : descFreqMap.entrySet()){
                System.out.println(entry.getKey()+ " - " + entry.getValue());
                if(++count>=25)
                    break;
            }
        }else{
            System.out.println("Sorry! Could not get frequencies");
        }
    }

    private static Map<String, Integer> descendingSort(Map<String, Integer> termFreq) {
        TreeMap<String,Integer> sorted_termFreq = new TreeMap<String,Integer>(new ValueComparator(termFreq));
        sorted_termFreq.putAll(termFreq);
        return sorted_termFreq;
    }

    private static Map<String, Integer> calculateFreq(List<String> words) {
        HashMap<String, Integer> termFreq = new HashMap<String, Integer>();
        for(String word : words){
            if(word.length()>1){
                if(termFreq.containsKey(word)){
                    termFreq.put(word, (termFreq.get(word)) + 1);
                }else{
                    termFreq.put(word, 1);
                }
            }
        }
        return termFreq;
    }

    private static List<String> filterStopWords(List<String> normalizedWords){
        String stopStr=null;
        try {
            stopStr = new String(readAllBytes(get("../stop_words.txt")));
        } catch (IOException e) {
             e.printStackTrace();
        }
        List<String> stopWords = new ArrayList<String>(Arrays.asList(stopStr.split(",")));
        normalizedWords.removeAll(stopWords);
        return normalizedWords;
    }

    private static List<String> extractWords(String filename){

        List<String> wordList = null;
        String fileText = null;
        try {
            fileText = new String(readAllBytes(get(filename)));
        } catch (IOException e) {
            e.printStackTrace();
        }
        wordList = new ArrayList<String>(Arrays.asList(fileText.toLowerCase().replaceAll("[^a-zA-Z]+"," ").split(" ")));
        return wordList;
    }

    static class ValueComparator implements Comparator<String> {

        Map<String, Integer> bmap;
        public ValueComparator(Map<String, Integer> base) {
            this.bmap = base;
        }

        public int compare(String a, String b) {
            if (bmap.get(a) >= bmap.get(b)) {
                return -1;
            } else {
                return 1;
            }
        }
    }

}
