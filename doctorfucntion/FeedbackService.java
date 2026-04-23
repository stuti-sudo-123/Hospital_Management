package doctorfucntion;
import java.sql.*;

public class FeedbackService {

    public static void viewFeedback(Connection conn, int doctorId) throws Exception {
        PreparedStatement ps = conn.prepareStatement(
            "SELECT u.full_name, f.rating, f.comment, f.submitted_at " +
            "FROM feedback f " +
            "JOIN users u ON u.user_id = f.patient_id " +
            "WHERE f.doctor_id=?"
        );
        ps.setInt(1, doctorId);
        ResultSet rs = ps.executeQuery();

        System.out.println("\n===== PATIENT FEEDBACK =====");
        boolean found = false;
        while (rs.next()) {
            found = true;
            System.out.println("----------------------------------");
            System.out.println("Patient      : " + rs.getString("full_name"));
            System.out.println("Rating       : " + rs.getInt("rating") + "/5");
            System.out.println("Comment      : " + rs.getString("comment"));
            System.out.println("Submitted At : " + rs.getTimestamp("submitted_at"));
        }
        if (!found) System.out.println("No feedback yet.");
        rs.close();
        ps.close();
    }
}