package patientfucntion;

import java.sql.Connection;
import java.util.Scanner;

public class PatientMenu {

    public static void menu(Connection conn, int patientId) {
        Scanner sc = new Scanner(System.in);

        while (true) {
            try {
                System.out.println("\n========= PATIENT MENU =========");
                System.out.println("1. Symptom-based Doctor Suggestion");
                System.out.println("2. Book Appointment");
                System.out.println("3. View Appointments");
                System.out.println("4. Cancel Appointment");
                System.out.println("5. View Medical Records");
                System.out.println("6. Submit Complaint");
                System.out.println("7. View Billing Summary");
                System.out.println("0. Logout");

                System.out.print("Enter choice: ");
                int choice = sc.nextInt();

                switch (choice) {

                    case 1:
                        SymptomSuggestion.suggestDoctors(conn);
                        break;

                    case 2:
                        AppointmentService.bookAppointment(conn, patientId);
                        break;

                    case 3:
                        ManageAppointments.viewAppointments(conn, patientId);
                        break;

                    case 4:
                        ManageAppointments.cancelAppointment(conn, patientId);
                        break;

                    case 5:
                        MedicalRecords.viewReports(conn, patientId);
                        break;

                    case 6:
                        ComplaintService.submitComplaint(conn, patientId);
                        break;

                    case 7:
                        BillingService.viewBills(conn, patientId);
                        break;

                    case 0:
                        System.out.println("Logging out...");
                        return;

                    default:
                        System.out.println("Invalid choice!");

                }

            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }
}