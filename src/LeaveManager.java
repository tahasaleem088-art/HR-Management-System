import java.sql.*;
import java.util.Scanner;

public class LeaveManager {

    Scanner sc = new Scanner(System.in);

    // Apply leave
    public void applyLeave() {
        System.out.print("Enter Employee ID: ");
        int empId = sc.nextInt();
        sc.nextLine();
        System.out.print("Enter Date (YYYY-MM-DD): ");
        String date = sc.nextLine();
        System.out.print("Enter Status (Present/Absent/Leave): ");
        String status = sc.nextLine();

        String query = "INSERT INTO Attendance (Emp_ID, Date, Status) VALUES (?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setInt(1, empId);
            ps.setString(2, date);
            ps.setString(3, status);
            ps.executeUpdate();
            System.out.println("Attendance recorded successfully!");

        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // View attendance
    public void viewAttendance() {
        System.out.print("Enter Employee ID: ");
        int empId = sc.nextInt();
        sc.nextLine();

        String query = "SELECT e.Emp_Name, a.Date, a.Status " +
                       "FROM Attendance a JOIN Employees e ON a.Emp_ID = e.Emp_ID " +
                       "WHERE a.Emp_ID = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setInt(1, empId);
            ResultSet rs = ps.executeQuery();

            System.out.println("\n--- Attendance Records ---");
            while (rs.next()) {
                System.out.println("Name: " + rs.getString("Emp_Name") +
                                   " | Date: " + rs.getString("Date") +
                                   " | Status: " + rs.getString("Status"));
            }

        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}