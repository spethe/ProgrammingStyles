var fs = require("fs");

function Qua(val){
	var value = {};

    this.fns = [val];
	this.bindF = function(transform){
		this.fns.push(transform);
		return this;
	};
    
    this.execute = function(){
        var guard_callable = function(v){
          if(typeof v  === 'function') return v();
          else return v;
        }
        this.fns.forEach(function(fn){
            var guard = guard_callable(value);
            value = fn(guard);
        });
        // console.log(value);
    };
};


var normalize = function(filePath){
    var norm_fn = function(filePath){
        var data = fs.readFileSync(filePath, "utf8");
	    data = data.toLowerCase().replace(/[^a-zA-Z/]/g," ").split(" ");
	    data = data.filter(function(str) {
            return /\S/.test(str);
        });
        return data;
    }
    return norm_fn(filePath);
};


var removeStopWords = function(data){
    var rem_fn = function(data){
        var stopWords = fs.readFileSync("../stop_words.txt","utf8").toString().split(',');
	    data = data.filter(function(word){
                if( (stopWords.indexOf(word)> -1) || (word.length<2) ){
                    return false;
                }else return true;
        });
	    return data;
    }
	return rem_fn(data);
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

//Quarantine and wrap the ouput in a function: 24.3 attempt :-)
var printOutput = function(freqArr){
   var printFreqs = function(freqArr){
        var top25 = "";
    	for(var i=0;i<25;i++){
    	       // top25 +="\n";
                top25 =freqArr[i][0] + " - " + freqArr[i][1];
                console.log(top25);
        }
   }
   return printFreqs(freqArr);
}

var getInput = function(){
    var myArgs = process.argv;
   
    var fn = function(myArgs){
        return myArgs[2];
    }
    return fn(myArgs);
};

//Main function

new Qua(getInput).bindF(normalize).bindF(removeStopWords).bindF(calcFrequencies).bindF(sortFreqs).bindF(printOutput).execute()





