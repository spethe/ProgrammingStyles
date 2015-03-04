var fs = require("fs");
var allWords = [[],null];
var stopWords = [[],null];

var nonStopWords = [[],function(){
    var rem = [];
   rem = allWords[0].filter(function(word){
        if(stopWords[0].indexOf(word) <= -1) return word;
    });
    return rem;
}];
var uniqueWords = [[],function(){
    var uniq =[];
    var nonStops = nonStopWords[0];
    if(nonStopWords[0].length > 0){
        for(var i = 0; i < nonStopWords[0].length; i++) 
	    {
		    if (uniq.indexOf(nonStops[i]) == -1) uniq.push(nonStops[i]);
	    }
    }
    return uniq;
}];
var counts = [[],function(){
    var occs = [];
   
    var nons = nonStopWords[0];
    var uniqs = uniqueWords[0];
    var no =0;
    uniqs.forEach(function(word){
        
        for(var j = 0; j<nons.length;j++){
            if(word == nons[j]){
                if(!occs[no]) occs[no] =1;
                else occs[no] = occs[no] + 1;
            }
        }
        no++;
    });
    
    return occs;
}];
var sortedWordsAndCounts = [[], function(){
    function _zip(arrays) {
        return arrays[0].map(function(_,i){
            return arrays.map(function(array){return array[i]})
        });
    }
    var sortedCounts = [];
    
    sortedCounts = _zip([uniqueWords[0],counts[0]]);
    sortedCounts.sort(function(a,b){
        return  b[1] - a[1];
    });
    return sortedCounts;
    
}];


//Main Function
var myArgs = process.argv;
allWords[0] = fs.readFileSync(myArgs[2], "utf8").toLowerCase().replace(/[^a-zA-Z/]/g," ").split(" ").filter(function(str){
    return /\S/.test(str);
}).filter(function(str){return str.length>=2});
stopWords[0] = fs.readFileSync("../stop_words.txt").toString().split(",");
console.log("Completed file extractions: Now the SpreadSheet Style-----")
var all_Cols = [allWords,stopWords,nonStopWords,uniqueWords,counts,sortedWordsAndCounts];
var updateCols = function(){
    all_Cols.forEach(function(col){
        if(col[1]!=null){
            
            col[0] = col[1]();
        }
    });    
};

updateCols();
var count = 1;
var tuples = sortedWordsAndCounts[0];
for(var ind=0;ind<25;ind++){
    var tuple = tuples[ind];
    console.log(tuple[0] + " - " + tuple[1]);
}
