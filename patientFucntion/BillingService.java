package patientfucntion;

import java.sql.*;

public class BillingService {

    public static void viewBills(Connection conn, int patientId) {

        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            ps = conn.prepareStatement(
                "SELECT * FROM vw_billing_summary " +
                "WHERE patient_name=(SELECT full_name FROM users WHERE user_id=?)"
            );

            ps.setInt(1, patientId);

            rs = ps.executeQuery();

            boolean found = false;

            System.out.println("\n===== BILLING DETAILS =====");

            while (rs.next()) {
                found = true;
                System.out.println("Bill ID: " + rs.getInt("bill_id")
                        + " | Total: " + rs.getDouble("total_amount")
                        + " | Status: " + rs.getString("payment_status"));
            }

            if (!found) {
                System.out.println("No bills found.");
            }

        } catch (SQLException e) {
            System.out.println("Database Error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
            } catch (SQLException e) {
                System.out.println("Closing Error: " + e.getMessage());
            }
        }
    }
}