package adminfunction;

import connection.DBConnection;

import java.sql.*;
import java.util.Scanner;

public class ManageRecords {

    private final Connection con;
    private final Scanner sc;
    private final int adminId;

    public ManageRecords(int adminId) {
        this.con = DBConnection.getConnection();
        this.sc = new Scanner(System.in);
        this.adminId = adminId;
    }

    public void viewAllPatients() {
        String sql = "SELECT * FROM vw_patient_full_profile";
        try (Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            System.out.println("\n===== ALL PATIENT PROFILES =====");
            boolean found = false;
            while (rs.next()) {
                found = true;
                System.out.println("----------------------------------");
                System.out.println("ID          : " + rs.getInt("user_id"));
                System.out.println("Name        : " + rs.getString("full_name"));
                System.out.println("Email       : " + rs.getString("email"));
                System.out.println("DOB         : " + rs.getDate("date_of_birth"));
                System.out.println("Contact     : " + rs.getString("contact_number"));
                System.out.println("Blood Group : " + rs.getString("blood_group"));
                System.out.println("Surgeries   : " + rs.getString("surgeries"));
                System.out.println("Current Meds: " + rs.getString("current_meds"));
                System.out.println("Allergies   : " + rs.getString("allergies"));
            }
            if (!found) System.out.println("No patients found.");
        } catch (SQLException e) {
            System.out.println("Error fetching patients: " + e.getMessage());
        }
    }

    public void viewAllDoctors() {
        String sql = "SELECT * FROM vw_doctor_details";
        try (Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            System.out.println("\n===== ALL DOCTOR PROFILES =====");
            boolean found = false;
            while (rs.next()) {
                found = true;
                System.out.println("----------------------------------");
                System.out.println("ID             : " + rs.getInt("doctor_id"));
                System.out.println("Name           : " + rs.getString("doctor_name"));
                System.out.println("Email          : " + rs.getString("email"));
                System.out.println("Qualification  : " + rs.getString("qualification"));
                System.out.println("Experience(yrs): " + rs.getInt("experience_yrs"));
                System.out.println("Contact        : " + rs.getString("contact_number"));
                System.out.println("Specialisation : " + rs.getString("specialisation"));
            }
            if (!found) System.out.println("No doctors found.");
        } catch (SQLException e) {
            System.out.println("Error fetching doctors: " + e.getMessage());
        }
    }

    public void updatePatientProfile() {
        System.out.print("\nEnter Patient ID to update: ");
        int patientId = Integer.parseInt(sc.nextLine().trim());

        System.out.print("New Contact Number (leave blank to skip): ");
        String contact = sc.nextLine().trim();
        System.out.print("New Blood Group (leave blank to skip): ");
        String bloodGroup = sc.nextLine().trim();

        if (contact.isEmpty() && bloodGroup.isEmpty()) {
            System.out.println("Nothing to update.");
            return;
        }

        StringBuilder sql = new StringBuilder("UPDATE patients SET ");
        boolean first = true;
        if (!contact.isEmpty()) {
            sql.append("contact_number = ?");
            first = false;
        }
        if (!bloodGroup.isEmpty()) {
            if (!first) sql.append(", ");
            sql.append("blood_group = ?");
        }
        sql.append(" WHERE patient_id = ?");

        try (PreparedStatement ps = con.prepareStatement(sql.toString())) {
            int idx = 1;
            if (!contact.isEmpty()) ps.setString(idx++, contact);
            if (!bloodGroup.isEmpty()) ps.setString(idx++, bloodGroup);
            ps.setInt(idx, patientId);

            int rows = ps.executeUpdate();
            System.out.println(rows > 0 ? "Patient profile updated successfully." : "Patient not found.");
        } catch (SQLException e) {
            System.out.println("Error updating patient: " + e.getMessage());
        }
    }

