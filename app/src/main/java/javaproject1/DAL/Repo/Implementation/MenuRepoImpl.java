package javaproject1.DAL.Repo.Implementation;

import javaproject1.DAL.DataBase.DBConnection;
import javaproject1.DAL.Entity.Menu;
import javaproject1.DAL.Repo.abstraction.IMenuRepo;
import java.sql.*;

public class MenuRepoImpl implements IMenuRepo {
    
    @Override
    public void addMenu(Menu menu) {
        String sql = "INSERT INTO menu (menu_id) VALUES (?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, menu.getMenuId());
            stmt.executeUpdate();
            System.out.println("Menu added successfully with ID: " + menu.getMenuId());
        } catch (SQLException e) {
            System.err.println("Add Menu Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public Menu getMenuById(int id) {
        String sql = "SELECT * FROM menu WHERE menu_id = ?";
        Menu menu = null;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                menu = new Menu();
                menu.setMenuId(rs.getString("menu_id"));
            }

        } catch (SQLException e) {
            System.err.println("Get Menu Error: " + e.getMessage());
            e.printStackTrace();
        }

        return menu;
    }

    @Override
    public void updateMenu(Menu menu) {
        String sql = "UPDATE menu SET menu_id = ? WHERE menu_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, menu.getMenuId());
            stmt.setString(2, menu.getMenuId());
            stmt.executeUpdate();
            System.out.println("Menu updated successfully");

        } catch (SQLException e) {
            System.err.println("Update Menu Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void deleteMenu(int id) {
        String sql = "DELETE FROM menu WHERE menu_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();
            System.out.println("Menu deleted successfully");

        } catch (SQLException e) {
            System.err.println("Delete Menu Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}