package user;

import adminfunction.HandleComplaints;
import adminfunction.ManageBilling;
import adminfunction.ManageRecords;
import connection.DBConnection;

import java.sql.*;
import java.util.Scanner;

public class Admin {

    private final int adminId;
    private final String fullName;
    private final String email;

    private final ManageRecords    manageRecords;
    private final HandleComplaints handleComplaints;
    private final ManageBilling    manageBilling;
    private final Scanner          sc;

    // -------------------------------------------------------
    //  Constructor — called after successful login
    // -------------------------------------------------------
    public Admin(int adminId, String fullName, String email) {
        this.adminId  = adminId;
        this.fullName = fullName;
        this.email    = email;

        this.manageRecords    = new ManageRecords(adminId);
        this.handleComplaints = new HandleComplaints(adminId);
        this.manageBilling    = new ManageBilling(adminId);
        this.sc               = new Scanner(System.in);
    }

    // -------------------------------------------------------
    //  Static login factory
    // -------------------------------------------------------
    /**
     * Authenticates an admin against the users + administrators tables.
     * Returns an Admin object on success, null on failure.
     */
    public static Admin login(String email, String password) {
        String sql = """
                SELECT u.user_id, u.full_name, u.email
                FROM users u
                JOIN administrators a ON u.user_id = a.admin_id
                WHERE u.email = ?
                  AND u.password_hash = ?
                  AND u.role = 'ADMIN'
                """;
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, email);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                int    id   = rs.getInt("user_id");
                String name = rs.getString("full_name");
                String mail = rs.getString("email");
                System.out.println("Login successful. Welcome, " + name + "!");
                return new Admin(id, name, mail);
            } else {
                System.out.println("Invalid email or password.");
                return null;
            }
        } catch (SQLException e) {
            System.out.println("Login error: " + e.getMessage());
            return null;
        }
    }

    // -------------------------------------------------------
    //  Getters
    // -------------------------------------------------------
    public int    getAdminId()  { return adminId;  }
    public String getFullName() { return fullName; }
    public String getEmail()    { return email;    }

    // -------------------------------------------------------
    //  Main Admin Menu  (Application Flow — 5 marks)
    // -------------------------------------------------------
    public void showDashboard() {
        boolean running = true;
        while (running) {
            System.out.println("\n╔══════════════════════════════════════╗");
            System.out.println("║        ADMIN DASHBOARD               ║");
            System.out.println("║  Logged in as: " + padRight(fullName, 22) + "║");
            System.out.println("╠══════════════════════════════════════╣");
            System.out.println("║  1. Manage Patient & Doctor Records  ║");
            System.out.println("║  2. Upload / View Test Reports       ║");
            System.out.println("║  3. Handle Complaints                ║");
            System.out.println("║  4. Manage Billing                   ║");
            System.out.println("║  0. Logout                           ║");
            System.out.println("╚══════════════════════════════════════╝");
            System.out.print("Choice: ");

            switch (sc.nextLine().trim()) {
                case "1" -> manageRecords.showMenu();
                case "2" -> showTestReportMenu();
                case "3" -> handleComplaints.showMenu();
                case "4" -> manageBilling.showMenu();
                case "0" -> {
                    System.out.println("Logging out... Goodbye, " + fullName + "!");
                    running = false;
                }
                default  -> System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    /** Separate sub-menu for test report operations (from ManageRecords) */
    private void showTestReportMenu() {
        boolean running = true;
        while (running) {
            System.out.println("\n===== TEST REPORTS =====");
            System.out.println(" 1. Upload Test Report");
            System.out.println(" 2. View Test Report");
            System.out.println(" 0. Back");
            System.out.print("Choice: ");
            switch (sc.nextLine().trim()) {
                case "1" -> manageRecords.uploadTestReport();
                case "2" -> manageRecords.viewTestReport();
                case "0" -> running = false;
                default  -> System.out.println("Invalid choice.");
            }
        }
    }

    // -------------------------------------------------------
    //  Utility
    // -------------------------------------------------------
    private String padRight(String s, int n) {
        return String.format("%-" + n + "s", s);
    }
}