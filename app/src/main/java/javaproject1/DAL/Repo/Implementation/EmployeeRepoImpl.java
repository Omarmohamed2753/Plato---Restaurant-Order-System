package javaproject1.DAL.Repo.Implementation;

import javaproject1.DAL.Entity.Employee;
import javaproject1.DAL.DataBase.DBConnection;
import javaproject1.DAL.Repo.abstraction.IEmployeeRepo;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EmployeeRepoImpl implements IEmployeeRepo {

    @Override
    public void addEmployee(Employee employee) {
        /*
         * CRITICAL FIX: the original query had 'id' in the column list and
         * passed employee.getId() which is always "" (empty string from the
         * no-arg Employee constructor).  That caused the INSERT to fail silently
         * (caught exception, printed to console, returned).
         * Solution: remove 'id' entirely and let the DB auto-generate it.
         */
        String query =
            "INSERT INTO employees (name, age, role, phone_number, image_path, experiences_year, restaurant_id) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, employee.getName());
            stmt.setInt(2, employee.getAge());
            stmt.setString(3, employee.getRole());
            stmt.setString(4, employee.getPhoneNumber());
            stmt.setString(5, employee.getImagePath() != null ? employee.getImagePath() : "");
            stmt.setInt(6, employee.getExperiencesYear());

            // Parse restaurant_id as a proper INTEGER (original code passed a String
            // with Types.INTEGER which silently stored NULL)
            if (employee.getRestaurant() != null && employee.getRestaurant().getRestaurantId() != null) {
                try {
                    stmt.setInt(7, Integer.parseInt(employee.getRestaurant().getRestaurantId()));
                } catch (NumberFormatException ex) {
                    System.err.println("Invalid restaurant ID: " + employee.getRestaurant().getRestaurantId());
                    stmt.setNull(7, Types.INTEGER);
                }
            } else {
                stmt.setNull(7, Types.INTEGER);
            }

            int rows = stmt.executeUpdate();

            if (rows > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        employee.setId(rs.getString(1));
                        System.out.println("Employee added successfully. ID=" + employee.getId()
                            + "  restaurant_id="
                            + (employee.getRestaurant() != null ? employee.getRestaurant().getRestaurantId() : "null"));
                    }
                }
            } else {
                System.err.println("ERROR: Employee INSERT affected 0 rows!");
            }

        } catch (SQLException e) {
            System.err.println("SQL Error adding employee: " + e.getMessage());
            System.err.println("SQLState=" + e.getSQLState() + "  ErrorCode=" + e.getErrorCode());
            e.printStackTrace();
        }
    }

    @Override
    public Employee getEmployeeById(int id) {
        String query = "SELECT * FROM employees WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapToEmployee(rs);
                }
            }

        } catch (SQLException e) {
            System.err.println("Error fetching employee by ID: " + e.getMessage());
        }
        return null;
    }

    @Override
    public void updateEmployee(Employee employee) {
        String query =
            "UPDATE employees SET name=?, age=?, role=?, phone_number=?, image_path=?, experiences_year=?, restaurant_id=? " +
            "WHERE id=?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, employee.getName());
            stmt.setInt(2, employee.getAge());
            stmt.setString(3, employee.getRole());
            stmt.setString(4, employee.getPhoneNumber());
            stmt.setString(5, employee.getImagePath() != null ? employee.getImagePath() : "");
            stmt.setInt(6, employee.getExperiencesYear());

            if (employee.getRestaurant() != null && employee.getRestaurant().getRestaurantId() != null) {
                try {
                    stmt.setInt(7, Integer.parseInt(employee.getRestaurant().getRestaurantId()));
                } catch (NumberFormatException ex) {
                    stmt.setNull(7, Types.INTEGER);
                }
            } else {
                stmt.setNull(7, Types.INTEGER);
            }

            stmt.setString(8, employee.getId());

            int rows = stmt.executeUpdate();
            if (rows > 0)
                System.out.println("Employee updated: " + employee.getId());
            else
                System.err.println("No employee found with ID: " + employee.getId());

        } catch (SQLException e) {
            System.err.println("Error updating employee: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void deleteEmployee(int id) {
        String query = "DELETE FROM employees WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, id);
            int rows = stmt.executeUpdate();
            if (rows > 0)
                System.out.println("Employee deleted: " + id);
            else
                System.err.println("No employee found with ID: " + id);

        } catch (SQLException e) {
            System.err.println("Error deleting employee: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public List<Employee> getAllEmployees() {
        List<Employee> employees = new ArrayList<>();
        String query = "SELECT * FROM employees";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                employees.add(mapToEmployee(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error fetching all employees: " + e.getMessage());
            e.printStackTrace();
        }
        return employees;
    }

    private Employee mapToEmployee(ResultSet rs) throws SQLException {
        Employee emp = new Employee();
        emp.setId(rs.getString("id"));
        emp.setName(rs.getString("name"));
        emp.setAge(rs.getInt("age"));
        emp.setRole(rs.getString("role"));
        emp.setPhoneNumber(rs.getString("phone_number"));
        emp.setImagePath(rs.getString("image_path"));
        emp.setExperiencesYear(rs.getInt("experiences_year"));
        return emp;
    }
}