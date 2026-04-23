package doctorfucntion;
import java.sql.*;
import java.util.Scanner;

public class TestService {

    public static void orderTest(Connection conn, int doctorId) throws Exception {
        Scanner sc = new Scanner(System.in);

        System.out.print("Enter Patient ID: ");
        int patientId = sc.nextInt();
        sc.nextLine();

        System.out.print("Enter Appointment ID: ");
        int appointmentId = sc.nextInt();
        sc.nextLine();

        System.out.print("Test Name: ");
        String testName = sc.nextLine();

        System.out.print("Notes: ");
        String notes = sc.nextLine();

        // Insert into medical_tests
        PreparedStatement ps = conn.prepareStatement(
            "INSERT INTO medical_tests(appointment_id, doctor_id, patient_id, test_name, test_description) VALUES (?,?,?,?,?)"
        );
        ps.setInt(1, appointmentId);
        ps.setInt(2, doctorId);
        ps.setInt(3, patientId);
        ps.setString(4, testName);
        ps.setString(5, notes);
        ps.executeUpdate();
        ps.close();

        System.out.println("Test ordered! Admin will upload the result.");
    }
}