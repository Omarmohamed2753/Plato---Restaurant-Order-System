package javaproject1.DAL.Repo.Implementation;

import javaproject1.DAL.Entity.MenuItem;
import javaproject1.DAL.Repo.abstraction.IMenuItemRepo;

import java.sql.*;

public class MenuItemRepo implements IMenuItemRepo {

    private Connection connection;

    public MenuItemRepo(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void addMenuItem(MenuItem item) {
        String sql = "INSERT INTO MenuItems(name, price, description) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, item.getName());
            stmt.setDouble(2, item.getPrice());
            stmt.setString(3, item.getDescription());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public MenuItem getMenuItemById(int id) {
        String sql = "SELECT * FROM MenuItems WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
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
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void updateMenuItem(MenuItem item) {
        String sql = "UPDATE MenuItems SET name = ?, price = ?, description = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, item.getName());
            stmt.setDouble(2, item.getPrice());
            stmt.setString(3, item.getDescription());
            stmt.setInt(4, item.getItemId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteMenuItem(int id) {
        String sql = "DELETE FROM MenuItems WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
