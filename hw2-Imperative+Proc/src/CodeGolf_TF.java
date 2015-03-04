import com.google.common.collect.HashMultiset;
import com.google.common.collect.ImmutableMultiset;
import com.google.common.collect.Multisets;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.*;
import org.apache.commons.lang3.text.StrTokenizer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.nio.file.Files.readAllBytes;
import static java.nio.file.Paths.get;

public class CodeGolf_TF {
    public static void main(String args[]) throws IOException {
        List<String> wordList = new ArrayList<String>(Arrays.asList(new StrTokenizer(new String(readAllBytes(get(args[0]))).toLowerCase().replaceAll("[^a-zA-Z]+"," ")).getTokenArray()));
        wordList.removeAll(Arrays.asList(new StrTokenizer(new String(readAllBytes(get("../stop_words.txt"))).replaceAll("[^a-zA-Z]+"," ")).getTokenArray()));
        CollectionUtils.filter(wordList, new Predicate<String>() {
            @Override
            public boolean evaluate(String s) {
                if (s.length() < 2) return false;
                else return true;
            }
        });
        ImmutableMultiset<String> sortedWords = Multisets.copyHighestCountFirst(HashMultiset.create(wordList));
        int count=0;
        for (String word : sortedWords.elementSet()) {
            System.out.println(word + " - " + sortedWords.count(word));
            if (++count >= 25)
                break;
        }
    }
}