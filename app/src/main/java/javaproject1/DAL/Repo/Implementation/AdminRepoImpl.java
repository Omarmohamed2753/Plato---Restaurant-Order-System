package javaproject1.DAL.Repo.Implementation;

import javaproject1.DAL.DataBase.DBConnection;
import javaproject1.DAL.Entity.Admin;
import javaproject1.DAL.Entity.Restaurant;
import javaproject1.DAL.Repo.abstraction.IAdminRepo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AdminRepoImpl implements IAdminRepo {

    @Override
    public void addAdmin(Admin admin) {
        String sql = "INSERT INTO admin (name, age, phone_number, email, password, restaurant_id) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, admin.getName());
            stmt.setInt(2, admin.getAge());
            stmt.setString(3, admin.getPhoneNumber());
            stmt.setString(4, admin.getEmail());
            stmt.setString(5, admin.getPassword());

            if (admin.getRestaurant() != null) {
                stmt.setInt(6, admin.getRestaurant().getRestaurantId());
            } else {
                stmt.setNull(6, java.sql.Types.INTEGER);
            }

            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    admin.setId(rs.getInt(1));
                }
            }

        } catch (SQLException e) {
            System.err.println("Error adding admin: " + e.getMessage());
        }
    }

    @Override
    public Admin getAdminById(int id) {
        String sql = "SELECT * FROM admin WHERE id = ?";
        Admin admin = null;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    admin = extractAdminFromResultSet(rs);
                }
            }

        } catch (SQLException e) {
            System.err.println("Error fetching admin by ID: " + e.getMessage());
        }

        return admin;
    }

    @Override
    public void updateAdmin(Admin admin) {
        String sql = "UPDATE admin SET name = ?, age = ?, phone_number = ?, email = ?, password = ?, restaurant_id = ? WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, admin.getName());
            stmt.setInt(2, admin.getAge());
            stmt.setString(3, admin.getPhoneNumber());
            stmt.setString(4, admin.getEmail());
            stmt.setString(5, admin.getPassword());

            if (admin.getRestaurant() != null) {
                stmt.setInt(6, admin.getRestaurant().getRestaurantId());
            } else {
                stmt.setNull(6, java.sql.Types.INTEGER);
            }

            stmt.setInt(7, admin.getId());
            stmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error updating admin: " + e.getMessage());
        }
    }

    @Override
    public void deleteAdmin(int id) {
        String sql = "DELETE FROM admin WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error deleting admin: " + e.getMessage());
        }
    }

    @Override
    public List<Admin> getAllAdmins() {
        List<Admin> admins = new ArrayList<>();
        String sql = "SELECT * FROM admin";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                admins.add(extractAdminFromResultSet(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error fetching admins: " + e.getMessage());
        }
        return admins;
    }

    //  Helper method
    private Admin extractAdminFromResultSet(ResultSet rs) throws SQLException {
        Restaurant restaurant = null;
        int restaurantId = rs.getInt("restaurant_id");
        if (restaurantId != 0) {
            restaurant = new Restaurant();
            restaurant.setRestaurantId(restaurantId);
        }

        return new Admin(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getInt("age"),
                rs.getString("phone_number"),
                rs.getString("email"),
                rs.getString("password"),
                restaurant
        );
    }
}
