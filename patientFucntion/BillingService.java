package patient;

import java.sql.*;

public class BillingService {

    public static void viewBills(Connection conn, int patientId) throws Exception {
        PreparedStatement ps = conn.prepareStatement(
            "SELECT * FROM vw_billing_summary WHERE patient_name=(SELECT full_name FROM users WHERE user_id=?)"
        );
        ps.setInt(1, patientId);

        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            System.out.println("Bill ID: " + rs.getInt("bill_id")
                    + " | Total: " + rs.getDouble("total_amount")
                    + " | Status: " + rs.getString("payment_status"));
        }
    }
}