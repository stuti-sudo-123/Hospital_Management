package patientFunction;

import java.sql.*;

public class MedicalRecords {

    public static void viewPrescriptions(Connection conn, int patientId) {

        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            ps = conn.prepareStatement(
                "SELECT * FROM prescriptions WHERE patient_id=?"
            );

            ps.setInt(1, patientId);

            rs = ps.executeQuery();

            boolean found = false;

            System.out.println("\n===== PRESCRIPTIONS =====");

            while (rs.next()) {
                found = true;
                System.out.println("Prescription ID: " + rs.getInt("prescription_id"));
            }

            if (!found) {
                System.out.println("No prescriptions found.");
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

    public static void viewReports(Connection conn, int patientId) {

        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            ps = conn.prepareStatement(
                "SELECT tr.report_title, tr.result_status " +
                "FROM test_reports tr " +
                "JOIN medical_tests mt ON tr.test_id=mt.test_id " +
                "WHERE mt.patient_id=?"
            );

            ps.setInt(1, patientId);

            rs = ps.executeQuery();

            boolean found = false;

            System.out.println("\n===== TEST REPORTS =====");

            while (rs.next()) {
                found = true;
                System.out.println("Report: " + rs.getString(1)
                        + " | Status: " + rs.getString(2));
            }

            if (!found) {
                System.out.println("No reports found.");
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