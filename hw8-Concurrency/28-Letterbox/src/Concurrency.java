import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.io.IOException;
import static java.nio.file.Files.readAllBytes;
import static java.nio.file.Paths.get;
import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Comparator;





public class Concurrency {

    public static void main(String[] args) throws InterruptedException {
        System.out.println("Concurrent Word Frequency begins... ");
        Messenger messenger = Messenger.getInstance();
        WordFreqManager wordFreqManager = new WordFreqManager();

        DataStoreManager dataStoreManager = new DataStoreManager();
        List<String> params = new ArrayList<String>();
        params.add(0,args[0]);
        messenger.send(dataStoreManager, new Message("init",wordFreqManager,params));
        WordFreqController wordFreqController = new WordFreqController();
        messenger.send(wordFreqController, new Message("run", dataStoreManager,null));

        List<ActiveObject> actors = new ArrayList<ActiveObject>();
        actors.add(0,wordFreqManager);
        actors.add(1,dataStoreManager);
        actors.add(2,wordFreqController);
        for(ActiveObject actor : actors){
            actor.join();
        }
        System.out.println("All joined; Ending of " + Thread.currentThread().getName());
    }
}

interface ActiveObject {
   public void dispatch(Message take);
   void deliver(Message msg) throws InterruptedException;
   void join() throws InterruptedException;
}

class Message {

    private final String message;
    private final ActiveObject active;
    private final List<String> params;
    private Map<String,Integer> paramMap;

    public Message(String message, ActiveObject active, List<String> params) {
        this.message = message;
        this.active = active;
        this.params = params;
    }

    public String getMessage() {
        return message;
    }

    public ActiveObject getActive() {
        return active;
    }

    public List<String> getParams() {
        return params;
    }

    public void setParamMap(Map<String,Integer> paramMap) {
        this.paramMap = paramMap;
    }

    public Map<String, Integer> getParamMap() {
        return paramMap;
    }
}

class Messenger {
    private static Messenger messenger = null;
    private Messenger(){

    }
    public static Messenger getInstance(){
        if(messenger == null){
            return new Messenger();
        }
        return messenger;
    }

    public void send(ActiveObject ao, Message msg) throws InterruptedException {
        ao.deliver(msg);
    }
}


class DataStoreManager  implements Runnable,ActiveObject{
       private LinkedBlockingQueue queue;
    private boolean over = false;
    private List<String> wordList;
    private ActiveObject wordFreqManager;
    private Messenger messenger = Messenger.getInstance();
    private Thread thread;
    public DataStoreManager() {
        queue = new LinkedBlockingQueue();
        thread = new Thread(this);
        thread.setDaemon(true);
        thread.start();
    }

    @Override
    public void run() {

        while(!over){
            try {
                Message take = (Message) this.queue.take();
                this.dispatch(take);
                if(take.getMessage() == "die"){
                    this.over = true;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public synchronized void  dispatch(Message take) {
        String msg =take.getMessage();
        if(msg == "init"){
            this.wordFreqManager = take.getActive();
            String path = take.getParams().get(0);
            String fileText = null;
            try {
                fileText = new String(readAllBytes(get(path)));
            } catch (IOException e) {
                e.printStackTrace();
            }
            wordList = new ArrayList<String>(Arrays.asList(fileText.toLowerCase().replaceAll("[^a-zA-Z]+", " ").split(" ")));
        } else if(msg == "send_word_freqs"){
            try {
                processWords(take);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else{
            try {
                messenger.send(this.wordFreqManager,take);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void processWords(Message take) throws InterruptedException, IOException {
        List<String> stopWords = new ArrayList<String>(Arrays.asList(new String(readAllBytes(get("../stop_words.txt"))).split(",")));

        for(String word : wordList){
            List<String> oneWord = new ArrayList<String>(1);
            oneWord.add(0,word);
            //Filter stopwords
            if(!stopWords.contains(word)) {
                messenger.send(this.wordFreqManager, new Message("word", null, oneWord));
            }
        }

        messenger.send(this.wordFreqManager,new Message("top25",take.getActive(),null));
    }

    @Override
    public void deliver(Message msg) throws InterruptedException {
        this.queue.put(msg);
    }

    @Override
    public void join() throws InterruptedException {
        this.thread.join();
    }
}

class WordFreqController implements Runnable,ActiveObject{
    private LinkedBlockingQueue queue;
    private boolean over = false;
    private Messenger messenger = Messenger.getInstance();
    ActiveObject dataStorageManager;
    private Thread thread;

    public WordFreqController() {
        queue = new LinkedBlockingQueue();
        dataStorageManager = new DataStoreManager();
        thread = new Thread(this);
        thread.start();
    }

    @Override
    public void run() {
        while(!over){
            try {
                Message take = (Message) this.queue.take();
                this.dispatch(take);
                if(take.getMessage() == "die"){
                    this.over = true;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void dispatch(Message take) {
        String msg = take.getMessage();
        if(msg == "run"){
            dataStorageManager = take.getActive();
            startProcess(take);
        }else if(msg == "top25"){
            display(take);
        }
    }

    private void display(Message take) {

        Map<String,Integer> sortedFreqs = take.getParamMap();
        int count =0;
        for(Map.Entry<String, Integer> entry : sortedFreqs.entrySet()){
            System.out.println(entry.getKey()+ " - " + entry.getValue());
            if(++count>=25)
                break;
        }
        try {
            messenger.send(dataStorageManager,new Message("die",this,null));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        this.over = true;
    }

    private void startProcess(Message take) {
        try {
            messenger.send(dataStorageManager, new Message("send_word_freqs", this, null));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void deliver(Message msg) throws InterruptedException {
        this.queue.put(msg);
    }

    public void join() throws InterruptedException {
        this.thread.join();
    }
}

class WordFreqManager implements Runnable,ActiveObject {
    private LinkedBlockingQueue queue;
    private boolean over = false;
    private Map<String, Integer> freqs;
    private Messenger messenger = Messenger.getInstance();
    private Thread thread = null;
    public WordFreqManager() {
        queue = new LinkedBlockingQueue();
        freqs = new ConcurrentHashMap<String, Integer>();
        thread = new Thread(this);
        thread.start();
    }

    @Override
    public void run() {
        while (!over) {
            try {
                Message take = (Message) this.queue.take();
                this.dispatch(take);
                if (take.getMessage() == "die") {
                    this.over = true;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public synchronized void dispatch(Message take) {
        String msg = take.getMessage();
        if (msg == "word") {
            increment_counter(take,freqs);
        } else if (msg == "top25") {
            top25(take);
        }
    }

    private void top25(Message take) {

        Map<String, Integer> sorted_termFreq = new TreeMap<String, Integer>(new ValueComparator(freqs));
        sorted_termFreq.putAll(freqs);
        
        try {
            Message top25 = new Message("top25", null, null);
            top25.setParamMap(sorted_termFreq);
            messenger.send(take.getActive(),top25);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void increment_counter(Message take, Map<String, Integer> freqs) {
        String word = take.getParams().get(0);
        if (word.length() > 1) {
            if (freqs.containsKey(word)) {
                freqs.put(word, (freqs.get(word)) + 1);
            } else {
                freqs.put(word, 1);
            }
        }

    }

    public void deliver(Message msg) throws InterruptedException {
        this.queue.put(msg);
    }

    public void join() throws InterruptedException {
       this.thread.join();
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

