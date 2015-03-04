var fs = require("fs");

function WordFrequencyFramework(){
    var loadEventHandlers = [];
    var workEventHandlers = [];
    var endEventHandlers = [];
    this.registerLoadEvent = function(loadEvent){
        loadEventHandlers.push(loadEvent);
    };
    this.registerWorkEvent = function(workEvent){
        workEventHandlers.push(workEvent);
    };
    this.registerEndEvent = function(endEvent){
        endEventHandlers.push(endEvent);
    };
    this.run = function(filePath){
        loadEventHandlers.forEach(function(loadEvent){
            loadEvent(filePath);
        });
        workEventHandlers.forEach(function(workEvent){
            workEvent();
        });
        endEventHandlers.forEach(function(endEvent){
            endEvent();
        });
    }
}

function StopWordsFilter(wfreqFw){
    var that = this;
    var wfreqFw = wfreqFw;
    var stopWords;
    var setStopWords = function(stopwords){
        stopWords = stopwords;
    }
    this.loadStopWords = function(){
        setStopWords(fs.readFileSync("../stop_words.txt").toString().split(','));
    };
    this.isStopWord = function(word){
        if(stopWords.indexOf(word)> -1) return true;
        else return false;
    }
    wfreqFw.registerLoadEvent(this.loadStopWords);
}

function DataStorage(wfreqFw,stopWordsFilter){
    var that = this;
    var wfreqFw = wfreqFw;
    var data = '';
    var stopWordsFilter = stopWordsFilter;
    var wordHandlers = [];
    var setData = function(tokens){
        data = tokens;
    };
    this.load = function(filePath){
        setData(fs.readFileSync(filePath, "utf8").toLowerCase().replace(/[^a-zA-Z/]/g," "));
    };
    this.tokenize = function(){
            data = data.split(" ");
            data = data.filter(function(str) {
                return /\S/.test(str);
            });
            data = data.filter(function(word){
                if( (stopWordsFilter.isStopWord(word)===true) || (word.length<2) ){
                    return false;
                }else return true;
            });
            data.forEach(function(word){
                wordHandlers.forEach(function(wordEvent){
                    wordEvent(word);
                });
            });
    };
    this.registerWordEvent = function(wordevent){
        wordHandlers.push(wordevent);
    }
    wfreqFw.registerLoadEvent(this.load);
    wfreqFw.registerWorkEvent(this.tokenize);

}

function FrequencyCounter(wfreqFw,dataStorage){
    var wfreqFw = wfreqFw;
    var dataStorage = dataStorage;
    var freqMap = {};
    this.calculateFreqs = function(word){
        if(freqMap[word]) freqMap[word] = freqMap[word]+1;
        else freqMap[word]= 1;
    };

    this.printFrequencies = function(){
        
        var freqArr = [];
        for(var key in freqMap){
            if (freqMap.hasOwnProperty(key)) {
                freqArr.push([key,freqMap[key]]);
            }
        };
        freqArr.sort(function(a,b){return b[1]-a[1]});
        for(var i=0;i<25;i++){
            console.log(freqArr[i][0] + " - " + freqArr[i][1]);
        }
    };
    dataStorage.registerWordEvent(this.calculateFreqs);
    wfreqFw.registerEndEvent(this.printFrequencies);
}
var wfreqFw = new WordFrequencyFramework();
var stopWordsFilter = new StopWordsFilter(wfreqFw);
var data_storage = new DataStorage(wfreqFw,stopWordsFilter);
var frequencyCounter = new FrequencyCounter(wfreqFw,data_storage);
var myArgs = process.argv;
wfreqFw.run(myArgs[2]);