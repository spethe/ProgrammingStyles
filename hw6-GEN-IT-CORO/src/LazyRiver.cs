
using System;
using System.Collections.Generic;
using System.IO;
using System.Text;
using System.Text.RegularExpressions;
using System.Linq;

namespace hw6{
    
    class LazyRiver{
        
        static IEnumerable<string> GetWordEnum(string filename){
            string wordStr = File.ReadAllText(filename).ToLower();
            string pattern = "[^a-zA-Z]+";
            string replacement = " ";
            Regex rgx = new Regex(pattern);
            string result = rgx.Replace(wordStr, replacement);
            string[] split = result.Split(' ');
            foreach (string word in split){
                if(word.Length > 1)
                    yield return word;
            }
      
        }
        
        static IEnumerable<string> GetNonStopWordEnum(string filename){
            string [] stopWords = File.ReadAllText("../stop_words.txt").ToLower().Split(',');
            List<string> stopWordList = new List<string>(stopWords);
            foreach(string word in GetWordEnum(filename)){
                if(!stopWordList.Contains(word)) yield return word;
            }
        }
        
        static IEnumerable<KeyValuePair<string,int>> GetFrequencyCounts(string filename){
            Dictionary<string, int> wordFreqs = new Dictionary<string, int>();
            
            foreach(string word in GetNonStopWordEnum(filename)){
                int defVal = 1;
                if(wordFreqs.TryGetValue(word, out defVal)){
                    wordFreqs[word] = wordFreqs[word] + 1;
                }else{
                    wordFreqs[word] = 1;
                }
            }
            IEnumerable<KeyValuePair<string,int>> freqCounts = wordFreqs.OrderByDescending(key => key.Value);
            foreach(KeyValuePair<string,int> freq in freqCounts){
                yield return freq;
            }
        }
        
        static void Main(string[] args){
            Console.WriteLine("Frequency counts are:");
            int i=1;
            foreach (KeyValuePair<string,int> freq in GetFrequencyCounts(args[0]))
            {
                Console.WriteLine("{0} - {1}", freq.Key, freq.Value);
                if(++i >25){
                    break;
                }
            }
        }
    }
}