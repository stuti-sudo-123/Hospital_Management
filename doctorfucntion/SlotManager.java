package doctorfucntion;
import java.sql.*;
import java.util.Scanner;

public class SlotManager {

    public static void viewSlots(Connection conn, int doctorId) throws Exception {
        PreparedStatement ps = conn.prepareStatement(
            "SELECT slot_id, slot_date, start_time, end_time, is_booked FROM doctor_slots WHERE doctor_id=?"
        );
        ps.setInt(1, doctorId);
        ResultSet rs = ps.executeQuery();

        System.out.println("\n===== YOUR SLOTS =====");
        while (rs.next()) {
            System.out.println("Slot ID: " + rs.getInt("slot_id")
                + " | Date: " + rs.getDate("slot_date")
                + " | Time: " + rs.getTime("start_time")
                + " | Booked: " + (rs.getBoolean("is_booked") ? "YES" : "NO"));
        }
    }

    public static void addSlot(Connection conn, int doctorId) throws Exception {
        Scanner sc = new Scanner(System.in);

        System.out.print("Date (YYYY-MM-DD): ");
        String date = sc.nextLine();

        System.out.print("Start Time (HH:MM): ");
        String start = sc.nextLine();

        System.out.print("End Time (HH:MM): ");
        String end = sc.nextLine();

        PreparedStatement ps = conn.prepareStatement(
            "INSERT INTO doctor_slots(doctor_id, slot_date, start_time, end_time) VALUES (?,?,?,?)"
        );
        ps.setInt(1, doctorId);
        ps.setString(2, date);
        ps.setString(3, start);
        ps.setString(4, end);
        ps.executeUpdate();

        System.out.println("Slot added!");
    }

    public static void removeSlot(Connection conn, int doctorId) throws Exception {
        Scanner sc = new Scanner(System.in);

        System.out.print("Enter Slot ID to remove: ");
        int slotId = sc.nextInt();

        // Check if booked
        PreparedStatement psCheck = conn.prepareStatement(
            "SELECT is_booked FROM doctor_slots WHERE slot_id=? AND doctor_id=?"
        );
        psCheck.setInt(1, slotId);
        psCheck.setInt(2, doctorId);
        ResultSet rs = psCheck.executeQuery();

        if (!rs.next()) {
            System.out.println("Slot not found!");
            return;
        }

        if (rs.getBoolean("is_booked")) {
            throw new Exception("Cannot remove! Slot is already booked by a patient.");
        }

        PreparedStatement ps = conn.prepareStatement(
            "DELETE FROM doctor_slots WHERE slot_id=?"
        );
        ps.setInt(1, slotId);
        ps.executeUpdate();
        System.out.println("Slot removed!");
    }
}