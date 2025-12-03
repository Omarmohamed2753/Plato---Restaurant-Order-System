package javaproject1.DAL.Repo.Implementation;

import javaproject1.DAL.DataBase.DBConnection;
import javaproject1.DAL.Entity.MenuItem;
import javaproject1.DAL.Repo.abstraction.IMenuItemRepo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MenuItemRepoImpl implements IMenuItemRepo {

    @Override
    public void addMenuItem(MenuItem item) {
        String sql = "INSERT INTO menu_items (name, price, description, category, image_path, menu_id) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, item.getName());
            stmt.setDouble(2, item.getPrice());
            stmt.setString(3, item.getDescription());
            stmt.setString(4, item.getCategory());
            stmt.setString(5, item.getImagePath());
            
            // Link to menu if available
            if (item.getItemId() != null && !item.getItemId().isEmpty()) {
                stmt.setString(6, item.getItemId()); // Temporarily using itemId as menu_id
            } else {
                stmt.setNull(6, Types.VARCHAR);
            }

            stmt.executeUpdate();
            
            // Get the generated ID
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    item.setItemId(rs.getString(1));
                }
            }

            System.out.println("MenuItem added successfully with ID: " + item.getItemId());

        } catch (SQLException e) {
            System.err.println("Error adding menu item: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @Override
    public void addMenuItemWithMenuId(MenuItem item, String menuId) {
        String sql = "INSERT INTO menu_items (name, price, description, category, image_path, menu_id) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, item.getName());
            stmt.setDouble(2, item.getPrice());
            stmt.setString(3, item.getDescription());
            stmt.setString(4, item.getCategory());
            stmt.setString(5, item.getImagePath());
            stmt.setString(6, menuId);

            stmt.executeUpdate();
            
            // Get the generated ID
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    item.setItemId(rs.getString(1));
                }
            }

            System.out.println("MenuItem added successfully with ID: " + item.getItemId() + " to menu: " + menuId);

        } catch (SQLException e) {
            System.err.println("Error adding menu item: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public MenuItem getMenuItemById(int id) {
        String sql = "SELECT * FROM menu_items WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return extractMenuItemFromResultSet(rs);
            }

        } catch (SQLException e) {
            System.err.println("Error fetching menu item: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void updateMenuItem(MenuItem item) {
        String sql = "UPDATE menu_items SET name=?, price=?, description=?, category=?, image_path=? WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, item.getName());
            stmt.setDouble(2, item.getPrice());
            stmt.setString(3, item.getDescription());
            stmt.setString(4, item.getCategory());
            stmt.setString(5, item.getImagePath());
            stmt.setString(6, item.getItemId());

            int rows = stmt.executeUpdate();
            if (rows > 0) {
                System.out.println("MenuItem updated successfully.");
            } else {
                System.out.println("No MenuItem found with ID: " + item.getItemId());
            }

        } catch (SQLException e) {
            System.err.println("Error updating menu item: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void deleteMenuItem(int id) {
        String sql = "DELETE FROM menu_items WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            int rows = stmt.executeUpdate();

            if (rows > 0) {
                System.out.println("MenuItem deleted successfully.");
            } else {
                System.out.println("No MenuItem found with ID: " + id);
            }

        } catch (SQLException e) {
            System.err.println("Error deleting menu item: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public List<MenuItem> getAllMenuItems() {
        List<MenuItem> items = new ArrayList<>();
        String sql = "SELECT * FROM menu_items";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                items.add(extractMenuItemFromResultSet(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error fetching menu items: " + e.getMessage());
            e.printStackTrace();
        }
        return items;
    }
    
    private MenuItem extractMenuItemFromResultSet(ResultSet rs) throws SQLException {
        MenuItem item = new MenuItem();
        item.setItemId(rs.getString("id"));
        item.setName(rs.getString("name"));
        item.setPrice(rs.getDouble("price"));
        item.setDescription(rs.getString("description"));
        item.setCategory(rs.getString("category"));
        item.setImagePath(rs.getString("image_path"));
        return item;
    }
}