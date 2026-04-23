package doctorfucntion;
import java.sql.*;
import java.util.Scanner;

public class TestService {

    public static void orderTest(Connection conn, int doctorId) throws Exception {
        Scanner sc = new Scanner(System.in);

        System.out.print("Enter Patient ID: ");
        int patientId = sc.nextInt();
        sc.nextLine();

        System.out.print("Test Name: ");
        String testName = sc.nextLine();

        System.out.print("Notes: ");
        String notes = sc.nextLine();

        PreparedStatement ps = conn.prepareStatement(
            "INSERT INTO test_reports(patient_id, doctor_id, report_title, result_status, notes) VALUES (?,?,?,'PENDING',?)"
        );
        ps.setInt(1, patientId);
        ps.setInt(2, doctorId);
        ps.setString(3, testName);
        ps.setString(4, notes);
        ps.executeUpdate();

        System.out.println("Test ordered! Admin will upload the result.");
    }
}