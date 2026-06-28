import java.sql.*;
import java.util.Scanner;

public class DepartmentManager {
    
    Scanner sc = new Scanner(System.in);

    // Add a department
    public void addDepartment() {
        System.out.print("Enter Department Name: ");
        String name = sc.nextLine();

        String query = "INSERT INTO Departments (Dept_Name) VALUES (?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setString(1, name);
            ps.executeUpdate();
            System.out.println("Department added successfully!");

        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // View all departments
    public void viewDepartments() {
        String query = "SELECT * FROM Departments";

        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(query)) {

            System.out.println("\n--- Departments ---");
            while (rs.next()) {
                System.out.println("ID: " + rs.getInt("Dept_ID") + 
                                   " | Name: " + rs.getString("Dept_Name"));
            }

        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}