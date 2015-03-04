var fs = require("fs");

var read_file = function(filePath, func){
	var fileContents = fs.readFileSync(filePath, "utf8");
	func(fileContents, removeStopWords);
};

var normalize = function(data, func){
	data = data.toLowerCase().replace(/[^a-zA-Z/]/g," ").split(" ");
	data = data.filter(function(str) {
        return /\S/.test(str);
    });
    func(data, calcFrequencies);
};


var removeStopWords = function(data,func){
	var stopWords = fs.readFileSync("../stop_words.txt","utf8").toString().split(',');
	data = data.filter(function(word){
                if( (stopWords.indexOf(word)> -1) || (word.length<2) ){
                    return false;
                }else return true;
            });
	func(data, sortFreqs)
};

var calcFrequencies = function(data,func){
	var freqMap = {};
	data.forEach(function(word){
		if(freqMap[word]) freqMap[word] = freqMap[word]+1;
        else freqMap[word]= 1;
	});
	func(freqMap, printFreqs);
};

var sortFreqs = function(freqMap,func){
		
		var freqArr = [];
        for(var key in freqMap){
            if (freqMap.hasOwnProperty(key)) {
                freqArr.push([key,freqMap[key]]);
            }
        };
        freqArr.sort(function(a,b){return b[1]-a[1]});
        
        func(freqArr,no_op);
};

var printFreqs = function(freqArr,func){
	for(var i=0;i<25;i++){
            console.log(freqArr[i][0] + " - " + freqArr[i][1]);
    }
    func(null);
};

var no_op = function(func){
	return;
};
var myArgs = process.argv
read_file(myArgs[2],normalize);