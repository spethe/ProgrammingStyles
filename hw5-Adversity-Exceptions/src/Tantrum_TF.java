import java.io.IOException;
import java.util.*;
import static java.nio.file.Files.readAllBytes;
import static java.nio.file.Paths.get;

/**
 * Created by swanand on 2/6/2015.
 */
public class Tantrum_TF {
    public static void main(String args[]) throws IOException {
            String filename = null;

            if(args.length <= 0) throw new NoArgumentException("Oh! C'mon atleast give the input file!");
            filename = args[0];
            Map<String, Integer> descFreqMap = descendingSort(calculateFreq(filterStopWords(extractWords(filename))));
            if(!(descFreqMap instanceof Map))throw new InstanceMismatchException("Thumbs down! No collection detected");
            if(descFreqMap!=null || !descFreqMap.isEmpty()){
                if(descFreqMap.size() < 25) throw new EmptyException("Less than 25! You are empty for me");
                int count =0;
                for(Map.Entry<String, Integer> entry : descFreqMap.entrySet()){
                    System.out.println(entry.getKey()+ " - " + entry.getValue());
                    if(++count>=25)
                        break;
                }
            }else{
               throw new EmptyException("Null/Empty map for you!");
            }
        }

    private static Map<String, Integer> descendingSort(Map<String, Integer> termFreq) {
            if(termFreq==null || termFreq.isEmpty()){
                throw new EmptyException("Null/Empty map !!");
            }
            if(!(termFreq instanceof Map<?,?>)){
                throw new InstanceMismatchException("Not a map. Not a clue how to process it.");
            }
            TreeMap<String,Integer> sorted_termFreq = new TreeMap<String,Integer>(new ValueComparator(termFreq));
            sorted_termFreq.putAll(termFreq);
            return sorted_termFreq;
        }

    private static Map<String, Integer> calculateFreq(List<String> words) {
            //Defensive Coding
            if(words==null || words.isEmpty()){
                throw new EmptyException("Null/Empty words !!");
            }
            if(!(words instanceof List<?>)){
                throw new InstanceMismatchException("Not a list. Not a clue how to process it!");
            }
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

    private static List<String> filterStopWords(List<String> normalizedWords) throws IOException {
            //Defensive Coding
            if(normalizedWords==null || normalizedWords.isEmpty()){
                throw new EmptyException("Null/Empty words. How to normalize them??!!");
            }
            if(!(normalizedWords instanceof List<?>)){
                throw new InstanceMismatchException("Not a list. Not a clue how to process it!");
            }
            String stopStr=null;

            stopStr = new String(readAllBytes(get("../stop_words.txt")));

            List<String> stopWords = new ArrayList<String>(Arrays.asList(stopStr.split(",")));
            normalizedWords.removeAll(stopWords);
            return normalizedWords;
    }

    private static List<String> extractWords(String filename) throws IOException {
            if(filename.length() ==0 || filename == null){
                throw new EmptyException("Null/Empty filename provided. Cannot extract words !!");
            }
            if(!(filename instanceof String)){
                throw new InstanceMismatchException("Filename not a string. Don't know how to read it!");
            }
            List<String> wordList = null;
            String fileText = null;

            fileText = new String(readAllBytes(get(filename)));

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

    private static class NoArgumentException extends RuntimeException{
            public NoArgumentException(String message) {
                super(message);
            }
        }

    private static class InstanceMismatchException extends RuntimeException {
        public InstanceMismatchException(String s) {
            super(s);
        }
    }

    private static class EmptyException extends RuntimeException {
        public EmptyException(String s) {
            super(s);
        }
    }
}
