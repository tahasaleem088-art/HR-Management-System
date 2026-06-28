import java.sql.*;
import java.util.Scanner;

public class SalaryManager {

    Scanner sc = new Scanner(System.in);

    // Add salary
    public void addSalary() {
        System.out.print("Enter Employee ID: ");
        int empId = sc.nextInt();
        sc.nextLine();
        System.out.print("Enter Basic Salary: ");
        double basic = sc.nextDouble();
        System.out.print("Enter Bonus: ");
        double bonus = sc.nextDouble();
        sc.nextLine();

        String query = "INSERT INTO Salaries (Emp_ID, Basic_Salary, Bonus) VALUES (?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setInt(1, empId);
            ps.setDouble(2, basic);
            ps.setDouble(3, bonus);
            ps.executeUpdate();
            System.out.println("Salary added successfully!");

        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // View salaries
    public void viewSalaries() {
        String query = "SELECT e.Emp_Name, s.Basic_Salary, s.Bonus, " +
                       "(s.Basic_Salary + s.Bonus) AS Total_Salary " +
                       "FROM Salaries s JOIN Employees e ON s.Emp_ID = e.Emp_ID";

        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(query)) {

            System.out.println("\n--- Salaries ---");
            while (rs.next()) {
                System.out.println("Name: " + rs.getString("Emp_Name") +
                                   " | Basic: " + rs.getDouble("Basic_Salary") +
                                   " | Bonus: " + rs.getDouble("Bonus") +
                                   " | Total: " + rs.getDouble("Total_Salary"));
            }

        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}