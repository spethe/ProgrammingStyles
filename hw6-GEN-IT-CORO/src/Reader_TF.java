import java.sql.*;
public class Reader_TF{
     public static void main(String[] args){
         Connection conn = null;
         try{
             Class.forName("org.sqlite.JDBC");
             conn = DriverManager.getConnection("jdbc:sqlite:test.db");
             String getR = "SELECT value,COUNT(*) as C FROM words GROUP BY value ORDER BY C DESC";
             PreparedStatement ps = conn.prepareStatement(getR);
             ResultSet rset = ps.executeQuery();
             System.out.println("select query executed");
             int count =1;
             while(rset.next() && count<=25){
                System.out.print(rset.getString("value") + " - " + rset.getInt("C"));
                System.out.println();
                count++;
             }   
             conn.close();
         }catch(Exception e) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }
     }         
}