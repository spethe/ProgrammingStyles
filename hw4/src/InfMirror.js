var fs = require('fs');
var REC_LIMIT = 8192;

function computeFreqs(wordList,stopwords,wordFreq){
	var word = '';
	//Base Case
	if(wordList.length == 0) return;
	else{
		word = wordList.shift();
		if(stopwords.indexOf(word)<= -1 && word.length > 1){ // Not in Stopwords
                 if(wordFreq[word]){
                 	wordFreq[word] = wordFreq[word] + 1;
                 }else{
                 	wordFreq[word] = 1;
                 }
        }
        //Tail Recursion
        computeFreqs(wordList,stopwords,wordFreq);
	}
}

var stopwords = fs.readFileSync("../stop_words.txt","utf8").toString().split(',');
var wordFreq = {};
var myArgs = process.argv
var wordList = fs.readFileSync(myArgs[2], "utf8").toLowerCase().replace(/[^a-zA-Z/]/g," ").split(" ");
wordList = wordList.filter(function(str) {return /\S/.test(str);});

//Calling the Recursion Function
var size =0;
while (size < wordList.length){
    computeFreqs(wordList.slice(size,size+REC_LIMIT),stopwords,wordFreq);
    size = size + REC_LIMIT+1;
}


//Sort and Print
var freqArr = [];
for(var key in wordFreq){
    if (wordFreq.hasOwnProperty(key)) {
        	freqArr.push([key,wordFreq[key]]);
        }
    };
freqArr.sort(function(a,b){return b[1]-a[1]});
for(var i=0;i<25;i++){
            console.log(freqArr[i][0] + " - " + freqArr[i][1]);
}