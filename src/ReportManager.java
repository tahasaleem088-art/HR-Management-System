import java.sql.*;

public class ReportManager {

    // Total employees
    public void totalEmployees() {
        String query = "SELECT COUNT(*) AS Total FROM Employees";

        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(query)) {

            if (rs.next())
                System.out.println("Total Employees: " + rs.getInt("Total"));

        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // Average salary
    public void averageSalary() {
        String query = "SELECT AVG(Basic_Salary + Bonus) AS AvgSalary FROM Salaries";

        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(query)) {

            if (rs.next())
                System.out.println("Average Salary: " + rs.getDouble("AvgSalary"));

        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // Department wise employee count
    public void departmentStats() {
        String query = "SELECT d.Dept_Name, COUNT(e.Emp_ID) AS Total " +
                       "FROM Departments d LEFT JOIN Employees e ON d.Dept_ID = e.Dept_ID " +
                       "GROUP BY d.Dept_Name";

        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(query)) {

            System.out.println("\n--- Department Stats ---");
            while (rs.next()) {
                System.out.println("Dept: " + rs.getString("Dept_Name") +
                                   " | Employees: " + rs.getInt("Total"));
            }

        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}