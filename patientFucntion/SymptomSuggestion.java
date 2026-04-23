package patientfucntion;

import java.sql.*;
import java.util.Scanner;

public class SymptomSuggestion {

    public static void suggestDoctors(Connection conn) {
        Scanner sc = new Scanner(System.in);

        try {
            // Print symptom list first
            System.out.println("\n===== AVAILABLE SYMPTOMS =====");
            PreparedStatement psList = conn.prepareStatement(
                "SELECT symptom_id, symptom_name FROM symptoms ORDER BY symptom_id"
            );
            ResultSet rsList = psList.executeQuery();
            boolean anySymptom = false;
            while (rsList.next()) {
                anySymptom = true;
                System.out.println(rsList.getInt("symptom_id") + ". " + rsList.getString("symptom_name"));
            }
            if (!anySymptom) {
                System.out.println("No symptoms found in database.");
                return;
            }
            rsList.close();
            psList.close();

            // Ask for input
            System.out.print("\nEnter symptom name: ");
            String symptom = sc.nextLine().trim();

            PreparedStatement ps = conn.prepareStatement(
                "SELECT d.doctor_id, u.full_name, dep.name " +
                "FROM symptoms s " +
                "JOIN symptom_department_map m ON s.symptom_id = m.symptom_id " +
                "JOIN departments dep ON dep.department_id = m.department_id " +
                "JOIN doctors d ON d.department_id = dep.department_id " +
                "JOIN users u ON u.user_id = d.doctor_id " +
                "WHERE LOWER(s.symptom_name) = LOWER(?)"
            );

            ps.setString(1, symptom);
            ResultSet rs = ps.executeQuery();

            boolean found = false;
            System.out.println("\n===== SUGGESTED DOCTORS =====");
            while (rs.next()) {
                found = true;
                System.out.println("Doctor ID : " + rs.getInt(1)
                        + " | Name : " + rs.getString(2)
                        + " | Dept : " + rs.getString(3));
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