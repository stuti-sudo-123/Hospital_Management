package adminfunction;

import connection.DBConnection;
import java.sql.*;
import java.util.Scanner;

public class HandleComplaints {

    private final Connection con;
    private final Scanner sc;
    private final int adminId;

    public HandleComplaints(int adminId) {
        this.con = DBConnection.getConnection();
        this.sc = new Scanner(System.in);
        this.adminId = adminId;
    }

    // =========================================================
    //   3. HANDLE COMPLAINTS  (3 marks)
    // =========================================================

    /** View ALL complaints (both PENDING and RESOLVED) */
    public void viewAllComplaints() {
        String sql = """
                SELECT c.complaint_id, u.full_name AS patient_name,
                       c.description, c.status, c.submitted_at, c.resolved_at
                FROM complaints c
                JOIN patients p  ON c.patient_id  = p.patient_id
                JOIN users    u  ON p.patient_id  = u.user_id
                ORDER BY c.submitted_at DESC
                """;
        printComplaints(sql, null, "ALL COMPLAINTS");
    }

    /** Filter complaints by status: PENDING or RESOLVED */
    public void filterComplaintsByStatus() {
        System.out.print("\nFilter by status (PENDING / RESOLVED): ");
        String status = sc.nextLine().trim().toUpperCase();

        if (!status.equals("PENDING") && !status.equals("RESOLVED")) {
            System.out.println("Invalid status. Please enter PENDING or RESOLVED.");
            return;
        }

        // Use the built-in view for PENDING, raw query for RESOLVED
        if (status.equals("PENDING")) {
            String sql = "SELECT complaint_id, patient_name, description, status, submitted_at, NULL AS resolved_at " +
                         "FROM vw_pending_complaints ORDER BY submitted_at DESC";
            printComplaints(sql, null, "PENDING COMPLAINTS");
        } else {
            String sql = """
                    SELECT c.complaint_id, u.full_name AS patient_name,
                           c.description, c.status, c.submitted_at, c.resolved_at
                    FROM complaints c
                    JOIN patients p ON c.patient_id = p.patient_id
                    JOIN users    u ON p.patient_id = u.user_id
                    WHERE c.status = 'RESOLVED'
                    ORDER BY c.resolved_at DESC
                    """;
            printComplaints(sql, null, "RESOLVED COMPLAINTS");
        }
    }

    /** Mark a complaint as RESOLVED */
    public void resolveComplaint() {
        System.out.print("\nEnter Complaint ID to mark as RESOLVED: ");
        int complaintId = Integer.parseInt(sc.nextLine().trim());

        // Check it exists and is still PENDING
        String checkSql = "SELECT status FROM complaints WHERE complaint_id = ?";
        try (PreparedStatement ps = con.prepareStatement(checkSql)) {
            ps.setInt(1, complaintId);
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) {
                System.out.println("Complaint ID " + complaintId + " not found.");
                return;
            }
            if (rs.getString("status").equals("RESOLVED")) {
                System.out.println("Complaint is already RESOLVED.");
                return;
            }
        } catch (SQLException e) {
            System.out.println("Error checking complaint: " + e.getMessage());
            return;
        }

        String sql = """
                UPDATE complaints
                SET status = 'RESOLVED', resolved_by = ?, resolved_at = NOW()
                WHERE complaint_id = ?
                """;
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, adminId);
            ps.setInt(2, complaintId);
            int rows = ps.executeUpdate();
            System.out.println(rows > 0
                    ? "Complaint ID " + complaintId + " marked as RESOLVED."
                    : "Update failed.");
        } catch (SQLException e) {
            System.out.println("Error resolving complaint: " + e.getMessage());
        }
    }

    // --- helper to print complaint result sets ---
    private void printComplaints(String sql, String statusFilter, String title) {
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            if (statusFilter != null) ps.setString(1, statusFilter);
            ResultSet rs = ps.executeQuery();

            System.out.println("\n===== " + title + " =====");
            boolean found = false;
            while (rs.next()) {
                found = true;
                System.out.println("------------------------------------------");
                System.out.println("Complaint ID  : " + rs.getInt("complaint_id"));
                System.out.println("Patient       : " + rs.getString("patient_name"));
                System.out.println("Status        : " + rs.getString("status"));
                System.out.println("Submitted At  : " + rs.getTimestamp("submitted_at"));
                System.out.println("Resolved At   : " + rs.getTimestamp("resolved_at"));
                System.out.println("Description   : " + rs.getString("description"));
            }
            if (!found) System.out.println("No complaints found.");
        } catch (SQLException e) {
            System.out.println("Error fetching complaints: " + e.getMessage());
        }
    }

    // =========================================================
    //   MENU
    // =========================================================
    public void showMenu() {
        boolean running = true;
        while (running) {
            System.out.println("\n======= ADMIN — HANDLE COMPLAINTS =======");
            System.out.println(" 1. View All Complaints");
            System.out.println(" 2. Filter Complaints by Status");
            System.out.println(" 3. Resolve a Complaint");
            System.out.println(" 0. Back");
            System.out.print("Choice: ");
            switch (sc.nextLine().trim()) {
                case "1" -> viewAllComplaints();
                case "2" -> filterComplaintsByStatus();
                case "3" -> resolveComplaint();
                case "0" -> running = false;
                default  -> System.out.println("Invalid choice.");
            }
        }
    }
}