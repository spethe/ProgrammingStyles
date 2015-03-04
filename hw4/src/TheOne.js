var fs = require("fs");

function TheOne(value){
	this.value = value;


	this.bindF = function(transform){
		this.value = transform(this.value);
		return this;
	};

};

var read_file = function(filePath){
	return fs.readFileSync(filePath, "utf8");	
};

var normalize = function(data){
	data = data.toLowerCase().replace(/[^a-zA-Z/]/g," ").split(" ");
	data = data.filter(function(str) {
        return /\S/.test(str);
    });
    return data;
};


var removeStopWords = function(data){
	var stopWords = fs.readFileSync("../stop_words.txt","utf8").toString().split(',');
	data = data.filter(function(word){
                if( (stopWords.indexOf(word)> -1) || (word.length<2) ){
                    return false;
                }else return true;
            });
	return data;
};

var calcFrequencies = function(data){
	var freqMap = {};
	data.forEach(function(word){
		if(freqMap[word]) freqMap[word] = freqMap[word]+1;
        else freqMap[word]= 1;
	});
	return freqMap;
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

var printFreqs = function(freqArr){
	for(var i=0;i<25;i++){
            console.log(freqArr[i][0] + " - " + freqArr[i][1]);
    }
};

//Main function
var myArgs = process.argv;
new TheOne(myArgs[2]).bindF(read_file).bindF(normalize).bindF(removeStopWords).bindF(calcFrequencies).bindF(sortFreqs).bindF(printFreqs);
