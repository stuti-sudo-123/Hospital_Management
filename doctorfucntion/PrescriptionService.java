package doctorfucntion;
import java.sql.*;
import java.util.Scanner;

public class PrescriptionService {

    public static void issuePrescription(Connection conn, int doctorId) throws Exception {
        Scanner sc = new Scanner(System.in);

        System.out.print("Enter Patient ID: ");
        int patientId = sc.nextInt();
        sc.nextLine();

        System.out.print("Enter Appointment ID: ");
        int appointmentId = sc.nextInt();
        sc.nextLine();

        System.out.print("Medicine Name: ");
        String medicine = sc.nextLine();

        System.out.print("Dosage: ");
        String dosage = sc.nextLine();

        System.out.print("Duration (days): ");
        int days = sc.nextInt();
        sc.nextLine();

        System.out.print("Notes: ");
        String notes = sc.nextLine();

        PreparedStatement ps = conn.prepareStatement(
            "INSERT INTO prescriptions(appointment_id, patient_id, medicine_name, dosage, duration_days, notes) VALUES (?,?,?,?,?,?)"
        );
        ps.setInt(1, appointmentId);
        ps.setInt(2, patientId);
        ps.setString(3, medicine);
        ps.setString(4, dosage);
        ps.setInt(5, days);
        ps.setString(6, notes);
        ps.executeUpdate();

        System.out.println("Prescription issued successfully!");
    }
}