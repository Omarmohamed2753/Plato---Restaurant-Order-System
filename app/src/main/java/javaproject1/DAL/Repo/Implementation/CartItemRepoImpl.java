package javaproject1.DAL.Repo.Implementation;

import javaproject1.DAL.DataBase.DBConnection;
import javaproject1.DAL.Entity.CartItem;
import javaproject1.DAL.Entity.MenuItem;
import javaproject1.DAL.Repo.abstraction.ICartItemRepo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CartItemRepoImpl implements ICartItemRepo {

    @Override
    public void addCartItem(CartItem cartItem) {
        String sql = "INSERT INTO cart_item (menu_item_id, quantity, sub_price) VALUES (?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, cartItem.getMenuItem().getItemId());
            stmt.setInt(2, cartItem.getQuantity());
            stmt.setDouble(3, cartItem.getSubPrice());
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    cartItem.setCartItemID(rs.getInt(1));
                }
            }

        } catch (SQLException e) {
            System.err.println("Error adding cart item: " + e.getMessage());
        }
    }

    @Override
    public CartItem getCartItemById(int id) {
        String sql = "SELECT * FROM cart_item WHERE cart_item_id = ?";
        CartItem cartItem = null;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    cartItem = extractCartItemFromResultSet(rs);
                }
            }

        } catch (SQLException e) {
            System.err.println("Error fetching cart item by ID: " + e.getMessage());
        }
        return cartItem;
    }

    @Override
    public void updateCartItem(CartItem cartItem) {
        String sql = "UPDATE cart_item SET menu_item_id = ?, quantity = ?, sub_price = ? WHERE cart_item_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, cartItem.getMenuItem().getItemId());
            stmt.setInt(2, cartItem.getQuantity());
            stmt.setDouble(3, cartItem.getSubPrice());
            stmt.setInt(4, cartItem.getCartItemID());

            stmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error updating cart item: " + e.getMessage());
        }
    }

    @Override
    public void deleteCartItem(int id) {
        String sql = "DELETE FROM cart_item WHERE cart_item_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error deleting cart item: " + e.getMessage());
        }
    }

    @Override
    public List<CartItem> getAllCartItems() {
        List<CartItem> items = new ArrayList<>();
        String sql = "SELECT * FROM cart_item";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                items.add(extractCartItemFromResultSet(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error fetching all cart items: " + e.getMessage());
        }
        return items;
    }

    //  Helper method to map ResultSet → CartItem object
    private CartItem extractCartItemFromResultSet(ResultSet rs) throws SQLException {
        CartItem cartItem = new CartItem();
        cartItem.setCartItemID(rs.getInt("cart_item_id"));
        cartItem.setQuantity(rs.getInt("quantity"));
        cartItem.setSubPrice(rs.getDouble("sub_price"));

        MenuItem menuItem = new MenuItem();
        menuItem.setItemId(rs.getString("menu_item_id"));
        cartItem.setMenuItem(menuItem);

        return cartItem;
    }
}
