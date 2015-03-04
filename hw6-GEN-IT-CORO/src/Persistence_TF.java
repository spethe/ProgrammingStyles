import java.sql.*;
import java.io.File;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.IOException;
import static java.nio.file.Files.readAllBytes;
import static java.nio.file.Paths.get;

public class Persistence_TF {
    public static void main(String[] args){
        Connection conn = null;
        try {
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection("jdbc:sqlite:test.db");
            conn.setAutoCommit(false);
            createSchema(conn);
            System.out.println("Schema Created");
            extractWordsToDb(conn, args[0]);
            System.out.println("Words extracted and populated in DB");
            conn.commit();
            conn.close();
        } catch(Exception e) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }
    }
    
    private static void createSchema(Connection conn){
        
        Statement createStatement = null;
        try {
                 createStatement = conn.createStatement();
                  createStatement.executeUpdate("drop table if exists documents");
                 String createDoc = "CREATE TABLE documents (ID INT PRIMARY KEY NOT NULL, NAME TEXT NOT NULL);";
                   
                createStatement.executeUpdate(createDoc);
                createStatement.executeUpdate("drop table if exists words");
                String createWords = "CREATE TABLE words" +
                "( ID INT PRIMARY KEY," + "doc_id INT," + "value TEXT);";
                
                createStatement.executeUpdate(createWords);
                
                createStatement.close();
            } catch ( Exception e ) {
                System.err.println( e.getClass().getName() + ": " + e.getMessage() );
                System.exit(0);
            }
    }
    
    private static void extractWordsToDb(Connection conn, String filename) throws SQLException, IOException{
        String fileText = new String(readAllBytes(get(filename)));
        System.out.println("File name is "+ filename);
        List<String> wordList = new ArrayList<String>(Arrays.asList(fileText.toLowerCase().replaceAll("[^a-zA-Z]+"," ").split(" ")));
        System.out.println("Words size is:"+ wordList.size());
        String stopStr = new String(readAllBytes(get("../stop_words.txt")));
        List<String> stopWords = new ArrayList<String>(Arrays.asList(stopStr.split(",")));
        wordList.removeAll(stopWords);
         System.out.println("Words size is:"+ wordList.size());
        String doc = "INSERT INTO documents VALUES(?,?);";
         PreparedStatement prep = conn.prepareStatement(doc);
         prep.setInt(1,1);
         prep.setString(2,filename);
         prep.executeUpdate();
        System.out.println("Inserted into documents");
        String wordQ = "INSERT INTO words VALUES(?,?,?);";
        int wordId = 0;
        PreparedStatement pstat = conn.prepareStatement(wordQ);
        for(String word : wordList){
            if(word.length()>1){
                pstat.setInt(1, ++wordId);
                pstat.setInt(2, 1);
                pstat.setString(3, word);
                pstat.executeUpdate();
            }
         }
        //conn.commit();
        System.out.println("Inserted into words");
    }
}