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
        String query = "INSERT INTO employees (id, name, age, role, phone_number, image_path, experiences_year, restaurant_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, employee.getId());
            stmt.setString(2, employee.getName());
            stmt.setInt(3, employee.getAge());
            stmt.setString(4, employee.getRole());
            stmt.setString(5, employee.getPhoneNumber());
            stmt.setString(6, employee.getImagePath());
            stmt.setInt(7, employee.getExperiencesYear());
            stmt.setObject(8, 
                (employee.getRestaurant() != null) ? employee.getRestaurant().getRestaurantId() : null, 
                java.sql.Types.INTEGER);

            stmt.executeUpdate();
            System.out.println(" Employee added successfully.");

        } catch (SQLException e) {
            System.out.println(" Error adding employee: " + e.getMessage());
        }
    }

    @Override
    public Employee getEmployeeById(int id) {
        String query = "SELECT * FROM employees WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Employee emp = new Employee();
                emp.setId(rs.getInt("id"));
                emp.setName(rs.getString("name"));
                emp.setAge(rs.getInt("age"));
                emp.setRole(rs.getString("role"));
                emp.setPhoneNumber(rs.getString("phone_number"));
                emp.setImagePath(rs.getString("image_path"));
                emp.setExperiencesYear(rs.getInt("experiences_year"));
                return emp;
            }

        } catch (SQLException e) {
            System.out.println(" Error fetching employee: " + e.getMessage());
        }
        return null;
    }

    @Override
    public void updateEmployee(Employee employee) {
        String query = "UPDATE employees SET name=?, age=?, role=?, phone_number=?, image_path=?, experiences_year=?, restaurant_id=? WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, employee.getName());
            stmt.setInt(2, employee.getAge());
            stmt.setString(3, employee.getRole());
            stmt.setString(4, employee.getPhoneNumber());
            stmt.setString(5, employee.getImagePath());
            stmt.setInt(6, employee.getExperiencesYear());
            stmt.setObject(7,
                (employee.getRestaurant() != null) ? employee.getRestaurant().getRestaurantId() : null,
                java.sql.Types.INTEGER);
            stmt.setInt(8, employee.getId());

            int rows = stmt.executeUpdate();
            if (rows > 0)
                System.out.println(" Employee updated successfully.");
            else
                System.out.println(" No employee found with ID: " + employee.getId());

        } catch (SQLException e) {
            System.out.println(" Error updating employee: " + e.getMessage());
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
                System.out.println("Employee deleted successfully.");
            else
                System.out.println(" No employee found with ID: " + id);

        } catch (SQLException e) {
            System.out.println(" Error deleting employee: " + e.getMessage());
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
                Employee emp = new Employee();
                emp.setId(rs.getInt("id"));
                emp.setName(rs.getString("name"));
                emp.setAge(rs.getInt("age"));
                emp.setRole(rs.getString("role"));
                emp.setPhoneNumber(rs.getString("phone_number"));
                emp.setImagePath(rs.getString("image_path"));
                emp.setExperiencesYear(rs.getInt("experiences_year"));
                employees.add(emp);
            }

        } catch (SQLException e) {
            System.out.println(" Error fetching employees: " + e.getMessage());
        }
        return employees;
    }
}
