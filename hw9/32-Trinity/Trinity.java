
import java.util.*;
import java.io.File;
import java.io.IOException;
import static java.nio.file.Files.readAllBytes;
import static java.nio.file.Paths.get;

public class Trinity {
    
    public static void main(String args[]) throws IOException{
        WordModel wmodel = new WordModel(args[0]);
        WordView wview = new WordView(wmodel);
        WordController wcontrol = new WordController(wview,wmodel);
        wcontrol.run();
    }
}

class WordView {
    private WordModel wmodel;
    public WordView(WordModel wmodel){
        this.wmodel = wmodel;
    }
    
    public void render(){
        Map<String,Integer> termFreq = wmodel.getFreq();
        TreeMap<String,Integer> sorted_termFreq = new TreeMap<String,Integer>(new ValueComparator(termFreq));
        sorted_termFreq.putAll(termFreq);
        int count =0;
            for(Map.Entry<String, Integer> entry : sorted_termFreq.entrySet()){
                System.out.println(entry.getKey()+ " - " + entry.getValue());
                if(++count>=25)
                    break;
            }
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

class WordModel{
    
    Map<String,Integer> termFreq;
    List<String> stopwords = new ArrayList<String>();
    public WordModel (String filename) throws IOException {
        this.stopwords= Arrays.asList(new String(readAllBytes(get("../stop_words.txt"))).split(","));
        update(filename);
    }
    
    public void update(String filename) throws IOException{
        termFreq = new HashMap<String,Integer>();
        List<String> wordList = new ArrayList<String>(Arrays.asList(new String(readAllBytes(get(filename))).toLowerCase().replaceAll("[^a-zA-Z]+"," ").split(" ")));
        wordList.removeAll(stopwords);
        for(String word : wordList){
           if(word.length()>1){
             if(termFreq.containsKey(word)){
                 termFreq.put(word, (termFreq.get(word)) + 1);
             }else{
                 termFreq.put(word, 1);
             }
            }
        }
    }
    
    public Map<String,Integer> getFreq(){
        return termFreq;
    }
}


class WordController {
    
    private WordModel wmodel;
    private WordView wview;
    public WordController(WordView wview, WordModel wmodel){
        this.wmodel = wmodel;
        this.wview = wview;
        this.wview.render();
    }
    
    public void run() throws IOException{
        while(true){
            System.out.println("Enter file: (Hit Ctrl+c to exit)");
            Scanner scanner = new Scanner(System.in);
            String filename = scanner.next();
            wmodel.update(filename);
            wview.render();
        }
    }
}