    public void updateDoctorProfile() {
        System.out.print("\nEnter Doctor ID to update: ");
        int doctorId = Integer.parseInt(sc.nextLine().trim());

        System.out.print("New Qualification (leave blank to skip): ");
        String qual = sc.nextLine().trim();
        System.out.print("New Experience Years (leave blank to skip): ");
        String expStr = sc.nextLine().trim();
        System.out.print("New Contact Number (leave blank to skip): ");
        String contact = sc.nextLine().trim();

        if (qual.isEmpty() && expStr.isEmpty() && contact.isEmpty()) {
            System.out.println("Nothing to update.");
            return;
        }

        StringBuilder sql = new StringBuilder("UPDATE doctors SET ");
        boolean first = true;
        if (!qual.isEmpty())    { sql.append("qualification = ?");     first = false; }
        if (!expStr.isEmpty())  { if (!first) sql.append(", "); sql.append("experience_yrs = ?"); first = false; }
        if (!contact.isEmpty()) { if (!first) sql.append(", "); sql.append("contact_number = ?"); }
        sql.append(" WHERE doctor_id = ?");

        try (PreparedStatement ps = con.prepareStatement(sql.toString())) {
            int idx = 1;
            if (!qual.isEmpty())    ps.setString(idx++, qual);
            if (!expStr.isEmpty())  ps.setInt(idx++, Integer.parseInt(expStr));
            if (!contact.isEmpty()) ps.setString(idx++, contact);
            ps.setInt(idx, doctorId);

            int rows = ps.executeUpdate();
            System.out.println(rows > 0 ? "Doctor profile updated successfully." : "Doctor not found.");
        } catch (SQLException e) {
            System.out.println("Error updating doctor: " + e.getMessage());
        }
    }

