package doctorfucntion;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import connection.DBConnection;

public class Patient 
{
    int patientId;
    Connection conn=null;
    PreparedStatement pstmt=null;
    ResultSet rs=null;
    String sql= "select * from patient where patient_id=?";
    try
    {
        conn=DBConnection.getConnection();
        pstmt=conn.prepareStatement(sql);
        pstmt.setInt(1, patientId);
        rs=pstmt.executeQuery();
        if(rs.next())
        {
            String name = rs.getString("name");
            int age = rs.getInt("age");
        }
   }
   catch(SQLException e)
   {
       e.printStackTrace();
   }

}
