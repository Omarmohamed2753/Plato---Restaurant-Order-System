package javaproject1.DAL.Repo.Implementation;

import javaproject1.DAL.Repo.abstraction.IEmployeRepo;
import javaproject1.DAL.Entity.Employee;

import java.sql.*;

public class EmployeeRepo implements IEmployeRepo {

    private Connection connection;

    public EmployeeRepo(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void addEmploye(Employee emp) {
        String sql = "INSERT INTO Employees(id, name, age, phoneNumber, role, experiencesYear, imagePath) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, emp.getId());
            stmt.setString(2, emp.getName());
            stmt.setInt(3, emp.getAge());
            stmt.setString(4, emp.getPhoneNumber());
            stmt.setString(5, emp.getRole());
            stmt.setInt(6, emp.getExperiencesYear());
            stmt.setString(7, emp.getImagePath());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Employee getEmployeById(int id) {
        String sql = "SELECT * FROM Employees WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Employee emp = new Employee(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getInt("age"),
                        rs.getString("role"),
                        rs.getString("phoneNumber"),
                        rs.getString("imagePath"),
                        rs.getInt("experiencesYear")
                );
                return emp;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void updateEmploye(Employee emp) {
        String sql = "UPDATE Employees SET name = ?, age = ?, phoneNumber = ?, role = ?, experiencesYear = ?, imagePath = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, emp.getName());
            stmt.setInt(2, emp.getAge());
            stmt.setString(3, emp.getPhoneNumber());
            stmt.setString(4, emp.getRole());
            stmt.setInt(5, emp.getExperiencesYear());
            stmt.setString(6, emp.getImagePath());
            stmt.setInt(7, emp.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteEmploye(int id) {
        String sql = "DELETE FROM Employees WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