    public void removePatient() {
        System.out.print("\nEnter Patient ID to remove: ");
        int patientId = Integer.parseInt(sc.nextLine().trim());

        System.out.print("Confirm removal of patient ID " + patientId + "? (yes/no): ");
        if (!sc.nextLine().trim().equalsIgnoreCase("yes")) {
            System.out.println("Removal cancelled.");
            return;
        }

        String sql = "DELETE FROM users WHERE user_id = ? AND role = 'PATIENT'";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, patientId);
            int rows = ps.executeUpdate();
            System.out.println(rows > 0 ? "Patient removed successfully." : "Patient not found.");
        } catch (SQLException e) {
            System.out.println("Error removing patient: " + e.getMessage());
        }
    }

    public void removeDoctor() {
        System.out.print("\nEnter Doctor ID to remove: ");
        int doctorId = Integer.parseInt(sc.nextLine().trim());

        System.out.print("Confirm removal of doctor ID " + doctorId + "? (yes/no): ");
        if (!sc.nextLine().trim().equalsIgnoreCase("yes")) {
            System.out.println("Removal cancelled.");
            return;
        }

        String sql = "DELETE FROM users WHERE user_id = ? AND role = 'DOCTOR'";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, doctorId);
            int rows = ps.executeUpdate();
            System.out.println(rows > 0 ? "Doctor removed successfully." : "Doctor not found.");
        } catch (SQLException e) {
            System.out.println("Error removing doctor: " + e.getMessage());
        }
    }

    public void assignDoctorToDepartment() {
        System.out.println("\n===== ASSIGN DOCTOR TO DEPARTMENT =====");
        viewAllDepartments();

        System.out.print("Enter Doctor ID: ");
        int doctorId = Integer.parseInt(sc.nextLine().trim());
        System.out.print("Enter Department ID: ");
        int deptId = Integer.parseInt(sc.nextLine().trim());

        String sql = "UPDATE doctors SET department_id = ? WHERE doctor_id = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, deptId);
            ps.setInt(2, doctorId);
            int rows = ps.executeUpdate();
            System.out.println(rows > 0
                    ? "Doctor ID " + doctorId + " assigned to Department ID " + deptId + " successfully."
                    : "Doctor not found.");
        } catch (SQLException e) {
            System.out.println("Error assigning department: " + e.getMessage());
        }
    }

    private void viewAllDepartments() {
        String sql = "SELECT department_id, name FROM departments ORDER BY department_id";
        try (Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            System.out.println("\n--- Departments ---");
            while (rs.next()) {
                System.out.printf("  [%2d] %s%n", rs.getInt("department_id"), rs.getString("name"));
            }
            System.out.println("-------------------");
        } catch (SQLException e) {
            System.out.println("Error fetching departments: " + e.getMessage());
        }
    }

    // =========================================================
    //   UPLOAD TEST REPORT
    // =========================================================

    private void showAvailableTestsWithoutReport() {
        String sql =
            "SELECT mt.test_id, u.full_name AS patient_name, mt.test_name, mt.ordered_at " +
            "FROM medical_tests mt " +
            "JOIN users u ON u.user_id = mt.patient_id " +
            "WHERE mt.test_id NOT IN (SELECT test_id FROM test_reports) " +
            "ORDER BY mt.test_id";
        try (Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            System.out.println("\n--- Pending Tests (No Report Yet) ---");
            boolean found = false;
            while (rs.next()) {
                found = true;
                System.out.println("Test ID  : " + rs.getInt("test_id")
                    + " | Patient : " + rs.getString("patient_name")
                    + " | Test    : " + rs.getString("test_name")
                    + " | Ordered : " + rs.getTimestamp("ordered_at"));
            }
            if (!found) System.out.println("No pending tests found.");
            System.out.println("--------------------------------------");
        } catch (SQLException e) {
            System.out.println("Error fetching tests: " + e.getMessage());
        }
    }

    private void showAllTestsWithReport() {
        String sql =
            "SELECT mt.test_id, u.full_name AS patient_name, mt.test_name, " +
            "tr.report_title, tr.result_status " +
            "FROM medical_tests mt " +
            "JOIN users u ON u.user_id = mt.patient_id " +
            "JOIN test_reports tr ON tr.test_id = mt.test_id " +
            "ORDER BY mt.test_id";
        try (Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            System.out.println("\n--- Tests With Reports ---");
            boolean found = false;
            while (rs.next()) {
                found = true;
                System.out.println("Test ID  : " + rs.getInt("test_id")
                    + " | Patient : " + rs.getString("patient_name")
                    + " | Test    : " + rs.getString("test_name")
                    + " | Report  : " + rs.getString("report_title")
                    + " | Result  : " + rs.getString("result_status"));
            }
            if (!found) System.out.println("No reports uploaded yet.");
            System.out.println("--------------------------");
        } catch (SQLException e) {
            System.out.println("Error fetching reports: " + e.getMessage());
        }
    }

    public void uploadTestReport() {
        System.out.println("\n===== UPLOAD TEST REPORT =====");

        // Show pending tests first
        showAvailableTestsWithoutReport();

        System.out.print("Enter Test ID to upload report for: ");
        int testId = Integer.parseInt(sc.nextLine().trim());

        if (!testExists(testId)) {
            System.out.println("Test ID not found.");
            return;
        }
        if (reportAlreadyExists(testId)) {
            System.out.println("A report already exists for Test ID " + testId + ". Use update instead.");
            return;
        }

        System.out.print("Report Number (e.g. RPT-2025-003): ");
        String reportNumber = sc.nextLine().trim();
        System.out.print("Report Title: ");
        String reportTitle = sc.nextLine().trim();
        System.out.print("Result Summary: ");
        String summary = sc.nextLine().trim();
        System.out.print("Overall Result Status (NORMAL/ABNORMAL/CRITICAL): ");
        String resultStatus = sc.nextLine().trim().toUpperCase();

        String[] fLabel = new String[4], fValue = new String[4], fUnit = new String[4],
                 fRange = new String[4], fFlag = new String[4];
        for (int i = 0; i < 4; i++) {
            System.out.print("Finding " + (i + 1) + " Label (leave blank to stop): ");
            fLabel[i] = sc.nextLine().trim();
            if (fLabel[i].isEmpty()) { fLabel[i] = null; break; }
            System.out.print("Finding " + (i + 1) + " Value: ");
            fValue[i] = sc.nextLine().trim();
            System.out.print("Finding " + (i + 1) + " Unit: ");
            fUnit[i] = sc.nextLine().trim();
            System.out.print("Finding " + (i + 1) + " Normal Range: ");
            fRange[i] = sc.nextLine().trim();
            System.out.print("Finding " + (i + 1) + " Flag (NORMAL/LOW/HIGH/CRITICAL): ");
            fFlag[i] = sc.nextLine().trim().toUpperCase();
        }

        System.out.print("Doctor Notes: ");
        String doctorNotes = sc.nextLine().trim();
        System.out.print("Lab Name (default: Hospital Central Lab): ");
        String labName = sc.nextLine().trim();
        if (labName.isEmpty()) labName = "Hospital Central Lab";
        System.out.print("Lab Technician Name: ");
        String labTech = sc.nextLine().trim();
        System.out.print("Sample Collected At (YYYY-MM-DD HH:MM:SS): ");
        String sampleCollected = sc.nextLine().trim();

        String sql = "INSERT INTO test_reports " +
            "(test_id, uploaded_by, report_number, report_title, status, result_summary, result_status, " +
            " finding_1_label, finding_1_value, finding_1_unit, finding_1_range, finding_1_flag, " +
            " finding_2_label, finding_2_value, finding_2_unit, finding_2_range, finding_2_flag, " +
            " finding_3_label, finding_3_value, finding_3_unit, finding_3_range, finding_3_flag, " +
            " finding_4_label, finding_4_value, finding_4_unit, finding_4_range, finding_4_flag, " +
            " doctor_notes, lab_name, lab_technician, sample_collected_at) " +
            "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, testId);
            ps.setInt(2, adminId);
            ps.setString(3, reportNumber);
            ps.setString(4, reportTitle);
            ps.setString(5, "UPLOADED");
            ps.setString(6, summary);
            ps.setString(7, resultStatus);
            for (int i = 0; i < 4; i++) {
                int base = 8 + (i * 5);
                ps.setString(base,     fLabel[i]);
                ps.setString(base + 1, fValue[i]);
                ps.setString(base + 2, fUnit[i]);
                ps.setString(base + 3, fRange[i]);
                ps.setString(base + 4, fFlag[i]);
            }
            ps.setString(28, doctorNotes.isEmpty() ? null : doctorNotes);
            ps.setString(29, labName);
            ps.setString(30, labTech.isEmpty() ? null : labTech);
            ps.setString(31, sampleCollected.isEmpty() ? null : sampleCollected);

            int rows = ps.executeUpdate();
            System.out.println(rows > 0 ? "Test report uploaded successfully." : "Upload failed.");
        } catch (SQLException e) {
            System.out.println("Error uploading report: " + e.getMessage());
        }
    }

    public void viewTestReport() {
        System.out.println("\n===== VIEW TEST REPORT =====");

        // Show all tests with reports first
        showAllTestsWithReport();

        System.out.print("Enter Test ID to view report: ");
        int testId = Integer.parseInt(sc.nextLine().trim());

        String sql = "SELECT tr.*, mt.test_name FROM test_reports tr " +
                     "JOIN medical_tests mt ON tr.test_id = mt.test_id " +
                     "WHERE tr.test_id = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, testId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    System.out.println("\n===== TEST REPORT =====");
                    System.out.println("Report No     : " + rs.getString("report_number"));
                    System.out.println("Test Name     : " + rs.getString("test_name"));
                    System.out.println("Title         : " + rs.getString("report_title"));
                    System.out.println("Status        : " + rs.getString("status"));
                    System.out.println("Result Status : " + rs.getString("result_status"));
                    System.out.println("Summary       : " + rs.getString("result_summary"));
                    System.out.println("--- Findings ---");
                    for (int i = 1; i <= 4; i++) {
                        String label = rs.getString("finding_" + i + "_label");
                        if (label != null && !label.isEmpty()) {
                            System.out.printf("  %s: %s %s  [Range: %s]  Flag: %s%n",
                                    label,
                                    rs.getString("finding_" + i + "_value"),
                                    rs.getString("finding_" + i + "_unit"),
                                    rs.getString("finding_" + i + "_range"),
                                    rs.getString("finding_" + i + "_flag"));
                        }
                    }
                    System.out.println("Doctor Notes  : " + rs.getString("doctor_notes"));
                    System.out.println("Lab           : " + rs.getString("lab_name"));
                    System.out.println("Lab Tech      : " + rs.getString("lab_technician"));
                    System.out.println("Sample Taken  : " + rs.getTimestamp("sample_collected_at"));
                    System.out.println("Generated At  : " + rs.getTimestamp("report_generated_at"));
                } else {
                    System.out.println("No report found for Test ID " + testId + ".");
                }
            }
        } catch (SQLException e) {
            System.out.println("Error fetching report: " + e.getMessage());
        }
    }

    private boolean testExists(int testId) {
        try (PreparedStatement ps = con.prepareStatement(
                "SELECT 1 FROM medical_tests WHERE test_id = ?")) {
            ps.setInt(1, testId);
            return ps.executeQuery().next();
        } catch (SQLException e) { return false; }
    }

    private boolean reportAlreadyExists(int testId) {
        try (PreparedStatement ps = con.prepareStatement(
                "SELECT 1 FROM test_reports WHERE test_id = ?")) {
            ps.setInt(1, testId);
            return ps.executeQuery().next();
        } catch (SQLException e) { return false; }
    }

    public void showMenu() {
        boolean running = true;
        while (running) {
            System.out.println("\n========= ADMIN — MANAGE RECORDS =========");
            System.out.println(" 1. View All Patients");
            System.out.println(" 2. Update Patient Profile");
            System.out.println(" 3. Remove Patient");
            System.out.println(" 4. View All Doctors");
            System.out.println(" 5. Update Doctor Profile");
            System.out.println(" 6. Remove Doctor");
            System.out.println(" 7. Assign Doctor to Department");
            System.out.println(" 8. Upload Test Report");
            System.out.println(" 9. View Test Report");
            System.out.println(" 0. Back");
            System.out.print("Choice: ");

            String choice = sc.nextLine().trim();
            switch (choice) {
                case "1" -> viewAllPatients();
                case "2" -> updatePatientProfile();
                case "3" -> removePatient();
                case "4" -> viewAllDoctors();
                case "5" -> updateDoctorProfile();
                case "6" -> removeDoctor();
                case "7" -> assignDoctorToDepartment();
                case "8" -> uploadTestReport();
                case "9" -> viewTestReport();
                case "0" -> running = false;
                default  -> System.out.println("Invalid choice.");
            }
        }
    }
}