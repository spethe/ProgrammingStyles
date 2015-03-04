
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import static java.nio.file.Files.readAllBytes;
import static java.nio.file.Paths.get;
import java.util.concurrent.LinkedBlockingQueue;

/* Created by swanand on 2/28/2015.
 */
public class ConUnits {
    public static List<String> stopWords = new ArrayList<String>();
    private static List<String> wordList = new ArrayList<String>();
    public static void main(String args[]) throws InterruptedException {
        try {
            stopWords = new ArrayList<String>(Arrays.asList(new String(readAllBytes(get("../stop_words.txt"))).split(",")));
            wordList = new ArrayList<String>(Arrays.asList(new String(readAllBytes(get(args[0]))).toLowerCase().replaceAll("[^a-zA-Z]+", " ").split(" ")));
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Concurrent count phase starts");
        for(String word : wordList){
                DataSpaces.wordspace.put(word);
        }
        List<Worker> workers = new ArrayList<Worker>();
        for(int i=0;i<3;i++){
                    workers.add(new Worker());
        }
        
        for(Worker w : workers){
            w.join();
        }
        
        List<Map<String,Integer>> freqs = new ArrayList<Map<String, Integer>>();
        DataSpaces.freqspace.drainTo(freqs);
        DataSpaces.alphafreqspace = Collections.synchronizedList(freqs);
        
        System.out.println("Concurrent Merge Phase starts");
        //Merge Phase starts :Concurrently merging on the basis of starting letter...
        List<Merger> mergers = new ArrayList<Merger>();
        Merger aMerger = new Merger("[a-h]");
        Merger iMerger = new Merger("[i-q]");
        Merger rMerger = new Merger("[r-z]");
        mergers.add(aMerger);
        mergers.add(iMerger);
        mergers.add(rMerger);
        for(Merger mer : mergers){
            mer.join();
        }
        Map<String,Integer> finalFreqs = new HashMap<String, Integer>();
        for(Map<String,Integer> part : DataSpaces.catAlphaFreqspace){
            finalFreqs.putAll(part);
        }
        TreeMap<String,Integer> sorted_termFreq = new TreeMap<String,Integer>(new ValueComparator(finalFreqs));
        sorted_termFreq.putAll(finalFreqs);

        int count =0;
        for(Map.Entry<String, Integer> entry : sorted_termFreq.entrySet()){
            System.out.println(entry.getKey()+ " - " + entry.getValue());
            if(++count>=25)
                break;
        }
        System.exit(0);
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

final class DataSpaces{
    public static final LinkedBlockingQueue wordspace = new LinkedBlockingQueue<String>();
    public static final LinkedBlockingQueue freqspace = new LinkedBlockingQueue<Map<String,Integer>>();
    public static List<Map<String,Integer>> alphafreqspace;
    public static final List<Map<String,Integer>> catAlphaFreqspace = Collections.synchronizedList(new ArrayList<Map<String,Integer>>());


    private DataSpaces(){
        //Cannot Instantiate

    }
}

class Worker implements Runnable{
       private Thread thread;
    public Worker() {
        thread = new Thread(this);
        thread.start();
    }

    @Override
    public void run() {
        processWords();
    }

    private synchronized void processWords(){
        Map<String,Integer> wordFreqs = new HashMap<String, Integer>();
       
        while(true){
            String word = null;
            try{
                     word = (String) DataSpaces.wordspace.poll(10,TimeUnit.SECONDS);
                     if(word == null) break;
            } catch (InterruptedException e) {
                //e.printStackTrace();
                break;
            }
            
            if((!ConUnits.stopWords.contains(word) && (word.length()>1))){
                    if(wordFreqs.containsKey(word)){
                        wordFreqs.put(word, wordFreqs.get(word)+1);
                    }else{
                        wordFreqs.put(word, 1);
                    }
            }
           
        }
        try {
            DataSpaces.freqspace.put(wordFreqs);
        } catch (InterruptedException e) {
            System.out.println("Interrupted during putting freq maps");
            e.printStackTrace();
        }
    }

    public void join() throws InterruptedException{
        this.thread.join();
    }   
}


class Merger implements Runnable{
    
    private final String alpha;
    private Thread thread;
    public Merger(String alpha) {
        this.alpha = alpha;
        thread = new Thread(this);
        thread.start();
    }



    @Override
    public void run() {
        
        Map<String,Integer> part = new HashMap<String, Integer>();
        int i=0;
        for(Map<String,Integer>partial : DataSpaces.alphafreqspace) {
            for (String key : partial.keySet()) {
                if (key.matches(alpha + "\\w*")) {
                    if (part.containsKey(key)) {
                        part.put(key, part.get(key) + partial.get(key));
                    } else {
                        part.put(key, partial.get(key));
                    }
                }
            }
        }
        DataSpaces.catAlphaFreqspace.add(part);
    }

    public void join() throws InterruptedException {
        this.thread.join();
    }
}

