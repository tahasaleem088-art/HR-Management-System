import java.sql.*;
import java.util.Scanner;

public class EmployeeManager {

    Scanner sc = new Scanner(System.in);

    // Add employee
    public void addEmployee() {
        System.out.print("Enter Employee Name: ");
        String name = sc.nextLine();
        System.out.print("Enter Department ID: ");
        int deptId = sc.nextInt();
        sc.nextLine();
        System.out.print("Enter Joining Date (YYYY-MM-DD): ");
        String date = sc.nextLine();

        String query = "INSERT INTO Employees (Emp_Name, Dept_ID, Joining_Date) VALUES (?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setString(1, name);
            ps.setInt(2, deptId);
            ps.setString(3, date);
            ps.executeUpdate();
            System.out.println("Employee added successfully!");

        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // View all employees
    public void viewEmployees() {
        String query = "SELECT e.Emp_ID, e.Emp_Name, d.Dept_Name, e.Joining_Date " +
                       "FROM Employees e JOIN Departments d ON e.Dept_ID = d.Dept_ID";

        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(query)) {

            System.out.println("\n--- Employees ---");
            while (rs.next()) {
                System.out.println("ID: " + rs.getInt("Emp_ID") +
                                   " | Name: " + rs.getString("Emp_Name") +
                                   " | Dept: " + rs.getString("Dept_Name") +
                                   " | Joined: " + rs.getString("Joining_Date"));
            }

        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // Update employee
    public void updateEmployee() {
        System.out.print("Enter Employee ID to update: ");
        int id = sc.nextInt();
        sc.nextLine();
        System.out.print("Enter New Name: ");
        String name = sc.nextLine();
        System.out.print("Enter New Department ID: ");
        int deptId = sc.nextInt();
        sc.nextLine();

        String query = "UPDATE Employees SET Emp_Name = ?, Dept_ID = ? WHERE Emp_ID = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setString(1, name);
            ps.setInt(2, deptId);
            ps.setInt(3, id);
            ps.executeUpdate();
            System.out.println("Employee updated successfully!");

        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // Delete employee
   public void deleteEmployee() {
    System.out.print("Enter Employee ID to delete: ");
    int id = sc.nextInt();
    sc.nextLine();

    try (Connection conn = DBConnection.getConnection()) {

        // Delete child records first (they reference Emp_ID via foreign keys)
        try (PreparedStatement ps1 = conn.prepareStatement(
                "DELETE FROM Salaries WHERE Emp_ID = ?")) {
            ps1.setInt(1, id);
            ps1.executeUpdate();
        }

        try (PreparedStatement ps2 = conn.prepareStatement(
                "DELETE FROM Attendance WHERE Emp_ID = ?")) {
            ps2.setInt(1, id);
            ps2.executeUpdate();
        }

        // Now the employee can be deleted safely
        try (PreparedStatement ps3 = conn.prepareStatement(
                "DELETE FROM Employees WHERE Emp_ID = ?")) {
            ps3.setInt(1, id);
            int rows = ps3.executeUpdate();
            if (rows == 0)
                System.out.println("No employee found with ID " + id);
            else
                System.out.println("Employee deleted successfully!");
        }

    } catch (SQLException e) {
        System.out.println("Error: " + e.getMessage());
    }
}
}