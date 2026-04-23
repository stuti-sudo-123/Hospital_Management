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

        System.out.print("Duration (e.g. 5 days, 2 weeks): ");
        String duration = sc.nextLine();

        System.out.print("Instructions: ");
        String notes = sc.nextLine();

        // Insert into prescriptions first
        PreparedStatement ps1 = conn.prepareStatement(
            "INSERT INTO prescriptions(appointment_id, doctor_id, patient_id) VALUES (?,?,?)",
            Statement.RETURN_GENERATED_KEYS
        );
        ps1.setInt(1, appointmentId);
        ps1.setInt(2, doctorId);
        ps1.setInt(3, patientId);
        ps1.executeUpdate();

        ResultSet keys = ps1.getGeneratedKeys();
        keys.next();
        int prescriptionId = keys.getInt(1);
        keys.close();
        ps1.close();

        // Insert into prescription_medicines
        PreparedStatement ps2 = conn.prepareStatement(
            "INSERT INTO prescription_medicines(prescription_id, medicine_name, dosage, duration, instructions) VALUES (?,?,?,?,?)"
        );
        ps2.setInt(1, prescriptionId);
        ps2.setString(2, medicine);
        ps2.setString(3, dosage);
        ps2.setString(4, duration);
        ps2.setString(5, notes);
        ps2.executeUpdate();
        ps2.close();

        System.out.println("Prescription issued successfully!");
    }
}