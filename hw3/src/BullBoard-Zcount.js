var fs = require("fs");

function EventManager(){
	var subscriptions = {};
	
	this.subscribe = function(eventType, handler){
		var subKeys = Object.keys(subscriptions);
		if(subKeys.indexOf(eventType) > -1){
			subscriptions[eventType].push(handler);
		}else{
			subscriptions[eventType] = [handler];
		}
	};

	this.publish = function(event){
		
		var subKeys = Object.keys(subscriptions);
		var evType = event[0];
		
		if(subKeys.indexOf(evType) >= -1){
				subscriptions[evType].forEach(function(handler){
				handler(event);
			});
		}
	}
}


function WordFreqApp(em){
	var eventManager = em;
	eventManager.subscribe('run',function(event){
		var filePath = event[1];
		eventManager.publish(['load',filePath]);
		eventManager.publish(['start',null]);
	});
	eventManager.subscribe('eof',function(event){
		eventManager.publish(['print',null]);
	});
}

function DataStorage(em){
	
	var eventManager = em;
	var data = '';
	eventManager.subscribe('load', function(event){
		data = fs.readFileSync("pride-and-prejudice.txt", "utf8").toLowerCase().replace(/[^a-zA-Z/]/g," ");
	});
	eventManager.subscribe('start', function(event){
		data = data.split(" ");
            data = data.filter(function(str) {
                return /\S/.test(str);
            });
            data = data.filter(function(word){
                if(word.length<2){
                    return false;
                }else return true;
            });
            data.forEach(function(word){
            	eventManager.publish(['word', word]);
            })
            eventManager.publish(['eof',null]);
	});
}

function StopWordFilter(em){
	var stopWords = [];
	var eventManager = em;
	eventManager.subscribe('load', function(event){
		stopWords = fs.readFileSync("../stop_words.txt").toString().split(',');
	});
	eventManager.subscribe('word', function(event){
		if(stopWords.indexOf(event[1])<=-1) eventManager.publish(['valid_word', event[1]]);
	});
}

function WordFreqCounter(em){
	var freqMap = {};
	var eventManager = em;
	eventManager.subscribe('valid_word', function(event){
		var validWord = event[1];
		if(freqMap[validWord]){
			freqMap[validWord] = freqMap[validWord]+1;
		} 
        else freqMap[validWord]= 1;
	});
	eventManager.subscribe('print', function(event){
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
	});
}

function ZCounter(em){
    var eventManager = em;
    var zcount = 0;
    eventManager.subscribe('valid_word', function(event){
        	var validWord = event[1];
        	zcount+=(validWord.match(/z/)||[]).length;
    });
    
    eventManager.subscribe('print', function(event){
        console.log("The # of occurrences of z are:  " + zcount);
    });
}

em = new EventManager();
dstore = new DataStorage(em);
swFilter = new StopWordFilter(em);
freqCounter = new WordFreqCounter(em);
zcounter = new ZCounter(em);
wfapp = new WordFreqApp(em);
var myArgs = process.argv;
em.publish(['run',myArgs[2]]);