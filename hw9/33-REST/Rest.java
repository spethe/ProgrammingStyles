import java.util.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import static java.nio.file.Files.readAllBytes;
import static java.nio.file.Paths.get;

public class Rest {
    public static Map<String,Handler> handlerMap = new HashMap<String,Handler>();
    public static Map<String,Map<String,Integer>> database = new HashMap<String,Map<String,Integer>>();
    public static Scanner scanner = new Scanner(System.in);
    public static void main(String[] args) throws IOException {
        setHandlerMap();
	    Req req = new Req("get","default",null);
        while(true){
            State state = handleReq(req);
            req = renderAndGetInput(state);
        }
    }

    private static void setHandlerMap() throws IOException {
        handlerMap.put("post_execution", new QuitHandler());
        handlerMap.put("get_default", new DefaultGetHandler());
        handlerMap.put("get_file_form", new UploadGetHandler());
        handlerMap.put("post_file", new UploadPostHandler());
        handlerMap.put("get_word", new WordGetHandler());
    }

    private static Req renderAndGetInput(State state) {
        System.out.println(state.getStatus());
        Map<String,Req> links = state.getReqMapper();
       if(links.containsKey("0")){ //Only 1 possible further state
            Req preq = links.get("0");
            if(preq.getOp() == "post"){
                String file = scanner.nextLine();
                preq.setData(file);
                return links.get("0");
            }else{
                return links.get("0");
            }
        }else{
            String option = scanner.nextLine();
            if(links.containsKey(option)){
                return links.get(option);
            }else{
                return new Req("get","default",null);
            }
        }
    }

    private static State handleReq(Req req) {
        String handle = handler_key(req.getOp(), req.getRes());
        //Check for handle and add check in upload post
        Handler handler;
        if(handlerMap.containsKey(handle)){
            handler = handlerMap.get(handle);
            return handler.handle(req.getData());
        }else{
            handler = handlerMap.get("get_default");
            return handler.handle(req.getData());
        }
    }

    private static String handler_key(String op, String res) {
        return op+"_"+res;
    }
}

//===

class Req {

    private final String op;
    private final String res;
    private Object data;

    public Req(String op, String res, Object data) {
        this.op = op;
        this.res = res;
        this.data = data;
    }

    public String getOp() {
        return op;
    }

    public String getRes() {
        return res;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data){
        this.data = data;
    }
}

//===
class State {
    String status;
    //Object data; //Make 2 fields: request and Map<String,Request> or only 1 Map<String,Request>
    Map<String,Req> reqMapper;
    public State(String status, Map<String,Req> data){
        this.status = status;
        this.reqMapper = data;
    }

    public String getStatus() {
        return status;
    }

    public Map<String,Req> getReqMapper() {
        return reqMapper;
    }
}

//===
interface Handler {
    public State handle(Object data);
}

//==
class DefaultGetHandler implements Handler {
    @Override
    public State handle(Object data) {
        HashMap<String, Req> links = new HashMap<String, Req>();
        String menu = "Options: \n1.Quit\n2.Upload File";
        links.put("1",new Req("post","execution",null));
        links.put("2",new Req("get","file_form",null));
        return new State(menu,links);
    }
}

//==
class QuitHandler implements Handler {
    @Override
    public State handle(Object data) {
        System.exit(0);
        return null;
    }
}
//==
class UploadGetHandler implements Handler {
    @Override
    public State handle(Object data) {
        Map<String,Req> reqMapper = new HashMap<String,Req>();
        reqMapper.put("0",new Req("post","file",null));
        return new State("Name of file to upload?", reqMapper);
    }
}
//==
class UploadPostHandler implements Handler {
    private final List<String> stopwords;
    private final Handler wordGetHandler;
    public UploadPostHandler() throws IOException {
        this.stopwords= Arrays.asList(new String(readAllBytes(get("../stop_words.txt"))).split(","));
        wordGetHandler = new WordGetHandler();
    }


    @Override
    public State handle(Object data) {
        String filename = (String)data;
        if(Rest.database.containsKey(filename)){
            Map<String, Integer> dMap = new HashMap<String, Integer>();
            dMap.put(filename,0);
            return wordGetHandler.handle(dMap);
        }
        Map<String,Integer> termFreq = new HashMap<String,Integer>();
        List<String> wordList = null;
        try {
            wordList = new ArrayList<String>(Arrays.asList(new String(readAllBytes(get(filename))).toLowerCase().replaceAll("[^a-zA-Z]+", " ").split(" ")));
        } catch (IOException e) {
            e.printStackTrace();
        }
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
        TreeMap<String,Integer> sorted_termFreq = new TreeMap<String,Integer>(new ValueComparator(termFreq));
        sorted_termFreq.putAll(termFreq);
        Rest.database.put(filename,sorted_termFreq);
        Map<String, Integer> dataMap = new HashMap<String, Integer>();
        dataMap.put(filename,0);
        return wordGetHandler.handle(dataMap);
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

//==
class WordGetHandler implements Handler {
    @Override
    public State handle(Object data) {
        Map<String,Integer> params = (Map<String, Integer>) data;
        HashMap<String, Req> links = new HashMap<String, Req>();
        String filename = params.keySet().iterator().next();
        Integer pos = params.get(filename);
        Tuple tuple = getWordTuple(filename,pos);
        String rep="\n" + tuple.getWord() + " - " + tuple.getFreq() + "\n";
        rep+="\n\nWhat would you like to do next? ";
        rep+="\n1.Quit" + "\n2.Upload Another File"+ "\n3.See the next most-freq occurring word";
        links.put("1",new Req("post","execution",null));
        links.put("2",new Req("get","file_form",null));
        Map<String, Integer> nextWordMap = new HashMap<String, Integer>();
        nextWordMap.put(filename,pos+1);
        links.put("3",new Req("get","word",nextWordMap));
        return new State(rep,links);
    }

    private Tuple getWordTuple(String filename, Integer pos) {
        Map<String,Integer> fileMap = Rest.database.get(filename);
        String word = (String) fileMap.keySet().toArray()[pos];
        return new Tuple(word, (Integer) fileMap.values().toArray()[pos]);
    }

    private class Tuple {
        private final String word;
        private final Integer freq;

        public Tuple(String word, Integer integer) {
            this.word = word;
            this.freq = integer;
        }

        public Integer getFreq(){
            return freq;
        }

        public String getWord() {
            return word;
        }
    }
}








