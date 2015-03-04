import java.util.*;

public class Frequencies1 implements IFrequencies{
    public Map<String, Integer> top25(List<String> wordList){
        System.out.println("Implementing 1st set of freqs method");
        HashMap<String, Integer> termFreq = new HashMap<String, Integer>();
        for(String word : wordList){
           if(word.length()>1){
             if(termFreq.containsKey(word)){
                 termFreq.put(word, (termFreq.get(word)) + 1);
             }else{
                 termFreq.put(word, 1);
             }
         }
        }
        TreeMap<String,Integer> sorted_termFreq = new TreeMap<String,Integer>(new ValueComparator(termFreq));
        sorted_termFreq.putAll(termFreq);
        return sorted_termFreq;
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