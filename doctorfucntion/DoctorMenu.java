package doctorfucntion;
import java.sql.Connection;
import java.util.Scanner;

public class DoctorMenu {

    public static void menu(Connection conn, int doctorId) {
        Scanner sc = new Scanner(System.in);

        while (true) {
            try {
                System.out.println("\n========= DOCTOR MENU =========");
                System.out.println("1. View Booked Patients");
                System.out.println("2. View Patient Profile");
                System.out.println("3. Issue Prescription");
                System.out.println("4. Order Medical Test");
                System.out.println("5. View My Slots");
                System.out.println("6. Add Slot");
                System.out.println("7. Remove Slot");
                System.out.println("8. View Patient Feedback");
                System.out.println("0. Logout");

                System.out.print("Enter choice: ");
                int choice = sc.nextInt();
                sc.nextLine();

                switch (choice) {
                    case 1: ViewPatientProfile.viewBookedPatients(conn, doctorId); break;
                    case 2: ViewPatientProfile.viewProfile(conn, doctorId); break;
                    case 3: PrescriptionService.issuePrescription(conn, doctorId); break;
                    case 4: TestService.orderTest(conn, doctorId); break;
                    case 5: SlotManager.viewSlots(conn, doctorId); break;
                    case 6: SlotManager.addSlot(conn, doctorId); break;
                    case 7: SlotManager.removeSlot(conn, doctorId); break;
                    case 8: FeedbackService.viewFeedback(conn, doctorId); break;
                    case 0: System.out.println("Logging out..."); return;
                    default: System.out.println("Invalid choice!");
                }

            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }
}