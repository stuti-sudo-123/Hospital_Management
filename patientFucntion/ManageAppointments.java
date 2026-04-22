package patientFucntion;

import java.sql.*;
import java.util.Scanner;

public class ManageAppointments {


    public static void viewAppointments(Connection conn, int patientId) {
        try {
            String query =
                "SELECT a.appointment_id, a.status, a.booked_at, " +
                "u.full_name AS doctor_name, ds.slot_date, ds.start_time, ds.end_time " +
                "FROM appointments a " +
                "JOIN doctors d ON a.doctor_id = d.doctor_id " +
                "JOIN users u ON d.doctor_id = u.user_id " +
                "JOIN doctor_slots ds ON a.slot_id = ds.slot_id " +
                "WHERE a.patient_id = ? " +
                "ORDER BY ds.slot_date DESC, ds.start_time DESC";

            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, patientId);

            ResultSet rs = ps.executeQuery();

            System.out.println("\n===== YOUR APPOINTMENTS =====");

            boolean found = false;

            while (rs.next()) {
                found = true;

                System.out.println("----------------------------------");
                System.out.println("Appointment ID : " + rs.getInt("appointment_id"));
                System.out.println("Doctor         : " + rs.getString("doctor_name"));
                System.out.println("Date           : " + rs.getDate("slot_date"));
                System.out.println("Time           : " + rs.getTime("start_time"));
                System.out.println("Status         : " + rs.getString("status"));
                System.out.println("Booked At      : " + rs.getTimestamp("booked_at"));
            }

            if (!found) {
                System.out.println("No appointments found.");
            }

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }



    public static void cancelAppointment(Connection conn, int patientId) {
        Scanner sc = new Scanner(System.in);

        try {
            System.out.print("Enter Appointment ID to cancel: ");
            int appointmentId = sc.nextInt();

   
            String checkQuery =
                "SELECT a.status, ds.slot_date, ds.start_time " +
                "FROM appointments a " +
                "JOIN doctor_slots ds ON a.slot_id = ds.slot_id " +
                "WHERE a.appointment_id=? AND a.patient_id=?";

            PreparedStatement psCheck = conn.prepareStatement(checkQuery);
            psCheck.setInt(1, appointmentId);
            psCheck.setInt(2, patientId);

            ResultSet rs = psCheck.executeQuery();

            if (!rs.next()) {
                System.out.println("Invalid Appointment ID!");
                return;
            }

            String status = rs.getString("status");

            if (status.equals("CANCELLED") || status.equals("COMPLETED")) {
                System.out.println("Cannot cancel this appointment!");
                return;
            }

            Date date = rs.getDate("slot_date");
            Time time = rs.getTime("start_time");

            Timestamp appointmentTime = Timestamp.valueOf(date + " " + time);
            Timestamp now = new Timestamp(System.currentTimeMillis());

            long diffHours = (appointmentTime.getTime() - now.getTime()) / (1000 * 60 * 60);

            if (diffHours < 24) {
                System.out.println("Cancellation not allowed within 24 hours!");
                return;
            }

            PreparedStatement psCancel = conn.prepareStatement(
                "UPDATE appointments SET status='CANCELLED', cancelled_at=NOW(), cancellation_reason=? WHERE appointment_id=?"
            );
            psCancel.setString(1, "Cancelled by patient");
            psCancel.setInt(2, appointmentId);
            psCancel.executeUpdate();

        
            PreparedStatement psSlot = conn.prepareStatement(
                "UPDATE doctor_slots SET is_booked=0 WHERE slot_id=(SELECT slot_id FROM appointments WHERE appointment_id=?)"
            );
            psSlot.setInt(1, appointmentId);
            psSlot.executeUpdate();

            System.out.println("Appointment cancelled successfully!");

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}