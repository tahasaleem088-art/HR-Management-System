import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

/*
 * HRMS GUI  (Swing)
 * -----------------
 * This file ONLY adds a graphical interface.
 * All the SQL is the same as your console managers, and it reuses
 * your existing DBConnection class for the database connection.
 *
 * Keep all your old files in the same folder. To run the GUI,
 * just run THIS file (HRMSApp) instead of Main.
 */
public class HRMSApp extends JFrame {

    public HRMSApp() {
        setTitle("HRMS - Human Resource Management System");
        setSize(700, 450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // Header
        JLabel header = new JLabel("Human Resource Management System", SwingConstants.CENTER);
        header.setFont(new Font("Segoe UI", Font.BOLD, 20));
        header.setBorder(new EmptyBorder(12, 0, 12, 0));
        add(header, BorderLayout.NORTH);

        // One tab per module
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Departments", departmentPanel());
        tabs.addTab("Employees", employeePanel());
        tabs.addTab("Salaries", salaryPanel());
        tabs.addTab("Attendance", attendancePanel());
        tabs.addTab("Reports", reportPanel());
        add(tabs);

        setVisible(true);
    }

    // ===================================================================
    // 1. DEPARTMENTS
    // ===================================================================
    private JPanel departmentPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(12, 12, 12, 12));

        JTextField name = new JTextField(15);
        JButton add = new JButton("Add Department");
        JButton show = new JButton("Show All");

        JPanel form = formBox("Department Details");
        form.add(new JLabel("Department Name:"));
        form.add(name);
        form.add(add);
        form.add(show);

        DefaultTableModel model = new DefaultTableModel(
                new String[]{"Dept_ID", "Dept_Name"}, 0);
        JTable table = new JTable(model);

        panel.add(form, BorderLayout.NORTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        // INSERT INTO Departments (Dept_Name) VALUES (?)
        add.addActionListener(e -> {
            if (name.getText().trim().isEmpty()) {
                msg("Please enter a department name.");
                return;
            }
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(
                         "INSERT INTO Departments (Dept_Name) VALUES (?)")) {
                ps.setString(1, name.getText().trim());
                ps.executeUpdate();
                msg("Department added successfully!");
                name.setText("");
            } catch (SQLException ex) { err(ex); }
        });

        // SELECT * FROM Departments
        show.addActionListener(e -> {
            try (Connection conn = DBConnection.getConnection();
                 Statement st = conn.createStatement();
                 ResultSet rs = st.executeQuery("SELECT * FROM Departments")) {
                model.setRowCount(0);
                while (rs.next()) {
                    model.addRow(new Object[]{
                            rs.getInt("Dept_ID"),
                            rs.getString("Dept_Name")
                    });
                }
            } catch (SQLException ex) { err(ex); }
        });

