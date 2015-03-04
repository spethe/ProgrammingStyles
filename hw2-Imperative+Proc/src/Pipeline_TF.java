import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by swanand on 1/15/2015.
 */
public class Pipeline_TF {
    public static void main(String args[]) throws FileNotFoundException {
        List<String> filenames = Arrays.asList(new String[]{args[0],args[1]});
        printFirstQuarter(descendingSort(calculateFreq(filterStopWords(normalize(readInputFile(filenames))))));
    }

    private static void printFirstQuarter(Map<String, Integer> freqMap) {
        int count =0;
        for(Map.Entry<String, Integer> entry : freqMap.entrySet()){
            System.out.println(entry.getKey()+ " - " + entry.getValue());
            if(++count>=25)
                break;
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
                if(termFreq.containsKey(word)){
                    termFreq.put(word, (termFreq.get(word)) + 1);
                }else{
                    termFreq.put(word, 1);
                }
        }
        return termFreq;
    }

    private static List<String> filterStopWords(List<String> normalizedWords) throws FileNotFoundException {
        String stopFilename = normalizedWords.remove(normalizedWords.size()-1);
        Scanner stopWordFileScanner = new Scanner(new File(stopFilename)).useDelimiter(",");
        ArrayList<String> stopWords = new ArrayList<String>();
        while(stopWordFileScanner.hasNext()){
            String nextStop = stopWordFileScanner.next();
            if(!stopWords.contains(nextStop)){  // To make it idempotent, every time this method will be called, only new stop words from the file will be added
                stopWords.add(nextStop);
            }
        }
        normalizedWords.removeAll(stopWords);
        return normalizedWords;
    }

    private static List<String> normalize(List<String> words) {
        String stopFilename = words.remove(words.size() - 1);
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
                if((newTerm.length()>1)){
                    toAdd.add(newTerm);
                    newTermBuilder.setLength(0);
                    newTerm="";
                }
            }
        }
        words.addAll(toAdd);
        words.add(words.size(), stopFilename);
        return words;
    }

    private static List<String> readInputFile(List<String> filenames) throws FileNotFoundException {
        Scanner inputFileScanner = new Scanner(new File(filenames.get(0)));
        ArrayList<String> words = new ArrayList<String>();
        while(inputFileScanner.hasNext()) {
            words.add(inputFileScanner.next().toLowerCase());
        }
        words.add(words.size(),filenames.get(1));
        return words;
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
