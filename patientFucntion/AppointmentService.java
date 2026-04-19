package patient;

import java.sql.*;
import java.util.Scanner;

public class AppointmentService {

    public static void viewAvailableSlots(Connection conn, int doctorId) throws Exception {
        PreparedStatement ps = conn.prepareStatement(
            "SELECT * FROM vw_available_slots WHERE doctor_id=?"
        );
        ps.setInt(1, doctorId);

        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            System.out.println("Slot ID: " + rs.getInt("slot_id")
                    + " | Date: " + rs.getDate("slot_date")
                    + " | Time: " + rs.getTime("start_time"));
        }
    }

    public static void bookAppointment(Connection conn, int patientId) throws Exception {
        Scanner sc = new Scanner(System.in);

        System.out.print("Enter Doctor ID: ");
        int docId = sc.nextInt();

        viewAvailableSlots(conn, docId);

        System.out.print("Enter Slot ID: ");
        int slotId = sc.nextInt();

        PreparedStatement ps = conn.prepareStatement(
            "INSERT INTO appointments(patient_id,doctor_id,slot_id) VALUES (?,?,?)"
        );
        ps.setInt(1, patientId);
        ps.setInt(2, docId);
        ps.setInt(3, slotId);
        ps.executeUpdate();

        System.out.println("Appointment Booked!");
    }
}