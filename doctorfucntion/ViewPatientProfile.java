package doctorfucntion;
import java.sql.*;
import java.util.Scanner;

public class ViewPatientProfile {

    public static void viewBookedPatients(Connection conn, int doctorId) throws Exception {
        PreparedStatement ps = conn.prepareStatement(
            "SELECT a.appointment_id, u.user_id, u.full_name, " +
            "ds.slot_date, ds.start_time, a.status " +
            "FROM appointments a " +
            "JOIN patients p ON a.patient_id = p.patient_id " +
            "JOIN users u ON p.patient_id = u.user_id " +
            "JOIN doctor_slots ds ON a.slot_id = ds.slot_id " +
            "WHERE a.doctor_id = ? " +
            "AND a.status IN ('BOOKED', 'CONFIRMED') " +
            "ORDER BY ds.slot_date ASC, ds.start_time ASC"
        );
        ps.setInt(1, doctorId);
        ResultSet rs = ps.executeQuery();

        System.out.println("\n===== PATIENTS BOOKED FOR YOU =====");
        boolean found = false;
        while (rs.next()) {
            found = true;
            System.out.println("----------------------------------");
            System.out.println("Appointment ID : " + rs.getInt("appointment_id"));
            System.out.println("Patient ID     : " + rs.getInt("user_id"));
            System.out.println("Patient Name   : " + rs.getString("full_name"));
            System.out.println("Date           : " + rs.getDate("slot_date"));
            System.out.println("Time           : " + rs.getTime("start_time"));
            System.out.println("Status         : " + rs.getString("status"));
        }
        if (!found) System.out.println("No patients booked.");
        rs.close();
        ps.close();
    }

    public static void viewProfile(Connection conn, int doctorId) throws Exception {
        Scanner sc = new Scanner(System.in);

        // Show booked patients first
        viewBookedPatients(conn, doctorId);

        System.out.print("\nEnter Patient ID to view profile: ");
        int patientId = sc.nextInt();
        sc.nextLine();

        // Basic info
        PreparedStatement ps = conn.prepareStatement(
            "SELECT * FROM vw_patient_full_profile WHERE user_id=?"
        );
        ps.setInt(1, patientId);
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            System.out.println("\n===== PATIENT PROFILE =====");
            System.out.println("Name     : " + rs.getString("full_name"));
            System.out.println("DOB      : " + rs.getDate("date_of_birth"));
            System.out.println("Blood Grp: " + rs.getString("blood_group"));
            System.out.println("Surgeries: " + rs.getString("surgeries"));
            System.out.println("Medicines: " + rs.getString("current_meds"));
            System.out.println("Allergies: " + rs.getString("allergies"));
        } else {
            System.out.println("Patient not found!");
            return;
        }
        rs.close();
        ps.close();

        // Past prescriptions
        PreparedStatement ps2 = conn.prepareStatement(
            "SELECT pm.medicine_name, pm.dosage, pm.duration, pm.instructions " +
            "FROM prescriptions p " +
            "JOIN prescription_medicines pm ON p.prescription_id = pm.prescription_id " +
            "WHERE p.patient_id=?"
        );
        ps2.setInt(1, patientId);
        ResultSet rs2 = ps2.executeQuery();
        System.out.println("\n--- Past Prescriptions ---");
        boolean hasPres = false;
        while (rs2.next()) {
            hasPres = true;
            System.out.println("Medicine     : " + rs2.getString("medicine_name"));
            System.out.println("Dosage       : " + rs2.getString("dosage"));
            System.out.println("Duration     : " + rs2.getString("duration"));
            System.out.println("Instructions : " + rs2.getString("instructions"));
            System.out.println("---");
        }
        if (!hasPres) System.out.println("No prescriptions found.");
        rs2.close();
        ps2.close();

        // Past test reports
        PreparedStatement ps3 = conn.prepareStatement(
            "SELECT mt.test_id, tr.report_title, tr.status, tr.result_status, tr.result_summary, " +
            "tr.finding_1_label, tr.finding_1_value, tr.finding_1_unit, tr.finding_1_flag, " +
            "tr.finding_2_label, tr.finding_2_value, tr.finding_2_unit, tr.finding_2_flag, " +
            "tr.finding_3_label, tr.finding_3_value, tr.finding_3_unit, tr.finding_3_flag, " +
            "tr.finding_4_label, tr.finding_4_value, tr.finding_4_unit, tr.finding_4_flag, " +
            "tr.doctor_notes, tr.lab_name, tr.lab_technician " +
            "FROM test_reports tr " +
            "JOIN medical_tests mt ON tr.test_id = mt.test_id " +
            "WHERE mt.patient_id=?"
        );
        ps3.setInt(1, patientId);
        ResultSet rs3 = ps3.executeQuery();

        System.out.println("\n--- Test Reports ---");
        boolean found = false;
        while (rs3.next()) {
            found = true;
            System.out.println("==========================================");
            System.out.println("Test ID       : " + rs3.getInt("test_id"));
            System.out.println("Report Title  : " + rs3.getString("report_title"));
            System.out.println("Status        : " + rs3.getString("status"));
            System.out.println("Result        : " + rs3.getString("result_status"));
            System.out.println("Summary       : " + rs3.getString("result_summary"));
            System.out.println("------------------------------------------");
            System.out.println("FINDINGS:");

            if (rs3.getString("finding_1_label") != null) {
                System.out.println("  " + rs3.getString("finding_1_label")
                    + " : " + rs3.getString("finding_1_value")
                    + " " + rs3.getString("finding_1_unit")
                    + " (Range: " + rs3.getString("finding_1_range") + ")"
                    + " [" + rs3.getString("finding_1_flag") + "]");
            }
            if (rs3.getString("finding_2_label") != null) {
                System.out.println("  " + rs3.getString("finding_2_label")
                    + " : " + rs3.getString("finding_2_value")
                    + " " + rs3.getString("finding_2_unit")
                    + " (Range: " + rs3.getString("finding_2_range") + ")"
                    + " [" + rs3.getString("finding_2_flag") + "]");
            }
            if (rs3.getString("finding_3_label") != null) {
                System.out.println("  " + rs3.getString("finding_3_label")
                    + " : " + rs3.getString("finding_3_value")
                    + " " + rs3.getString("finding_3_unit")
                    + " (Range: " + rs3.getString("finding_3_range") + ")"
                    + " [" + rs3.getString("finding_3_flag") + "]");
            }
            if (rs3.getString("finding_4_label") != null) {
                System.out.println("  " + rs3.getString("finding_4_label")
                    + " : " + rs3.getString("finding_4_value")
                    + " " + rs3.getString("finding_4_unit")
                    + " (Range: " + rs3.getString("finding_4_range") + ")"
                    + " [" + rs3.getString("finding_4_flag") + "]");
            }

            System.out.println("------------------------------------------");
            System.out.println("Doctor Notes  : " + rs3.getString("doctor_notes"));
            System.out.println("Lab           : " + rs3.getString("lab_name"));
            System.out.println("Technician    : " + rs3.getString("lab_technician"));
            System.out.println("==========================================");
        }
        if (!found) System.out.println("No reports found.");
        rs3.close();
        ps3.close();
    }
}