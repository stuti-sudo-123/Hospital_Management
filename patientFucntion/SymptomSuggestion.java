package patientFucntion;

import java.sql.*;
import java.util.Scanner;

public class SymptomSuggestion {

    public static void suggestDoctors(Connection conn) {
        Scanner sc = new Scanner(System.in);

        try {
            System.out.print("Enter symptom: ");
            String symptom = sc.nextLine();

            PreparedStatement ps = conn.prepareStatement(
                "SELECT d.doctor_id, u.full_name, dep.name " +
                "FROM symptoms s " +
                "JOIN symptom_department_map m ON s.symptom_id=m.symptom_id " +
                "JOIN departments dep ON dep.department_id=m.department_id " +
                "JOIN doctors d ON d.department_id=dep.department_id " +
                "JOIN users u ON u.user_id=d.doctor_id " +
                "WHERE s.symptom_name=?"
            );

            ps.setString(1, symptom);

            ResultSet rs = ps.executeQuery();

            boolean found = false;

            while (rs.next()) {
                found = true;
                System.out.println("Doctor ID: " + rs.getInt(1)
                        + " | Name: " + rs.getString(2)
                        + " | Dept: " + rs.getString(3));
            }

            if (!found) {
                System.out.println("No doctors found for this symptom.");
            }

            rs.close();
            ps.close();

        } catch (SQLException e) {
            System.out.println("Database Error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}