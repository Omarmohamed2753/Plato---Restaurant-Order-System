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
        String sql = "INSERT INTO menu_items (name, price, description) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, item.getName());
            stmt.setDouble(2, item.getPrice());
            stmt.setString(3, item.getDescription());
            stmt.executeUpdate();

            System.out.println("MenuItem added successfully.");

        } catch (SQLException e) {
            System.out.println("Error adding menu item: " + e.getMessage());
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
                MenuItem item = new MenuItem();
                item.setItemId(rs.getInt("id"));
                item.setName(rs.getString("name"));
                item.setPrice(rs.getDouble("price"));
                item.setDescription(rs.getString("description"));
                return item;
            }

        } catch (SQLException e) {
            System.out.println("Error fetching menu item: " + e.getMessage());
        }
        return null;
    }

    @Override
    public void updateMenuItem(MenuItem item) {
        String sql = "UPDATE menu_items SET name=?, price=?, description=? WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, item.getName());
            stmt.setDouble(2, item.getPrice());
            stmt.setString(3, item.getDescription());
            stmt.setInt(4, item.getItemId());

            int rows = stmt.executeUpdate();
            if (rows > 0)
                System.out.println("MenuItem updated successfully.");
            else
                System.out.println("No MenuItem found with ID: " + item.getItemId());

        } catch (SQLException e) {
            System.out.println("Error updating menu item: " + e.getMessage());
        }
    }

    @Override
    public void deleteMenuItem(int id) {
        String sql = "DELETE FROM menu_items WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            int rows = stmt.executeUpdate();

            if (rows > 0)
                System.out.println("MenuItem deleted successfully.");
            else
                System.out.println("No MenuItem found with ID: " + id);

        } catch (SQLException e) {
            System.out.println("Error deleting menu item: " + e.getMessage());
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
                MenuItem item = new MenuItem();
                item.setItemId(rs.getInt("id"));
                item.setName(rs.getString("name"));
                item.setPrice(rs.getDouble("price"));
                item.setDescription(rs.getString("description"));
                items.add(item);
            }

        } catch (SQLException e) {
            System.out.println("Error fetching menu items: " + e.getMessage());
        }
        return items;
    }
}
