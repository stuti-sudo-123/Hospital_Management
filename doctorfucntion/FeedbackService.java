package doctorfucntion;
import java.sql.*;

public class FeedbackService {

    public static void viewFeedback(Connection conn, int doctorId) throws Exception {
        PreparedStatement ps = conn.prepareStatement(
            "SELECT u.full_name, pf.rating, pf.comment FROM patient_feedback pf " +
            "JOIN users u ON u.user_id = pf.patient_id " +
            "WHERE pf.doctor_id=?"
        );
        ps.setInt(1, doctorId);
        ResultSet rs = ps.executeQuery();

        System.out.println("\n===== PATIENT FEEDBACK =====");
        boolean found = false;
        while (rs.next()) {
            found = true;
            System.out.println("Patient : " + rs.getString("full_name"));
            System.out.println("Rating  : " + rs.getInt("rating") + "/5");
            System.out.println("Comment : " + rs.getString("comment"));
            System.out.println("---");
        }
        if (!found) System.out.println("No feedback yet.");
    }
}