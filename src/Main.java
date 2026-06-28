import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        DepartmentManager dm = new DepartmentManager();
        EmployeeManager em = new EmployeeManager();
        SalaryManager sm = new SalaryManager();
        LeaveManager lm = new LeaveManager();
        ReportManager rm = new ReportManager();

        while (true) {
            System.out.println("\n===== HRMS MAIN MENU =====");
            System.out.println("1. Department Management");
            System.out.println("2. Employee Management");
            System.out.println("3. Salary Management");
            System.out.println("4. Leave Management");
            System.out.println("5. Reports");
            System.out.println("6. Exit");
            System.out.print("Choose: ");

            int choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {
                case 1:
                    System.out.println("\n1. Add Department\n2. View Departments");
                    System.out.print("Choose: ");
                    int dc = sc.nextInt(); sc.nextLine();
                    if (dc == 1) dm.addDepartment();
                    else if (dc == 2) dm.viewDepartments();
                    break;
                case 2:
                    System.out.println("\n1. Add Employee\n2. View Employees\n3. Update Employee\n4. Delete Employee");
                    System.out.print("Choose: ");
                    int ec = sc.nextInt(); sc.nextLine();
                    if (ec == 1) em.addEmployee();
                    else if (ec == 2) em.viewEmployees();
                    else if (ec == 3) em.updateEmployee();
                    else if (ec == 4) em.deleteEmployee();
                    break;
                case 3:
                    System.out.println("\n1. Add Salary\n2. View Salaries");
                    System.out.print("Choose: ");
                    int sc2 = sc.nextInt(); sc.nextLine();
                    if (sc2 == 1) sm.addSalary();
                    else if (sc2 == 2) sm.viewSalaries();
                    break;
                case 4:
                    System.out.println("\n1. Apply Leave\n2. View Attendance");
                    System.out.print("Choose: ");
                    int lc = sc.nextInt(); sc.nextLine();
                    if (lc == 1) lm.applyLeave();
                    else if (lc == 2) lm.viewAttendance();
                    break;
                case 5:
                    System.out.println("\n1. Total Employees\n2. Average Salary\n3. Department Stats");
                    System.out.print("Choose: ");
                    int rc = sc.nextInt(); sc.nextLine();
                    if (rc == 1) rm.totalEmployees();
                    else if (rc == 2) rm.averageSalary();
                    else if (rc == 3) rm.departmentStats();
                    break;
                case 6:
                    System.out.println("Goodbye!");
                    return;
                default:
                    System.out.println("Invalid choice!");
            }
        }
    }
}