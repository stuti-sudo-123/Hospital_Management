package adminfunction;
//import connection.DBConnection;
import java.sql.Connection;
import java.util.Scanner;

public class AdminMenu {

    public static void menu(Connection conn) {
        Scanner sc = new Scanner(System.in);
        int adminId = 1; // fixed from your SQL

        while (true) {
            try {
                System.out.println("\n========= ADMIN MENU =========");
                System.out.println("1. Manage Patient & Doctor Records");
                System.out.println("2. Handle Complaints");
                System.out.println("3. Manage Billing");
                System.out.println("0. Logout");

                System.out.print("Enter choice: ");
                int choice = Integer.parseInt(sc.nextLine().trim());

                switch (choice) {
                    case 1:
                        new ManageRecords(adminId).showMenu();
                        break;
                    case 2:
                        new HandleComplaints(adminId).showMenu();
                        break;
                    case 3:
                        new ManageBilling(adminId).showMenu();
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