        return panel;
    }

    // ===================================================================
    // 2. EMPLOYEES  (add / view / update / delete)
    // ===================================================================
    private JPanel employeePanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(12, 12, 12, 12));

        JTextField empId = new JTextField(6);   // used for Update / Delete
        JTextField name = new JTextField(12);
        JTextField deptId = new JTextField(6);
        JTextField date = new JTextField(10);   // YYYY-MM-DD

        JButton add = new JButton("Add");
        JButton update = new JButton("Update");
        JButton delete = new JButton("Delete");
        JButton show = new JButton("Show All");

        JPanel form = formBox("Employee Details");
        form.add(new JLabel("Emp ID (for update/delete):"));
        form.add(empId);
        form.add(new JLabel("Name:"));
        form.add(name);
        form.add(new JLabel("Dept ID:"));
        form.add(deptId);
        form.add(new JLabel("Joining Date (YYYY-MM-DD):"));
        form.add(date);
        form.add(add);
        form.add(update);
        form.add(delete);
        form.add(show);

        DefaultTableModel model = new DefaultTableModel(
                new String[]{"Emp_ID", "Emp_Name", "Dept_Name", "Joining_Date"}, 0);
        JTable table = new JTable(model);

        panel.add(form, BorderLayout.NORTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        // INSERT INTO Employees (Emp_Name, Dept_ID, Joining_Date) VALUES (?, ?, ?)
        add.addActionListener(e -> {
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(
                         "INSERT INTO Employees (Emp_Name, Dept_ID, Joining_Date) VALUES (?, ?, ?)")) {
                ps.setString(1, name.getText().trim());
                ps.setInt(2, Integer.parseInt(deptId.getText().trim()));
                ps.setString(3, date.getText().trim());
                ps.executeUpdate();
                msg("Employee added successfully!");
            } catch (NumberFormatException ne) {
                msg("Dept ID must be a number.");
            } catch (SQLException ex) { err(ex); }
        });

        // UPDATE Employees SET Emp_Name = ?, Dept_ID = ? WHERE Emp_ID = ?
        update.addActionListener(e -> {
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(
                         "UPDATE Employees SET Emp_Name = ?, Dept_ID = ? WHERE Emp_ID = ?")) {
                ps.setString(1, name.getText().trim());
                ps.setInt(2, Integer.parseInt(deptId.getText().trim()));
                ps.setInt(3, Integer.parseInt(empId.getText().trim()));
                ps.executeUpdate();
                msg("Employee updated successfully!");
            } catch (NumberFormatException ne) {
                msg("Emp ID and Dept ID must be numbers.");
            } catch (SQLException ex) { err(ex); }
        });

        // DELETE FROM Employees WHERE Emp_ID = ?
        delete.addActionListener(e -> {
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(
                         "DELETE FROM Employees WHERE Emp_ID = ?")) {
                ps.setInt(1, Integer.parseInt(empId.getText().trim()));
                ps.executeUpdate();
                msg("Employee deleted successfully!");
            } catch (NumberFormatException ne) {
                msg("Emp ID must be a number.");
            } catch (SQLException ex) { err(ex); }
        });

        // SELECT ... FROM Employees e JOIN Departments d ON e.Dept_ID = d.Dept_ID
        show.addActionListener(e -> {
            String q = "SELECT e.Emp_ID, e.Emp_Name, d.Dept_Name, e.Joining_Date " +
                       "FROM Employees e JOIN Departments d ON e.Dept_ID = d.Dept_ID";
            try (Connection conn = DBConnection.getConnection();
                 Statement st = conn.createStatement();
                 ResultSet rs = st.executeQuery(q)) {
                model.setRowCount(0);
                while (rs.next()) {
                    model.addRow(new Object[]{
                            rs.getInt("Emp_ID"),
                            rs.getString("Emp_Name"),
                            rs.getString("Dept_Name"),
                            rs.getString("Joining_Date")
                    });
                }
            } catch (SQLException ex) { err(ex); }
        });

        return panel;
    }

    // ===================================================================
    // 3. SALARIES
    // ===================================================================
    private JPanel salaryPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(12, 12, 12, 12));

        JTextField empId = new JTextField(6);
        JTextField basic = new JTextField(8);
        JTextField bonus = new JTextField(8);
        JButton add = new JButton("Add Salary");
        JButton show = new JButton("Show All");

        JPanel form = formBox("Salary Details");
        form.add(new JLabel("Emp ID:"));
        form.add(empId);
        form.add(new JLabel("Basic Salary:"));
        form.add(basic);
        form.add(new JLabel("Bonus:"));
        form.add(bonus);
        form.add(add);
        form.add(show);

        DefaultTableModel model = new DefaultTableModel(
                new String[]{"Emp_Name", "Basic_Salary", "Bonus", "Total_Salary"}, 0);
        JTable table = new JTable(model);

        panel.add(form, BorderLayout.NORTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        // INSERT INTO Salaries (Emp_ID, Basic_Salary, Bonus) VALUES (?, ?, ?)
        add.addActionListener(e -> {
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(
                         "INSERT INTO Salaries (Emp_ID, Basic_Salary, Bonus) VALUES (?, ?, ?)")) {
                ps.setInt(1, Integer.parseInt(empId.getText().trim()));
                ps.setDouble(2, Double.parseDouble(basic.getText().trim()));
                ps.setDouble(3, Double.parseDouble(bonus.getText().trim()));
                ps.executeUpdate();
                msg("Salary added successfully!");
            } catch (NumberFormatException ne) {
                msg("Emp ID, Basic and Bonus must be numbers.");
            } catch (SQLException ex) { err(ex); }
        });

        // SELECT ... (Basic_Salary + Bonus) AS Total_Salary ... JOIN Employees
        show.addActionListener(e -> {
            String q = "SELECT e.Emp_Name, s.Basic_Salary, s.Bonus, " +
                       "(s.Basic_Salary + s.Bonus) AS Total_Salary " +
                       "FROM Salaries s JOIN Employees e ON s.Emp_ID = e.Emp_ID";
            try (Connection conn = DBConnection.getConnection();
                 Statement st = conn.createStatement();
                 ResultSet rs = st.executeQuery(q)) {
                model.setRowCount(0);
                while (rs.next()) {
                    model.addRow(new Object[]{
                            rs.getString("Emp_Name"),
                            rs.getDouble("Basic_Salary"),
                            rs.getDouble("Bonus"),
                            rs.getDouble("Total_Salary")
                    });
                }
            } catch (SQLException ex) { err(ex); }
        });

        return panel;
    }

    // ===================================================================
    // 4. ATTENDANCE / LEAVE
    // ===================================================================
    private JPanel attendancePanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(12, 12, 12, 12));

        JTextField empId = new JTextField(6);
        JTextField date = new JTextField(10);   // YYYY-MM-DD
        JComboBox<String> status = new JComboBox<>(new String[]{"Present", "Absent", "Leave"});
        JButton apply = new JButton("Record");
        JButton view = new JButton("View (by Emp ID)");

        JPanel form = formBox("Attendance Details");
        form.add(new JLabel("Emp ID:"));
        form.add(empId);
        form.add(new JLabel("Date (YYYY-MM-DD):"));
        form.add(date);
        form.add(new JLabel("Status:"));
        form.add(status);
        form.add(apply);
        form.add(view);

        DefaultTableModel model = new DefaultTableModel(
                new String[]{"Emp_Name", "Date", "Status"}, 0);
        JTable table = new JTable(model);

        panel.add(form, BorderLayout.NORTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        // INSERT INTO Attendance (Emp_ID, Date, Status) VALUES (?, ?, ?)
        apply.addActionListener(e -> {
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(
                         "INSERT INTO Attendance (Emp_ID, Date, Status) VALUES (?, ?, ?)")) {
                ps.setInt(1, Integer.parseInt(empId.getText().trim()));
                ps.setString(2, date.getText().trim());
                ps.setString(3, (String) status.getSelectedItem());
                ps.executeUpdate();
                msg("Attendance recorded successfully!");
            } catch (NumberFormatException ne) {
                msg("Emp ID must be a number.");
            } catch (SQLException ex) { err(ex); }
        });

        // SELECT ... FROM Attendance a JOIN Employees e ... WHERE a.Emp_ID = ?
        view.addActionListener(e -> {
            String q = "SELECT e.Emp_Name, a.Date, a.Status " +
                       "FROM Attendance a JOIN Employees e ON a.Emp_ID = e.Emp_ID " +
                       "WHERE a.Emp_ID = ?";
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(q)) {
                ps.setInt(1, Integer.parseInt(empId.getText().trim()));
                ResultSet rs = ps.executeQuery();
                model.setRowCount(0);
                while (rs.next()) {
                    model.addRow(new Object[]{
                            rs.getString("Emp_Name"),
                            rs.getString("Date"),
                            rs.getString("Status")
                    });
                }
            } catch (NumberFormatException ne) {
                msg("Emp ID must be a number.");
            } catch (SQLException ex) { err(ex); }
        });

        return panel;
    }

    // ===================================================================
    // 5. REPORTS
    // ===================================================================
    private JPanel reportPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(12, 12, 12, 12));

        JButton total = new JButton("Total Employees");
        JButton avg = new JButton("Average Salary");
        JButton stats = new JButton("Department Stats");
        JLabel result = new JLabel("Choose a report above.");
        result.setFont(new Font("Segoe UI", Font.BOLD, 14));

        JPanel top = formBox("Reports");
        top.add(total);
        top.add(avg);
        top.add(stats);
        top.add(result);

        DefaultTableModel model = new DefaultTableModel(
                new String[]{"Dept_Name", "Total_Employees"}, 0);
        JTable table = new JTable(model);

        panel.add(top, BorderLayout.NORTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        // SELECT COUNT(*) AS Total FROM Employees
        total.addActionListener(e -> {
            try (Connection conn = DBConnection.getConnection();
                 Statement st = conn.createStatement();
                 ResultSet rs = st.executeQuery("SELECT COUNT(*) AS Total FROM Employees")) {
                model.setRowCount(0);
                if (rs.next()) result.setText("Total Employees: " + rs.getInt("Total"));
            } catch (SQLException ex) { err(ex); }
        });

        // SELECT AVG(Basic_Salary + Bonus) AS AvgSalary FROM Salaries
        avg.addActionListener(e -> {
            try (Connection conn = DBConnection.getConnection();
                 Statement st = conn.createStatement();
                 ResultSet rs = st.executeQuery(
                         "SELECT AVG(Basic_Salary + Bonus) AS AvgSalary FROM Salaries")) {
                model.setRowCount(0);
                if (rs.next()) result.setText("Average Salary: " + rs.getDouble("AvgSalary"));
            } catch (SQLException ex) { err(ex); }
        });

        // SELECT d.Dept_Name, COUNT(e.Emp_ID) AS Total ... GROUP BY d.Dept_Name
        stats.addActionListener(e -> {
            String q = "SELECT d.Dept_Name, COUNT(e.Emp_ID) AS Total " +
                       "FROM Departments d LEFT JOIN Employees e ON d.Dept_ID = e.Dept_ID " +
                       "GROUP BY d.Dept_Name";
            try (Connection conn = DBConnection.getConnection();
                 Statement st = conn.createStatement();
                 ResultSet rs = st.executeQuery(q)) {
                model.setRowCount(0);
                result.setText("Department-wise employee count:");
                while (rs.next()) {
                    model.addRow(new Object[]{
                            rs.getString("Dept_Name"),
                            rs.getInt("Total")
                    });
                }
            } catch (SQLException ex) { err(ex); }
        });

        return panel;
    }

    // ===================================================================
    // Small helpers (kept simple)
    // ===================================================================
    private JPanel formBox(String title) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        p.setBorder(new TitledBorder(title));
        return p;
    }

    private void msg(String text) {
        JOptionPane.showMessageDialog(this, text);
    }

    private void err(SQLException ex) {
        JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(),
                "Database Error", JOptionPane.ERROR_MESSAGE);
    }

    // ===================================================================
    public static void main(String[] args) {
        // Modern look instead of default gray Swing
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(HRMSApp::new);
    }
}