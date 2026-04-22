package adminfunction;

import connection.DBConnection;

import java.sql.*;
import java.util.Scanner;

public class ManageBilling {

    private final Connection con;
    private final Scanner sc;
    private final int adminId;

    public ManageBilling(int adminId) {
        this.con = DBConnection.getConnection();
        this.sc = new Scanner(System.in);
        this.adminId = adminId;
    }

    // =========================================================
    //   4. MANAGE BILLING  (3 marks)
    // =========================================================

    /** View billing summary for all patients (uses vw_billing_summary) */
    public void viewAllBills() {
        String sql = "SELECT * FROM vw_billing_summary ORDER BY bill_id";
        try (Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            System.out.println("\n===== BILLING SUMMARY =====");
            boolean found = false;
            while (rs.next()) {
                found = true;
                System.out.println("------------------------------------------");
                System.out.println("Bill ID          : " + rs.getInt("bill_id"));
                System.out.println("Patient          : " + rs.getString("patient_name"));
                System.out.println("Appointment ID   : " + rs.getInt("appointment_id"));
                System.out.println("Appointment Date : " + rs.getDate("appointment_date"));
                System.out.printf ("Consultation Fee : ₹%.2f%n", rs.getDouble("consultation_fee"));
                System.out.printf ("Medicine Cost    : ₹%.2f%n", rs.getDouble("medicine_cost"));
                System.out.printf ("Test Cost        : ₹%.2f%n", rs.getDouble("test_cost"));
                System.out.printf ("TOTAL AMOUNT     : ₹%.2f%n", rs.getDouble("total_amount"));
                System.out.println("Payment Status   : " + rs.getString("payment_status"));
            }
            if (!found) System.out.println("No billing records found.");
        } catch (SQLException e) {
            System.out.println("Error fetching bills: " + e.getMessage());
        }
    }

