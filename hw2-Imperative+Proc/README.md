Assume, present working directory to be: /workspace/project2/src
***For Pipeline&Cookbook***
Steps for executing the program: 
1. First, compile the java file by going to the src directory and executing command: javac Cookbook_TF.java
2. Then after compiling run the java code with the following command: java Cookbook_TF pride-and-prejudice.txt
|||,
javac Pipeline_TF.java
**** java Pipeline_TF pride-and-prejudice.txt ../stop_words.txt ****

***For CodeGolf to work with 3rd party libraries do the following****
javac -cp "*" CodeGolf_TF.java
java -cp *:. CodeGolf_TF pride-and-prejudice.txt