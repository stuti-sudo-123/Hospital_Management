package connection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ResourseClose 
{
     Connection conn=null;
     PreparedStatement pstmt=null;
     ResultSet rs=null;
     void closeAll(Connection conn, PreparedStatement pstmt, ResultSet rs)
     {
        try
        {
            if(conn!=null)
            {
                conn.close();
            }
        }
        catch(SQLException e)
        {
            e.printStackTrace();
        }
        try
        {
            if(pstmt!=null)
            {
                pstmt.close();
            }
        }
        catch(SQLException e)
        {
            e.printStackTrace();
        }
        try
        {
            if(rs!=null)
            {
                rs.close();
            }
        }
        catch(SQLException e)
        {
            e.printStackTrace();
        }
     }  
}
