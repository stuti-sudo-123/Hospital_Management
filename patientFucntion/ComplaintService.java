package patientFucntion;

import java.sql.*;
import java.util.Scanner;

public class ComplaintService {

    public static void submitComplaint(Connection conn, int patientId) throws Exception {
        Scanner sc = new Scanner(System.in);

        System.out.print("Enter Complaint: ");
        String desc = sc.nextLine();

        PreparedStatement ps = conn.prepareStatement(
            "INSERT INTO complaints(patient_id,description) VALUES (?,?)"
        );
        ps.setInt(1, patientId);
        ps.setString(2, desc);
        ps.executeUpdate();

        System.out.println("Complaint Submitted!");
    }
}