var fs = require("fs");

var partition = function(filepath){
    var lines = fs.readFileSync(filepath,"utf8").toString().split('\n');
    var splitInput = chunkify(lines,200);
    return splitInput;
}

var chunkify = function(lines, n){
    var chunks = [], i=0, len = lines.length;
    while(i<len){
        chunks.push(lines.slice(i,i+=n));    
    }
    return chunks;
}

var splitWords = function(line){
    var lineStrings = line.join("\n");
    var words = lineStrings.toLowerCase().replace(/[^a-zA-Z/]/g," ").split(" ").filter(function(str){
        return /\S/.test(str);
        }).filter(function(str){return str.length>=2});
    
    //removing stop words
    var stopWords = fs.readFileSync("../stop_words.txt","utf8").split(",");
    
    words = words.filter(function(word){
        if(stopWords.indexOf(word) <= -1) return word;
    });
    
    var simpleCounts = [];
    words.forEach(function(word){
        simpleCounts.push({word:word,count:1});
    });
    return simpleCounts;
}

// var regroup = function(pairs){
//     var mappings = {};
//     pairs.forEach(function(pair){
//         if(pair["word"] in mappings){
//             mappings[pair["word"]].push(pair);
//         }else{
//             mappings[pair["word"]] =[pair];
//         }
//     });
//     mappings = alphaSort(mappings)
//     return mappings;
// }

var cat_regroup = function(pairs){
    var mappings = {};
    mappings["a"] = [];
    mappings["f"] = [];
    mappings["k"] = [];
    mappings["p"] = [];
    mappings["u"] = [];
    
    pairs.forEach(function(pair){
        var keyWord = pair["word"];
        
        if(/\b[a-e]/.test(keyWord)){
            mappings["a"].push(pair);
        }
        if(/\b[f-j]/.test(keyWord)){
            mappings["f"].push(pair);
        }
        
        if(/\b[k-o]/.test(keyWord)){
            mappings["k"].push(pair);
        }
        
        if(/\b[p-t]/.test(keyWord)){
            mappings["p"].push(pair);
        }
        if(/\b[u-z]/.test(keyWord)){
            mappings["u"].push(pair);
        }
    });
    return mappings;
}

//Reduce Function
var countWords = function(splitByWords){
    return splitByWords.reduce(function(countMap,simpleIns){
        var word = simpleIns["word"];
        if(word in countMap){
            countMap[word] = ++countMap[word];
        }else{
            countMap[word] = 1;
        }
        return countMap;
    },{});
}

var items = function(obj) {
    var i, arr = [];
    for(i in obj) {
        arr.push(obj[i]);
    }
    return arr;
};

var sortFreqs = function(freqMap){
		
		var freqArr = [];
        for(var key in freqMap){
            if (freqMap.hasOwnProperty(key)) {
                freqArr.push([key,freqMap[key]]);
            }
        };
        freqArr.sort(function(a,b){return b[1]-a[1]});
        return freqArr;
};

var printOutput = function(freqArr){
    var top25 = "";
    	for(var i=0;i<25;i++){
    	       // top25 +="\n";
                top25 =freqArr[i][0] + " - " + freqArr[i][1];
                console.log(top25);
        }
};

var extend = function(a,b){
    for(var key in b)
        if(b.hasOwnProperty(key))
            a[key] = b[key];
    return a;
};

//Main Function
//Partition
var splitLines = partition(process.argv[2]);

//Map Phase
var splits = []
splits = splits.concat.apply(splits,splitLines.map(splitWords));

//Regroup
var splitsByWords = cat_regroup(splits);

//Reduce

var wordfreqs = items(splitsByWords).map(countWords);
var combinedMap = {};
for(var i =0;i<wordfreqs.length;i++){
    extend(combinedMap,wordfreqs[i]); 
};

//Sort and Print! <Finally!!>
printOutput(sortFreqs(combinedMap));

