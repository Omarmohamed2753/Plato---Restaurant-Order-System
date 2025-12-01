package javaproject1.DAL.Repo.Implementation;

import javaproject1.DAL.DataBase.DBConnection;
import javaproject1.DAL.Entity.Menu;
import javaproject1.DAL.Repo.abstraction.IMenuRepo;
import java.sql.*;
public class MenuRepoImpl implements IMenuRepo {
    @Override
    public void addMenu(Menu menu) {
        String sql = "INSERT INTO menus (menu_id) VALUES (?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, menu.getMenuId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Add Menu Error: " + e.getMessage());
        }
    }

    @Override
    public Menu getMenuById(int id) {
        String sql = "SELECT * FROM menus WHERE menu_id = ?";
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
            System.out.println("Get Menu Error: " + e.getMessage());
        }

        return menu;
    }

    @Override
    public void updateMenu(Menu menu) {
        String sql = "UPDATE menus SET menu_id = ? WHERE menu_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, menu.getMenuId());
            stmt.setString(2, menu.getMenuId());
            stmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Update Menu Error: " + e.getMessage());
        }
    }

    @Override
    public void deleteMenu(int id) {
        String sql = "DELETE FROM menus WHERE menu_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Delete Menu Error: " + e.getMessage());
        }
    }
}
