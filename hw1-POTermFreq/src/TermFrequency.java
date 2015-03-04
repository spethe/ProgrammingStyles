import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.*;

/**
 * Created by swanand on 1/7/2015.
 */
public class TermFrequency {

    public static void main(String[] args) throws FileNotFoundException {
        
        Map<String, Integer> termFreqMap = new HashMap<String, Integer>();
        
        //Prepare Stop Words list
        List<String> stopWordList = extractStopWords();
        
        //Read the file given as an input
        Scanner corpusFileScanner = new Scanner(new File(args[0]));     

        //Scan the input file for term frequency
        while(corpusFileScanner.hasNext()){
            String term = corpusFileScanner.next().toLowerCase();
            char[]termArray = term.toCharArray();
            StringBuilder newTermBuilder = new StringBuilder();
            String newTerm ="";
            
            for(int i=0, j=0;i<termArray.length;i++){
                if(Character.isLetter(termArray[i])){
                    newTerm = newTermBuilder.append(termArray[i]).toString();
                }else{

                    if(newTerm.matches("[a-z]+") && newTerm.length()>1){
                        populateFrequencyFor(newTerm,stopWordList,termFreqMap);
                        newTermBuilder.setLength(0);
                        newTerm="";
                    }else continue;
                }
            }
            
            if(newTerm.length()>1){
                populateFrequencyFor(newTerm,stopWordList ,termFreqMap);
            }
        }

        TreeMap<String,Integer> sorted_freq_map = new TreeMap<String,Integer>(new ValueComparator(termFreqMap));
        sorted_freq_map.putAll(termFreqMap);
        int count =0;
        for(Map.Entry<String, Integer> entry : sorted_freq_map.entrySet()){
            System.out.println(entry.getKey()+ " - " + entry.getValue());
            if(++count>=25)
                break;
        }

    }

    private static List<String> extractStopWords() throws FileNotFoundException{
        List<String> stopWordList = new ArrayList<String>();
        Scanner stopWordFileScanner = new Scanner(new File("../stop_words.txt")).useDelimiter(",");
        while(stopWordFileScanner.hasNext()){
            stopWordList.add(stopWordFileScanner.next());
        }
        return stopWordList;
    }
    
    private static void populateFrequencyFor(String term, List<String> stopWordList,Map<String,Integer> termFreqMap){
        if(!stopWordList.contains(term)){
            Integer freq = termFreqMap.containsKey(term) ? termFreqMap.put(term, (termFreqMap.get(term)) + 1) : termFreqMap.put(term, 1);
        }
    }
    
    static class ValueComparator implements Comparator<String> {

        Map<String, Integer> base;
        public ValueComparator(Map<String, Integer> base) {
            this.base = base;
        }

        public int compare(String a, String b) {
            if (base.get(a) >= base.get(b)) {
                return -1;
            } else {
                return 1;
            }
        }
    }
}
