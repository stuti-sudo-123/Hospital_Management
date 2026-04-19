package patientFunction;

import java.sql.*;

public class MedicalRecords {

    public static void viewPrescriptions(Connection conn, int patientId) throws Exception {
        PreparedStatement ps = conn.prepareStatement(
            "SELECT * FROM prescriptions WHERE patient_id=?"
        );
        ps.setInt(1, patientId);

        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            System.out.println("Prescription ID: " + rs.getInt("prescription_id"));
        }
    }

    public static void viewReports(Connection conn, int patientId) throws Exception {
        PreparedStatement ps = conn.prepareStatement(
            "SELECT tr.report_title, tr.result_status " +
            "FROM test_reports tr " +
            "JOIN medical_tests mt ON tr.test_id=mt.test_id " +
            "WHERE mt.patient_id=?"
        );
        ps.setInt(1, patientId);

        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            System.out.println("Report: " + rs.getString(1)
                    + " | Status: " + rs.getString(2));
        }
    }
}