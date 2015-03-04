import java.util.*;

public class BarPrinter implements IPrinter{
    public void print(Map<String,Integer> sorted_termFreq){
        
            int count =0;
            for(Map.Entry<String, Integer> entry : sorted_termFreq.entrySet()){
                System.out.println(entry.getKey()+ " | " + entry.getValue());
                if(++count>=25)
                    break;
            }
    }
}