package javaproject1.DAL.Repo.Implementation;

import javaproject1.DAL.DataBase.DBConnection;
import javaproject1.DAL.Entity.*;
import javaproject1.DAL.Repo.abstraction.IUserRepo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserRepoImpl implements IUserRepo {
    public UserRepoImpl() {
    }
    @Override
    public void addUser(User user) {
        String sql = "INSERT INTO users (name, age, phone, email, password, is_elite) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, user.getName());
            stmt.setInt(2, user.getAge());
            stmt.setString(3, user.getPhoneNumber());
            stmt.setString(4, user.getEmail());
            stmt.setString(5, user.getPassword());
            stmt.setBoolean(6, user.isElite());
            stmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Add User Error: " + e.getMessage());
        }
    }

    @Override
    public User getUserById(int id) {
        String sql = "SELECT * FROM users WHERE user_id = ?";
        User user = null;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                user = mapToUser(rs, conn);
            }

        } catch (SQLException e) {
            System.out.println("Get User Error: " + e.getMessage());
        }
        return user;
    }

    @Override
    public void updateUser(User user) {
        String sql = "UPDATE users SET name=?, age=?, phone=?, email=?, password=?, is_elite=? WHERE user_id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, user.getName());
            stmt.setInt(2, user.getAge());
            stmt.setString(3, user.getPhoneNumber());
            stmt.setString(4, user.getEmail());
            stmt.setString(5, user.getPassword());
            stmt.setBoolean(6, user.isElite());
            stmt.setString(7, user.getId());
            stmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Update User Error: " + e.getMessage());
        }
    }

    @Override
    public void deleteUser(int id) {
        String sql = "DELETE FROM users WHERE user_id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Delete User Error: " + e.getMessage());
        }
    }

    @Override
    public List<User> getAllUsers() {
        String sql = "SELECT * FROM users";
        List<User> users = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                users.add(mapToUser(rs, conn));
            }

        } catch (SQLException e) {
            System.out.println("Get All Users Error: " + e.getMessage());
        }

        return users;
    }

    @Override
    public User getUserByEmail(String email) {
        String sql = "SELECT * FROM users WHERE email = ?";
        User user = null;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                user = mapToUser(rs, conn);
            }

        } catch (SQLException e) {
            System.out.println("Get User By Email Error: " + e.getMessage());
        }
        return user;
    }

    private User mapToUser(ResultSet rs, Connection conn) throws SQLException {
        User user = new User();
        int userId = rs.getInt("user_id");
        user.setId(String.valueOf(userId));
        user.setName(rs.getString("name"));
        user.setAge(rs.getInt("age"));
        user.setPhoneNumber(rs.getString("phone"));
        user.setEmail(rs.getString("email"));
        user.setPassword(rs.getString("password"));
        user.setElite(rs.getBoolean("is_elite"));

        user.setAddresses(loadAddresses(conn, userId));
        user.setOrders(loadOrders(conn, userId));
        user.setSubscription(loadSubscription(conn, userId));
        user.setCart(loadCart(conn, userId));

        return user;
    }
    
    private List<Order> loadOrders(Connection conn, int userId) throws SQLException {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT * FROM orders WHERE user_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Order order = new Order();
                    order.setOrderId(rs.getString("order_id"));
                    order.setTotalAmount(rs.getDouble("total_amount"));
                    String statusStr = rs.getString("status");
                    if (statusStr != null) {
                        order.setStatus(javaproject1.DAL.Enums.OrderStatus.valueOf(statusStr.toUpperCase()));
                    }
                    Timestamp orderDate = rs.getTimestamp("order_date");
                    if (orderDate != null) {
                        order.setOrderDate(new java.util.Date(orderDate.getTime()));
                    }
                    orders.add(order);
                }
            }
        }
        return orders;
    }
    
    private Cart loadCart(Connection conn, int userId) throws SQLException {
        // First, get the cart_id for this user
        String cartSql = "SELECT cart_id FROM cart WHERE user_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(cartSql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int cartId = rs.getInt("cart_id");
                    return loadCartItems(conn, cartId);
                }
            }
        }
        return new Cart();
    }
    
    private Cart loadCartItems(Connection conn, int cartId) throws SQLException {
        Cart cart = new Cart();
        cart.setCartId(cartId);
        String sql = "SELECT ci.*, mi.id as menu_item_id, mi.name, mi.price, mi.description, mi.category, mi.image_path " +
                     "FROM cart_item ci " +
                     "JOIN menu_items mi ON ci.menu_item_id = mi.id " +
                     "WHERE ci.cart_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, cartId);
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
        }
        return cart;
    }
    
    private List<Address> loadAddresses(Connection conn, int userId) throws SQLException {
        List<Address> addresses = new ArrayList<>();
        String sql = "SELECT a.* FROM address a " +
                     "INNER JOIN user_addresses ua ON a.id = ua.address_id " +
                     "WHERE ua.user_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Address address = new Address();
                    address.setId(rs.getString("id"));
                    address.setStreet(rs.getString("street"));
                    address.setCity(rs.getString("city"));
                    address.setBuildingNumber(rs.getInt("building_number"));
                    addresses.add(address);
                }
            }
        }
        return addresses;
    }
    
    private Subscription loadSubscription(Connection conn, int userId) throws SQLException {
        String sql = "SELECT * FROM subscriptions WHERE user_id = ? AND active = TRUE ORDER BY subscription_id DESC LIMIT 1";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    java.sql.Date startDateSql = rs.getDate("start_date");
                    java.sql.Date endDateSql = rs.getDate("end_date");
                    
                    java.util.Date startDate = (startDateSql != null) ? new java.util.Date(startDateSql.getTime()) : null;
                    java.util.Date endDate = (endDateSql != null) ? new java.util.Date(endDateSql.getTime()) : null;
                    
                    Subscription subscription = new Subscription(startDate, endDate);
                    subscription.setId(rs.getInt("subscription_id"));
                    subscription.setActive(rs.getBoolean("active"));
                    return subscription;
                }
            }
        }
        return null;
    }
}