    /**
     * Generate a new billing record for a patient linked to an appointment.
     * Admin enters consultation fee, medicine cost, and test cost.
     */
    public void generateBill() {
        System.out.println("\n===== GENERATE BILL =====");

        System.out.print("Enter Patient ID: ");
        int patientId = Integer.parseInt(sc.nextLine().trim());

        System.out.print("Enter Appointment ID (0 if not linked): ");
        int appointmentId = Integer.parseInt(sc.nextLine().trim());

        System.out.print("Consultation Fee (₹): ");
        double consultFee = Double.parseDouble(sc.nextLine().trim());

        System.out.print("Medicine Cost (₹): ");
        double medCost = Double.parseDouble(sc.nextLine().trim());

        System.out.print("Test Cost (₹): ");
        double testCost = Double.parseDouble(sc.nextLine().trim());

        System.out.print("Payment Status (PENDING / PAID / WAIVED): ");
        String payStatus = sc.nextLine().trim().toUpperCase();

        String sql = """
                INSERT INTO billing
                    (patient_id, appointment_id, consultation_fee, medicine_cost, test_cost,
                     payment_status, generated_by)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;

        try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, patientId);
            if (appointmentId == 0) ps.setNull(2, Types.INTEGER);
            else                    ps.setInt(2, appointmentId);
            ps.setDouble(3, consultFee);
            ps.setDouble(4, medCost);
            ps.setDouble(5, testCost);
            ps.setString(6, payStatus);
            ps.setInt(7, adminId);

            int rows = ps.executeUpdate();
            if (rows > 0) {
                ResultSet keys = ps.getGeneratedKeys();
                if (keys.next())
                    System.out.println("Bill generated successfully. Bill ID: " + keys.getInt(1));
                double total = consultFee + medCost + testCost;
                System.out.printf("Total Amount: ₹%.2f%n", total);
            } else {
                System.out.println("Failed to generate bill.");
            }
        } catch (SQLException e) {
            System.out.println("Error generating bill: " + e.getMessage());
        }
    }

    /**
     * Update an existing billing record:
     * - Update fees (consultation / medicine / test)
     * - Update payment status
     */
    public void updateBill() {
        System.out.println("\n===== UPDATE BILL =====");
        viewAllBills();

        System.out.print("Enter Bill ID to update: ");
        int billId = Integer.parseInt(sc.nextLine().trim());

        // Verify bill exists
        if (!billExists(billId)) {
            System.out.println("Bill ID " + billId + " not found.");
            return;
        }

        System.out.println("What would you like to update?");
        System.out.println(" 1. Update Fee Amounts");
        System.out.println(" 2. Update Payment Status");
        System.out.println(" 3. Update Both");
        System.out.print("Choice: ");
        String choice = sc.nextLine().trim();

        double consultFee = -1, medCost = -1, testCost = -1;
        String payStatus = null;

        if (choice.equals("1") || choice.equals("3")) {
            System.out.print("New Consultation Fee (₹, leave blank to keep): ");
            String cf = sc.nextLine().trim();
            if (!cf.isEmpty()) consultFee = Double.parseDouble(cf);

            System.out.print("New Medicine Cost (₹, leave blank to keep): ");
            String mc = sc.nextLine().trim();
            if (!mc.isEmpty()) medCost = Double.parseDouble(mc);

            System.out.print("New Test Cost (₹, leave blank to keep): ");
            String tc = sc.nextLine().trim();
            if (!tc.isEmpty()) testCost = Double.parseDouble(tc);
        }

        if (choice.equals("2") || choice.equals("3")) {
            System.out.print("New Payment Status (PENDING / PAID / WAIVED): ");
            payStatus = sc.nextLine().trim().toUpperCase();
            if (payStatus.isEmpty()) payStatus = null;
        }

        // Build dynamic UPDATE
        StringBuilder sql = new StringBuilder("UPDATE billing SET ");
        boolean first = true;
        if (consultFee >= 0) { sql.append("consultation_fee = ?");  first = false; }
        if (medCost   >= 0)  { if (!first) sql.append(", "); sql.append("medicine_cost = ?");   first = false; }
        if (testCost  >= 0)  { if (!first) sql.append(", "); sql.append("test_cost = ?");        first = false; }
        if (payStatus != null){ if (!first) sql.append(", "); sql.append("payment_status = ?"); }
        sql.append(" WHERE bill_id = ?");

        try (PreparedStatement ps = con.prepareStatement(sql.toString())) {
            int idx = 1;
            if (consultFee >= 0) ps.setDouble(idx++, consultFee);
            if (medCost    >= 0) ps.setDouble(idx++, medCost);
            if (testCost   >= 0) ps.setDouble(idx++, testCost);
            if (payStatus != null) ps.setString(idx++, payStatus);
            ps.setInt(idx, billId);

            int rows = ps.executeUpdate();
            System.out.println(rows > 0 ? "Bill updated successfully." : "No changes made.");
        } catch (SQLException e) {
            System.out.println("Error updating bill: " + e.getMessage());
        }
    }

    /** View billing record for a specific patient */
    public void viewPatientBill() {
        System.out.print("\nEnter Patient ID: ");
        int patientId = Integer.parseInt(sc.nextLine().trim());

        String sql = """
                SELECT b.bill_id, b.appointment_id, ds.slot_date AS appointment_date,
                       b.consultation_fee, b.medicine_cost, b.test_cost,
                       b.total_amount, b.payment_status
                FROM billing b
                LEFT JOIN appointments a  ON b.appointment_id = a.appointment_id
                LEFT JOIN doctor_slots ds ON a.slot_id        = ds.slot_id
                WHERE b.patient_id = ?
                ORDER BY b.generated_at DESC
                """;
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, patientId);
            ResultSet rs = ps.executeQuery();

            System.out.println("\n===== BILLS FOR PATIENT ID " + patientId + " =====");
            boolean found = false;
            while (rs.next()) {
                found = true;
                System.out.println("------------------------------------------");
                System.out.println("Bill ID          : " + rs.getInt("bill_id"));
                System.out.println("Appointment ID   : " + rs.getInt("appointment_id"));
                System.out.println("Appointment Date : " + rs.getDate("appointment_date"));
                System.out.printf ("Consultation Fee : ₹%.2f%n", rs.getDouble("consultation_fee"));
                System.out.printf ("Medicine Cost    : ₹%.2f%n", rs.getDouble("medicine_cost"));
                System.out.printf ("Test Cost        : ₹%.2f%n", rs.getDouble("test_cost"));
                System.out.printf ("TOTAL AMOUNT     : ₹%.2f%n", rs.getDouble("total_amount"));
                System.out.println("Payment Status   : " + rs.getString("payment_status"));
            }
            if (!found) System.out.println("No bills found for Patient ID " + patientId + ".");
        } catch (SQLException e) {
            System.out.println("Error fetching patient bill: " + e.getMessage());
        }
    }

    // --- helper ---
    private boolean billExists(int billId) {
        try (PreparedStatement ps = con.prepareStatement(
                "SELECT 1 FROM billing WHERE bill_id = ?")) {
            ps.setInt(1, billId);
            return ps.executeQuery().next();
        } catch (SQLException e) { return false; }
    }

    // =========================================================
    //   MENU
    // =========================================================
    public void showMenu() {
        boolean running = true;
        while (running) {
            System.out.println("\n======= ADMIN — MANAGE BILLING =======");
            System.out.println(" 1. View All Bills");
            System.out.println(" 2. View Bills for a Patient");
            System.out.println(" 3. Generate New Bill");
            System.out.println(" 4. Update Existing Bill");
            System.out.println(" 0. Back");
            System.out.print("Choice: ");
            switch (sc.nextLine().trim()) {
                case "1" -> viewAllBills();
                case "2" -> viewPatientBill();
                case "3" -> generateBill();
                case "4" -> updateBill();
                case "0" -> running = false;
                default  -> System.out.println("Invalid choice.");
            }
        }
    }
}