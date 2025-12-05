package javaproject1.DAL.Repo.Implementation;

import javaproject1.DAL.DataBase.DBConnection;
import javaproject1.DAL.Entity.Cart;
import javaproject1.DAL.Entity.CartItem;
import javaproject1.DAL.Entity.MenuItem;
import javaproject1.DAL.Repo.abstraction.ICartRepo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CartRepoImpl implements ICartRepo {

    @Override
    public void addCart(Cart cart) {
        // First, check if cart already exists for this user
        String checkSql = "SELECT cart_id FROM cart WHERE user_id = ?";
        String insertSql = "INSERT INTO cart (user_id) VALUES (?)";
        
        try (Connection conn = DBConnection.getConnection()) {
            // Check if cart exists
            try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                checkStmt.setInt(1, cart.getCartId()); // Assuming cartId is temporarily storing userId
                ResultSet rs = checkStmt.executeQuery();
                
                if (rs.next()) {
                    // Cart already exists, just set the ID
                    cart.setCartId(rs.getInt("cart_id"));
                    System.out.println("Cart already exists with ID: " + cart.getCartId());
                    return;
                }
            }
            
            // Create new cart
            try (PreparedStatement stmt = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setInt(1, cart.getCartId()); // userId
                stmt.executeUpdate();

                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        int cartId = rs.getInt(1);
                        cart.setCartId(cartId);
                        System.out.println("New cart created with ID: " + cartId);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error adding cart: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public Cart getCartById(int id) {
        Cart cart = new Cart();
        cart.setCartId(id);
        
        String sql = "SELECT ci.*, mi.id as menu_item_id, mi.name, mi.price, mi.description, mi.category, mi.image_path " +
                     "FROM cart_item ci " +
                     "JOIN menu_items mi ON ci.menu_item_id = mi.id " +
                     "WHERE ci.cart_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                List<CartItem> items = new ArrayList<>();
                while (rs.next()) {
                    CartItem item = new CartItem();
                    item.setCartItemID(rs.getInt("cart_item_id"));
                    
                    MenuItem menuItem = new MenuItem();
                    menuItem.setItemId(rs.getString("menu_item_id"));
                    menuItem.setName(rs.getString("name"));
                    menuItem.setPrice(rs.getDouble("price"));
                    menuItem.setDescription(rs.getString("description"));
                    menuItem.setCategory(rs.getString("category"));
                    menuItem.setImagePath(rs.getString("image_path"));
                    item.setMenuItem(menuItem);
                    
                    item.setQuantity(rs.getInt("quantity"));
                    item.setSubPrice(rs.getDouble("sub_price"));

                    items.add(item);
                }
                cart.setItems(items);
            }

        } catch (SQLException e) {
            System.err.println("Error getting cart by ID: " + e.getMessage());
            e.printStackTrace();
        }

        return cart;
    }

    @Override
    public void updateCart(Cart cart) {
        // Update existing items
        String updateSql = "UPDATE cart_item SET quantity = ?, sub_price = ? WHERE cart_item_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(updateSql)) {

            for (CartItem item : cart.getItems()) {
                if (item.getCartItemID() > 0) { // Only update existing items
                    stmt.setInt(1, item.getQuantity());
                    stmt.setDouble(2, item.getSubPrice());
                    stmt.setInt(3, item.getCartItemID());
                    stmt.addBatch();
                }
            }
            stmt.executeBatch();
            System.out.println("Cart updated with ID: " + cart.getCartId());

        } catch (SQLException e) {
            System.err.println("Error updating cart: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void deleteCart(int id) {
        String deleteItemsSql = "DELETE FROM cart_item WHERE cart_id = ?";
        String deleteCartSql = "DELETE FROM cart WHERE cart_id = ?";

        try (Connection conn = DBConnection.getConnection()) {
            // First delete all items
            try (PreparedStatement stmt = conn.prepareStatement(deleteItemsSql)) {
                stmt.setInt(1, id);
                stmt.executeUpdate();
            }
            
            // Then delete cart
            try (PreparedStatement stmt = conn.prepareStatement(deleteCartSql)) {
                stmt.setInt(1, id);
                stmt.executeUpdate();
            }
            
            System.out.println("Cart deleted with ID: " + id);

        } catch (SQLException e) {
            System.err.println("Error deleting cart: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public List<Cart> getAllCarts() {
        List<Cart> carts = new ArrayList<>();
        String sql = "SELECT DISTINCT cart_id FROM cart";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                int cartId = rs.getInt("cart_id");
                carts.add(getCartById(cartId));
            }

        } catch (SQLException e) {
            System.err.println("Error fetching all carts: " + e.getMessage());
            e.printStackTrace();
        }

        return carts;
    }
    
    /**
     * Get or create cart for a user
     */
    public Cart getOrCreateCartForUser(int userId) {
        String checkSql = "SELECT cart_id FROM cart WHERE user_id = ?";
        String insertSql = "INSERT INTO cart (user_id) VALUES (?)";
        
        try (Connection conn = DBConnection.getConnection()) {
            // Check if cart exists
            try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                checkStmt.setInt(1, userId);
                ResultSet rs = checkStmt.executeQuery();
                
                if (rs.next()) {
                    int cartId = rs.getInt("cart_id");
                    return getCartById(cartId);
                }
            }
            
            // Create new cart
            try (PreparedStatement stmt = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setInt(1, userId);
                stmt.executeUpdate();

                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        int cartId = rs.getInt(1);
                        Cart cart = new Cart();
                        cart.setCartId(cartId);
                        return cart;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting/creating cart for user: " + e.getMessage());
            e.printStackTrace();
        }
        
        return new Cart(); // Return empty cart as fallback
    }
}
