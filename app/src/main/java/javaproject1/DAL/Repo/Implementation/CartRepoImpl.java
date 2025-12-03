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
        String sql = "INSERT INTO cart () VALUES ()"; // cart_id auto_increment

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    int cartId = rs.getInt(1);
                    cart.setCartId(cartId);
                }
            }

            // بعد إنشاء الكارت، نحفظ العناصر المرتبطة بيه
            String addItemSQL = "UPDATE cart_item SET cart_id = ? WHERE cart_item_id = ?";
            try (PreparedStatement itemStmt = conn.prepareStatement(addItemSQL)) {
                for (CartItem item : cart.getItems()) {
                    itemStmt.setInt(1, cart.getCartId());
                    itemStmt.setInt(2, item.getCartItemID());
                    itemStmt.addBatch();
                }
                itemStmt.executeBatch();
            }

        } catch (SQLException e) {
            System.err.println("Error adding cart: " + e.getMessage());
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
                    item.setQuantity(rs.getInt("quantity"));
                    item.setSubPrice(rs.getDouble("sub_price"));

                    // Load full MenuItem details
                    MenuItem menuItem = new MenuItem();
                    menuItem.setItemId(rs.getString("menu_item_id"));
                    menuItem.setName(rs.getString("name"));
                    menuItem.setPrice(rs.getDouble("price"));
                    menuItem.setDescription(rs.getString("description"));
                    menuItem.setCategory(rs.getString("category"));
                    menuItem.setImagePath(rs.getString("image_path"));
                    item.setMenuItem(menuItem);

                    items.add(item);
                }
                cart.setItems(items);
            }

        } catch (SQLException e) {
            System.err.println("Error getting cart by ID: " + e.getMessage());
        }

        return cart;
    }

    @Override
    public void updateCart(Cart cart) {
        String sql = "UPDATE cart_item SET quantity = ?, sub_price = ? WHERE cart_item_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            for (CartItem item : cart.getItems()) {
                stmt.setInt(1, item.getQuantity());
                stmt.setDouble(2, item.getSubPrice());
                stmt.setInt(3, item.getCartItemID());
                stmt.addBatch();
            }
            stmt.executeBatch();

        } catch (SQLException e) {
            System.err.println("Error updating cart: " + e.getMessage());
        }
    }

    @Override
    public void deleteCart(int id) {
        String sql = "DELETE FROM cart WHERE cart_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error deleting cart: " + e.getMessage());
        }
    }

    @Override
    public List<Cart> getAllCarts() {
        List<Cart> carts = new ArrayList<>();
        String sql = "SELECT cart_id FROM cart";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                int cartId = rs.getInt("cart_id");
                carts.add(getCartById(cartId)); // استدعاء الميثود اللي بتجيب الكارت بعناصره
            }

        } catch (SQLException e) {
            System.err.println("Error fetching all carts: " + e.getMessage());
        }

        return carts;
    }
}
