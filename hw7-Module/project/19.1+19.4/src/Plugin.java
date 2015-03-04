import java.lang.*;
import java.util.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

public class Plugin{
    
    public static void main(String args[]){
        Properties props = new Properties();
        try{
            InputStream input = new FileInputStream(new File("config.properties"));
            props.load(input);
            Class words1 = Class.forName(props.getProperty("words"));
            Class freqs1 = Class.forName(props.getProperty("frequencies"));
            Class printer1 = Class.forName(props.getProperty("printers"));
            IPrinter printer = (IPrinter)printer1.newInstance();
            IWords words = (IWords)words1.newInstance();
            IFrequencies freq = (IFrequencies)freqs1.newInstance();
            List<String> wordList = words.extractWords(args[0]);
            Map<String,Integer> sorted_termFreq = freq.top25(wordList);
            printer.print(sorted_termFreq);
           
        }catch(Exception ex){
            ex.printStackTrace();
        }
        
    }
}