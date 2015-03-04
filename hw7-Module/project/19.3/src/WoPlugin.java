import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.*;
import java.util.*;

public class WoPlugin{
   
    public static void main(String args[]){
        
        Properties props = new Properties();
        try{

            InputStream input = new FileInputStream(new File("config.properties"));
            props.load(input);
            IWords words = null;
            IFrequencies freq = null;
            
            switch(props.getProperty("words")){
                case "Words1": words = new Words1();
                                break;
                
                case "Words2": words = new Words2();
                                break;
                default: System.out.println("Sorry! You can only choose Words1 or Words2");
            }
            
            switch(props.getProperty("frequencies")){
                
                case "Frequencies1": freq = new Frequencies1();
                                        break;
                case "Frequencies2": freq = new Frequencies2();
                                        break;
                
                default: System.out.println("Sorry! You can only choose Frequencies1 or Frequencies2");
            }
            
            List<String> wordList = words.extractWords(args[0]);
            Map<String,Integer> sorted_termFreq = freq.top25(wordList);
            int count =0;
            for(Map.Entry<String, Integer> entry : sorted_termFreq.entrySet()){
                System.out.println(entry.getKey()+ " - " + entry.getValue());
                if(++count>=25)
                    break;
            }
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }
}