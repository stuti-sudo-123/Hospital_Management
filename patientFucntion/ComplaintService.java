package patientFucntion;

import java.sql.*;
import java.util.Scanner;

public class ComplaintService {

    public static void submitComplaint(Connection conn, int patientId) {

        Scanner sc = new Scanner(System.in);
        PreparedStatement ps = null;

        try {
            System.out.print("Enter Complaint: ");
            String desc = sc.nextLine();

            if (desc == null || desc.trim().isEmpty()) {
                System.out.println("Complaint cannot be empty!");
                return;
            }

            ps = conn.prepareStatement(
                "INSERT INTO complaints(patient_id,description) VALUES (?,?)"
            );

            ps.setInt(1, patientId);
            ps.setString(2, desc);

            int rows = ps.executeUpdate();

            if (rows > 0) {
                System.out.println("Complaint Submitted Successfully!");
            } else {
                System.out.println("Failed to submit complaint.");
            }

        } catch (SQLException e) {
            System.out.println("Database Error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        } finally {
            try {
                if (ps != null) ps.close();
            } catch (SQLException e) {
                System.out.println("Closing Error: " + e.getMessage());
            }
        }
    }
}