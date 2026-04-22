package connection;

import java.sql.Connection; 
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection 
{
    public static Connection getConnection()
    {
    	String driver="com.mysql.cj.jdbc.Driver";
        String url="jdbc:mysql://localhost:3306/svnit";
        String username="root";
        String password="1234";
        Connection con=null;
        try 
        {
        	Class.forName(driver);
            con= DriverManager.getConnection(url, username,password);
        }
        catch(ClassNotFoundException ex)
        {
        	ex.printStackTrace();
        }
        catch (SQLException ex) 
        {
            ex.printStackTrace();
        }
		return con;
    }
} 