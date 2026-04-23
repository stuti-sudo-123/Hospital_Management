package patientfucntion;

import java.sql.*;
import java.util.Scanner;

public class PatientAuth {

    public static int login(Connection conn) {
        Scanner sc = new Scanner(System.in);
        int userId = -1;

        try {
            System.out.print("Email: ");
            String email = sc.nextLine();

            System.out.print("Password: ");
            String password = sc.nextLine();

            PreparedStatement ps = conn.prepareStatement(
                    "SELECT user_id FROM users WHERE email=? AND password_hash=? AND role='PATIENT'"
            );

            ps.setString(1, email);
            ps.setString(2, password);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                System.out.println("Login Successful!");
                userId = rs.getInt("user_id");
            } else {
                System.out.println("Invalid Login!");
            }

            rs.close();
            ps.close();

        } catch (SQLException e) {
            System.out.println("Database Error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }

        return userId;
    }

    public static void register(Connection conn) {
        Scanner sc = new Scanner(System.in);

        try {
            System.out.print("Full Name: ");
            String name = sc.nextLine();

            System.out.print("Email: ");
            String email = sc.nextLine();

            System.out.print("Password: ");
            String pass = sc.nextLine();

            System.out.print("DOB (YYYY-MM-DD): ");
            String dob = sc.nextLine();

            System.out.print("Contact: ");
            String contact = sc.nextLine();

            System.out.print("Blood Group: ");
            String bg = sc.nextLine();

            // Insert into users table
            PreparedStatement ps1 = conn.prepareStatement(
                    "INSERT INTO users(full_name,email,password_hash,role) VALUES (?,?,?,'PATIENT')",
                    Statement.RETURN_GENERATED_KEYS
            );

            ps1.setString(1, name);
            ps1.setString(2, email);
            ps1.setString(3, pass);
            ps1.executeUpdate();

            ResultSet rs = ps1.getGeneratedKeys();
            rs.next();
            int userId = rs.getInt(1);

            rs.close();
            ps1.close();

            // Insert into patients table
            PreparedStatement ps2 = conn.prepareStatement(
                    "INSERT INTO patients VALUES (?,?,?,?)"
            );

            ps2.setInt(1, userId);
            ps2.setString(2, dob);
            ps2.setString(3, contact);
            ps2.setString(4, bg);
            ps2.executeUpdate();

            ps2.close();

            // Medical background
            System.out.print("Surgeries: ");
            String surg = sc.nextLine();

            System.out.print("Medicines: ");
            String meds = sc.nextLine();

            System.out.print("Allergies: ");
            String all = sc.nextLine();

            PreparedStatement ps3 = conn.prepareStatement(
                    "INSERT INTO patient_medical_background(patient_id,surgeries,current_meds,allergies) VALUES (?,?,?,?)"
            );

            ps3.setInt(1, userId);
            ps3.setString(2, surg);
            ps3.setString(3, meds);
            ps3.setString(4, all);
            ps3.executeUpdate();

            ps3.close();

            System.out.println("Registration Successful!");

        } catch (SQLException e) {
            System.out.println("Database Error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}