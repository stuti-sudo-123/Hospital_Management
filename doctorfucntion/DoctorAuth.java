package doctorfucntion;
import java.sql.*;
import java.util.Scanner;

public class DoctorAuth {

    public static int login(Connection conn) throws Exception {
        Scanner sc = new Scanner(System.in);
        System.out.print("Email: ");
        String email = sc.nextLine();
        System.out.print("Password: ");
        String password = sc.nextLine();

        PreparedStatement ps = conn.prepareStatement(
            "SELECT user_id FROM users WHERE email=? AND password_hash=? AND role='DOCTOR'"
        );
        ps.setString(1, email);
        ps.setString(2, password);
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            System.out.println("Login Successful!");
            return rs.getInt("user_id");
        } else {
            throw new Exception("Invalid Login!");
        }
    }
}