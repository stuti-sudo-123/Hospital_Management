package user;

import connection.DBConnection;
import java.sql.Connection;
import java.util.Scanner;
import patientfucntion.PatientAuth;
import patientfucntion.PatientMenu;
import doctorfucntion.DoctorAuth;
import doctorfucntion.DoctorMenu;
import adminfunction.AdminAuth;
//import adminfunction.AdminMenu;

public class Main {

    public static void main(String[] args) {

        Connection conn = DBConnection.getConnection();

        if (conn == null) {
            System.out.println("Failed to connect to database. Exiting.");
            return;
        }

        Scanner sc = new Scanner(System.in);

        while (true) {
            try {
                System.out.println("\n===== HOSPITAL MANAGEMENT SYSTEM =====");
                System.out.println("1. Patient Login");
                System.out.println("2. Patient Register");
                System.out.println("3. Doctor Login");
                System.out.println("4. Administrator Login");
                System.out.println("0. Exit");

                System.out.print("Enter choice: ");
                int choice = Integer.parseInt(sc.nextLine().trim());

                switch (choice) {

                    case 1:
                        int patientId = PatientAuth.login(conn);
                        if (patientId != -1) {
                            PatientMenu.menu(conn, patientId);
                        }
                        break;

                    case 2:
                        PatientAuth.register(conn);
                        break;

                    case 3:
                        int doctorId = DoctorAuth.login(conn);
                        DoctorMenu.menu(conn, doctorId);
                        break;

                    case 4:
                        AdminAuth.login(conn);
                        break;

                    case 0:
                        System.out.println("Goodbye!");
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