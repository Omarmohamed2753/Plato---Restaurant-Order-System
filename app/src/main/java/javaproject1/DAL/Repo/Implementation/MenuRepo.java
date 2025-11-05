package javaproject1.DAL.Repo.Implementation;

import javaproject1.DAL.DataBase.DBConnection;
import javaproject1.DAL.Entity.MenuItem;
import javaproject1.DAL.Repo.abstraction.IMenuItemRepo;

import java.sql.*;

public class MenuRepo implements IMenuItemRepo {

    @Override
    public void addMenuItem(MenuItem item) {
        String sql = "INSERT INTO menu_items(name, price) VALUES(?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, item.getName());
            stmt.setDouble(2, item.getPrice());
            stmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Add Error: " + e.getMessage());
        }
    }

    @Override
    public MenuItem getMenuItemById(int id) {
        String sql = "SELECT * FROM menu_items WHERE id = ?";
        MenuItem item = null;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next())
                item = new MenuItem(rs.getInt("id"), rs.getString("name"), rs.getDouble("price"));

        } catch (SQLException e) {
            System.out.println("Get Error: " + e.getMessage());
        }
        return item;
    }

    @Override
    public void updateMenuItem(MenuItem item) {
        String sql = "UPDATE menu_items SET name = ?, price = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, item.getName());
            stmt.setDouble(2, item.getPrice());
            stmt.setInt(3, item.getItemId());
            stmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Update Error: " + e.getMessage());
        }
    }

    @Override
    public void deleteMenuItem(int id) {
        String sql = "DELETE FROM menu_items WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Delete Error: " + e.getMessage());
        }
    }
}
