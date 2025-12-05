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
        String sql = "INSERT INTO cart_item (cart_id, menu_item_id, quantity, sub_price) VALUES (?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, cartItem.getCartItemID()); // Temporarily stores cart_id
            stmt.setString(2, cartItem.getMenuItem().getItemId());
            stmt.setInt(3, cartItem.getQuantity());
            stmt.setDouble(4, cartItem.getSubPrice());
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    cartItem.setCartItemID(rs.getInt(1));
                    System.out.println("CartItem added with ID: " + cartItem.getCartItemID());
                }
            }

        } catch (SQLException e) {
            System.err.println("Error adding cart item: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Add cart item with explicit cart_id
     */
    public void addCartItemWithCartId(CartItem cartItem, int cartId) {
        String sql = "INSERT INTO cart_item (cart_id, menu_item_id, quantity, sub_price) VALUES (?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, cartId);
            stmt.setString(2, cartItem.getMenuItem().getItemId());
            stmt.setInt(3, cartItem.getQuantity());
            stmt.setDouble(4, cartItem.getSubPrice());
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    cartItem.setCartItemID(rs.getInt(1));
                    System.out.println("CartItem added with ID: " + cartItem.getCartItemID() + " to cart: " + cartId);
                }
            }

        } catch (SQLException e) {
            System.err.println("Error adding cart item: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public CartItem getCartItemById(int id) {
        String sql = "SELECT ci.*, mi.id as menu_item_id, mi.name, mi.price, mi.description, mi.category, mi.image_path " +
                     "FROM cart_item ci " +
                     "JOIN menu_items mi ON ci.menu_item_id = mi.id " +
                     "WHERE ci.cart_item_id = ?";
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
            e.printStackTrace();
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
            System.out.println("CartItem updated with ID: " + cartItem.getCartItemID());

        } catch (SQLException e) {
            System.err.println("Error updating cart item: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void deleteCartItem(int id) {
        String sql = "DELETE FROM cart_item WHERE cart_item_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();
            System.out.println("CartItem deleted with ID: " + id);

        } catch (SQLException e) {
            System.err.println("Error deleting cart item: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public List<CartItem> getAllCartItems() {
        List<CartItem> items = new ArrayList<>();
        String sql = "SELECT ci.*, mi.id as menu_item_id, mi.name, mi.price, mi.description, mi.category, mi.image_path " +
                     "FROM cart_item ci " +
                     "JOIN menu_items mi ON ci.menu_item_id = mi.id";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                items.add(extractCartItemFromResultSet(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error fetching all cart items: " + e.getMessage());
            e.printStackTrace();
        }
        return items;
    }

    private CartItem extractCartItemFromResultSet(ResultSet rs) throws SQLException {
        CartItem cartItem = new CartItem();
        cartItem.setCartItemID(rs.getInt("cart_item_id"));
        cartItem.setQuantity(rs.getInt("quantity"));
        cartItem.setSubPrice(rs.getDouble("sub_price"));

        MenuItem menuItem = new MenuItem();
        menuItem.setItemId(rs.getString("menu_item_id"));
        menuItem.setName(rs.getString("name"));
        menuItem.setPrice(rs.getDouble("price"));
        menuItem.setDescription(rs.getString("description"));
        menuItem.setCategory(rs.getString("category"));
        menuItem.setImagePath(rs.getString("image_path"));
        cartItem.setMenuItem(menuItem);

        return cartItem;
    }
}
