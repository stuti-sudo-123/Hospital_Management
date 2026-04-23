package doctorfucntion;
import java.sql.*;
import java.util.Scanner;

public class ViewPatientProfile {

    public static void viewProfile(Connection conn) throws Exception {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter Patient ID: ");
        int patientId = sc.nextInt();

        // Basic info
        PreparedStatement ps = conn.prepareStatement(
            "SELECT * FROM vw_patient_full_profile WHERE user_id=?"
        );
        ps.setInt(1, patientId);
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            System.out.println("Name     : " + rs.getString("full_name"));
            System.out.println("DOB      : " + rs.getDate("date_of_birth"));
            System.out.println("Blood Grp: " + rs.getString("blood_group"));
            System.out.println("Surgeries: " + rs.getString("surgeries"));
            System.out.println("Medicines: " + rs.getString("current_meds"));
            System.out.println("Allergies: " + rs.getString("allergies"));
        } else {
            throw new Exception("Patient not found!");
        }

        // Past prescriptions
        PreparedStatement ps2 = conn.prepareStatement(
            "SELECT medicine_name, dosage, duration_days FROM prescriptions WHERE patient_id=?"
        );
        ps2.setInt(1, patientId);
        ResultSet rs2 = ps2.executeQuery();
        System.out.println("\n--- Past Prescriptions ---");
        while (rs2.next()) {
            System.out.println(rs2.getString("medicine_name")
                + " | " + rs2.getString("dosage")
                + " | " + rs2.getInt("duration_days") + " days");
        }

        // Past test reports
        PreparedStatement ps3 = conn.prepareStatement(
            "SELECT report_title, result_status FROM test_reports WHERE patient_id=?"
        );
        ps3.setInt(1, patientId);
        ResultSet rs3 = ps3.executeQuery();
        System.out.println("\n--- Test Reports ---");
        boolean found = false;
        while (rs3.next()) {
            found = true;
            System.out.println(rs3.getString("report_title")
                + " | " + rs3.getString("result_status"));
        }
        if (!found) throw new Exception("No reports found for this patient!");
    }
}