package patientfucntion;

import java.sql.*;
import java.util.Scanner;

public class AppointmentService {

    public static void viewAvailableSlots(Connection conn, int doctorId) {

        try {
            PreparedStatement ps = conn.prepareStatement(
                "SELECT * FROM vw_available_slots WHERE doctor_id=?"
            );

            ps.setInt(1, doctorId);

            ResultSet rs = ps.executeQuery();

            boolean found = false;

            while (rs.next()) {
                found = true;
                System.out.println("Slot ID: " + rs.getInt("slot_id")
                        + " | Date: " + rs.getDate("slot_date")
                        + " | Time: " + rs.getTime("start_time"));
            }

            if (!found) {
                System.out.println("No available slots for this doctor.");
            }

            rs.close();
            ps.close();

        } catch (SQLException e) {
            System.out.println("Database Error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public static void bookAppointment(Connection conn, int patientId) {
        Scanner sc = new Scanner(System.in);

        try {
            System.out.print("Enter Doctor ID: ");
            int docId = sc.nextInt();

            // Show available slots
            viewAvailableSlots(conn, docId);

            System.out.print("Enter Slot ID: ");
            int slotId = sc.nextInt();

            PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO appointments(patient_id,doctor_id,slot_id) VALUES (?,?,?)"
            );

            ps.setInt(1, patientId);
            ps.setInt(2, docId);
            ps.setInt(3, slotId);

            int rows = ps.executeUpdate();

            if (rows > 0) {
                System.out.println("Appointment Booked Successfully!");
            } else {
                System.out.println("Failed to book appointment.");
            }

            ps.close();

        } catch (SQLException e) {
            System.out.println("Database Error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}