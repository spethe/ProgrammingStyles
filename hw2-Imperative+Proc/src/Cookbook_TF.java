import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Cookbook_TF {
    public static List<String> stopWords = new ArrayList<String>();
    public static List<String> words = new ArrayList<String>();
    public static Map<String, Integer> termFreq = new HashMap<String, Integer>();
    public static TreeMap<String,Integer> sorted_termFreq = new TreeMap<String,Integer>(new ValueComparator(termFreq));

    public static void main(String[] args) throws FileNotFoundException {
        extractStopWords();
        readAndScanInput(args[0]);
        normalizeTerms();
        calculateFrequencies();
        sortByFrequency();
        printWords();
    }

    private static void printWords() {
        int count =0;
        for(Map.Entry<String, Integer> entry : sorted_termFreq.entrySet()){
            System.out.println(entry.getKey()+ " - " + entry.getValue());
            if(++count>=25)
                break;
        }
    }

    private static void sortByFrequency() {
        sorted_termFreq.putAll(termFreq);
    }

    private static void calculateFrequencies() {
        for(String word : words){
            if(!stopWords.contains(word)){
                if(termFreq.containsKey(word)){
                    termFreq.put(word, (termFreq.get(word)) + 1);
                }else{
                    termFreq.put(word, 1);
                }
            }
        }
    }

    private static void normalizeTerms() {
        List<String> toAdd = new ArrayList<String>();
        Pattern nonAlphaNum = Pattern.compile("[^a-z]");
        for(ListIterator<String> iter = words.listIterator();iter.hasNext();){
            String word = iter.next();
            Matcher m = nonAlphaNum.matcher(word);
                  if(m.find()){
                      iter.remove();
                      char[]termArray = word.toCharArray();
                      StringBuilder newTermBuilder = new StringBuilder();
                      String newTerm ="";

                      for(int i=0, j=0;i<termArray.length;i++){
                          if(Character.isLetter(termArray[i])){
                              newTerm = newTermBuilder.append(termArray[i]).toString();
                          }else{

                              if(newTerm.matches("[a-z]+") && newTerm.length()>1){
                                  toAdd.add(newTerm);
                                  newTermBuilder.setLength(0);
                                  newTerm="";
                              }else continue;
                          }
                      }
                      if((newTerm.length()>1)&& (!stopWords.contains(newTerm))){
                          toAdd.add(newTerm);
                          newTermBuilder.setLength(0);
                          newTerm="";
                      }
                  }
          }
        words.addAll(toAdd);
    }

    private static void readAndScanInput(String filename) throws FileNotFoundException {
        Scanner corpusFileScanner = new Scanner(new File(filename));
        while(corpusFileScanner.hasNext()) {
            words.add(corpusFileScanner.next().toLowerCase());
        }
    }

    private static void extractStopWords() throws FileNotFoundException{
        Scanner stopWordFileScanner = new Scanner(new File("../stop_words.txt")).useDelimiter(",");

        while(stopWordFileScanner.hasNext()){
            String nextStop = stopWordFileScanner.next();
            if(!stopWords.contains(nextStop)){  // To make it idempotent, every time this method will be called, only new stop words from the file will be added
                stopWords.add(nextStop);
            }
        }
    }

    static class ValueComparator implements Comparator<String> {

        Map<String, Integer> baseMap;
        public ValueComparator(Map<String, Integer> base) {
            this.baseMap = base;
        }

        public int compare(String a, String b) {
            if (baseMap.get(a) >= baseMap.get(b)) {
                return -1;
            } else {
                return 1;
            }
        }
    }
}
