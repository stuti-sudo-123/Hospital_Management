package adminfunction;
import java.util.Scanner;
import java.sql.Connection;

public class AdminAuth {

    private static final String ADMIN_PASSWORD = "Admin@1234";

    public static void login(Connection conn) {
        Scanner sc = new Scanner(System.in);
        try {
            System.out.print("Admin Password: ");
            String pass = sc.nextLine().trim();

            if (pass.equals(ADMIN_PASSWORD)) {
                System.out.println("Admin Login Successful!");
                AdminMenu.menu(conn);
            } else {
                System.out.println("Invalid Admin Password!");
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}