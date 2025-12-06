package javaproject1.DAL.Repo.Implementation;

import javaproject1.DAL.DataBase.DBConnection;
import javaproject1.DAL.Entity.*;
import javaproject1.DAL.Enums.OrderStatus;
import javaproject1.DAL.Repo.abstraction.IOrderRepo;
import javaproject1.DAL.Repo.abstraction.IUserRepo;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class OrderRepoImpl implements IOrderRepo {

    private final IUserRepo userRepo;

    public OrderRepoImpl(IUserRepo userRepo) {
        this.userRepo = userRepo;
    }

    @Override
    public void addOrder(Order order) {
        String sql = """
            INSERT INTO orders 
            (user_id, restaurant_id, total_amount, status, order_date, address_id, payment_id, delivery_id)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
            """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            if (order.getUser() != null)
                stmt.setInt(1, Integer.parseInt(order.getUser().getId()));
            else stmt.setNull(1, Types.INTEGER);

            if (order.getRestaurant() != null)
                stmt.setInt(2, Integer.parseInt(order.getRestaurant().getRestaurantId()));
            else stmt.setNull(2, Types.INTEGER);

            stmt.setDouble(3, order.getTotalAmount());
            stmt.setString(4, order.getStatus().name());
            stmt.setTimestamp(5, new Timestamp(order.getOrderDate().getTime()));

            if (order.getDeliveryAddress() != null)
                stmt.setInt(6, Integer.parseInt(order.getDeliveryAddress().getId()));
            else stmt.setNull(6, Types.INTEGER);

            if (order.getPayment() != null)
                stmt.setString(7, order.getPayment().getPaymentId());
            else stmt.setNull(7, Types.VARCHAR);

            if (order.getDelivery() != null)
                stmt.setString(8, order.getDelivery().getDeliveryId());
            else stmt.setNull(8, Types.VARCHAR);

            stmt.executeUpdate();
            
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    int generatedId = rs.getInt(1);
                    order.setOrderId(String.valueOf(generatedId));
                    System.out.println("DEBUG OrderRepo - Order added with ID: " + generatedId);
                }
            }

        } catch (SQLException e) {
            System.err.println("Error adding order: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public Order getOrderById(int id) {
        String sql = "SELECT * FROM orders WHERE order_id = ?";
        Order order = null;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                order = mapToOrder(rs, conn);
            }

        } catch (SQLException e) {
            System.err.println("Error getting order: " + e.getMessage());
            e.printStackTrace();
        }

        return order;
    }

    @Override
    public void updateOrder(Order order) {
        String sql = """
            UPDATE orders SET 
            user_id = ?, restaurant_id = ?, total_amount = ?, status = ?, order_date = ?, 
            address_id = ?, payment_id = ?, delivery_id = ?
            WHERE order_id = ?
            """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            if (order.getUser() != null)
                stmt.setString(1, order.getUser().getId());
            else stmt.setNull(1, Types.INTEGER);

            if (order.getRestaurant() != null)
                stmt.setString(2, order.getRestaurant().getRestaurantId());
            else stmt.setNull(2, Types.INTEGER);

            stmt.setDouble(3, order.getTotalAmount());
            stmt.setString(4, order.getStatus().name());
            stmt.setTimestamp(5, new Timestamp(order.getOrderDate().getTime()));

            if (order.getDeliveryAddress() != null)
                stmt.setString(6, order.getDeliveryAddress().getId());
            else stmt.setNull(6, Types.INTEGER);

            if (order.getPayment() != null)
                stmt.setString(7, order.getPayment().getPaymentId());
            else stmt.setNull(7, Types.INTEGER);

            if (order.getDelivery() != null)
                stmt.setString(8, order.getDelivery().getDeliveryId());    
            else stmt.setNull(8, Types.INTEGER);

            stmt.setString(9, order.getOrderId());

            int rows = stmt.executeUpdate();
            if (rows > 0) {
                System.out.println("DEBUG OrderRepo - Order updated: " + order.getOrderId());
            }

        } catch (SQLException e) {
            System.err.println("Error updating order: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void deleteOrder(int id) {
        String sql = "DELETE FROM orders WHERE order_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            int rows = stmt.executeUpdate();
            
            if (rows > 0) {
                System.out.println("DEBUG OrderRepo - Order deleted: " + id);
            }

        } catch (SQLException e) {
            System.err.println("Error deleting order: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public List<Order> getAllOrders() {
        String sql = "SELECT * FROM orders ORDER BY order_id DESC";
        List<Order> orders = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            // CRITICAL FIX: Process each order separately with its own result set
            while (rs.next()) {
                try {
                    Order order = mapToOrder(rs, conn);
                    if (order != null) {
                        orders.add(order);
                        System.out.println("DEBUG OrderRepo - Loaded order #" + order.getOrderId() + 
                            " with " + (order.getItems() != null ? order.getItems().size() : 0) + " items");
                    }
                } catch (Exception e) {
                    System.err.println("Error mapping order: " + e.getMessage());
                    e.printStackTrace();
                    // Continue processing other orders even if one fails
                }
            }

        } catch (SQLException e) {
            System.err.println("Error getting all orders: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("DEBUG OrderRepo - Total orders retrieved: " + orders.size());
        return orders;
    }

    // In OrderRepoImpl.java, replace the mapToOrder method with this fixed version:

private Order mapToOrder(ResultSet rs, Connection conn) throws SQLException {
    int orderId = rs.getInt("order_id");
    int userId = rs.getInt("user_id");
    int restaurantIdInt = rs.getInt("restaurant_id");
    String restaurantId = rs.wasNull() ? null : String.valueOf(restaurantIdInt);
    double totalAmount = rs.getDouble("total_amount");
    String status = rs.getString("status");
    Timestamp orderDate = rs.getTimestamp("order_date");
    int addressIdInt = rs.getInt("address_id");
    String addressId = rs.wasNull() ? null : String.valueOf(addressIdInt);
    String paymentId = rs.getString("payment_id");
    String deliveryId = rs.getString("delivery_id");

    System.out.println("\n=== MAPPING ORDER #" + orderId + " ===");

    // Load user with full details
    User user = (userId > 0) ? userRepo.getUserById(userId) : null;
    System.out.println("User: " + (user != null ? user.getName() : "N/A"));

    // Load restaurant with full details
    Restaurant restaurant = null;
    if (restaurantId != null) {
        RestaurantRepoImpl restaurantRepo = new RestaurantRepoImpl();
        restaurant = restaurantRepo.getRestaurantById(restaurantIdInt);
        System.out.println("Restaurant: " + (restaurant != null ? restaurant.getName() : "N/A"));
    }

    // Load address with full details
    Address address = null;
    if (addressId != null) {
        AddressRepoImpl addressRepo = new AddressRepoImpl();
        address = addressRepo.getAddressById(addressIdInt);
        System.out.println("Address: " + (address != null ? address.toString() : "N/A"));
    }

    // Load payment with full details
    Payment payment = null;
    if (paymentId != null) {
        PaymentRepoImpl paymentRepo = new PaymentRepoImpl();
        try {
            payment = paymentRepo.getPaymentById(Integer.parseInt(paymentId));
        } catch (NumberFormatException e) {
            System.err.println("Invalid payment ID: " + paymentId);
        }
    }

    // CRITICAL FIX: Load delivery with FULL employee details from database
    Delivery delivery = null;
    if (deliveryId != null && !deliveryId.trim().isEmpty()) {
        System.out.println("Loading delivery: " + deliveryId);
        
        // Load delivery from database with employee information
        String deliverySql = "SELECT d.*, e.id as emp_id, e.name as emp_name, e.phone_number, e.role " +
                            "FROM delivery d " +
                            "LEFT JOIN employees e ON d.delivery_person_id = e.id " +
                            "WHERE d.delivery_id = ?";
        
        try (PreparedStatement deliveryStmt = conn.prepareStatement(deliverySql)) {
            deliveryStmt.setString(1, deliveryId);
            
            try (ResultSet deliveryRs = deliveryStmt.executeQuery()) {
                if (deliveryRs.next()) {
                    delivery = new Delivery();
                    delivery.setDeliveryId(deliveryRs.getString("delivery_id"));
                    delivery.setStatus(deliveryRs.getString("status"));
                    
                    Timestamp estimatedTime = deliveryRs.getTimestamp("estimated_delivery_time");
                    if (estimatedTime != null) {
                        delivery.setEstimatedDeliveryTime(new Date(estimatedTime.getTime()));
                    }
                    
                    // CRITICAL: Load employee details if exists
                    int empId = deliveryRs.getInt("emp_id");
                    if (!deliveryRs.wasNull() && empId > 0) {
                        Employee deliveryPerson = new Employee();
                        deliveryPerson.setId(String.valueOf(empId));
                        deliveryPerson.setName(deliveryRs.getString("emp_name"));
                        deliveryPerson.setPhoneNumber(deliveryRs.getString("phone_number"));
                        deliveryPerson.setRole(deliveryRs.getString("role"));
                        
                        delivery.setDeliveryPerson(deliveryPerson);
                        
                        System.out.println("✓ Delivery person loaded: " + deliveryPerson.getName() + 
                                         " (ID: " + empId + ", Phone: " + deliveryPerson.getPhoneNumber() + ")");
                    } else {
                        System.out.println("⚠ No delivery person assigned yet");
                    }
                } else {
                    System.err.println("✗ Delivery record not found in database: " + deliveryId);
                }
            }
        } catch (SQLException e) {
            System.err.println("✗ Error loading delivery: " + e.getMessage());
            e.printStackTrace();
        }
    } else {
        System.out.println("No delivery ID for this order");
    }

    // Parse order status
    OrderStatus orderStatus = OrderStatus.PENDING;
    if (status != null) {
        try {
            orderStatus = OrderStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            System.err.println("Invalid order status: " + status);
        }
    }
    
    Date date = (orderDate != null) ? new Date(orderDate.getTime()) : null;

    // Create order
    Order order = new Order();
    order.setOrderId(String.valueOf(orderId));
    order.setUser(user);
    order.setRestaurant(restaurant);
    order.setTotalAmount(totalAmount);
    order.setStatus(orderStatus);
    order.setOrderDate(date);
    order.setDeliveryAddress(address);
    order.setPayment(payment);
    order.setDelivery(delivery);

    // Load cart items for THIS specific order
    List<CartItem> items = loadOrderItemsForOrder(orderId);
    order.setItems(items);

    System.out.println("✓ Order mapped with " + items.size() + " items");
    System.out.println("=== END MAPPING ORDER #" + orderId + " ===\n");
    
    return order;
}

    /**
     * CRITICAL FIX: Load cart items for a specific order using a separate connection
     * This prevents issues with nested result sets and ensures each order gets its own items
     */
    private List<CartItem> loadOrderItemsForOrder(int orderId) {
        List<CartItem> items = new ArrayList<>();
        
        String sql = """
            SELECT ci.cart_item_id, ci.menu_item_id, ci.quantity, ci.sub_price,
                   mi.id, mi.name, mi.price, mi.description, mi.category, mi.image_path
            FROM order_items oi
            JOIN cart_item ci ON oi.cart_item_id = ci.cart_item_id
            JOIN menu_items mi ON ci.menu_item_id = mi.id
            WHERE oi.order_id = ?
            ORDER BY oi.cart_item_id
            """;
        
        // Use a separate connection for loading items to avoid nested ResultSet issues
        try (Connection itemConn = DBConnection.getConnection();
             PreparedStatement stmt = itemConn.prepareStatement(sql)) {
            
            stmt.setInt(1, orderId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    // Create MenuItem
                    MenuItem menuItem = new MenuItem();
                    menuItem.setItemId(rs.getString("id"));
                    menuItem.setName(rs.getString("name"));
                    menuItem.setPrice(rs.getDouble("price"));
                    menuItem.setDescription(rs.getString("description"));
                    menuItem.setCategory(rs.getString("category"));
                    menuItem.setImagePath(rs.getString("image_path"));
                    
                    // Create CartItem
                    CartItem cartItem = new CartItem();
                    cartItem.setCartItemID(rs.getInt("cart_item_id"));
                    cartItem.setMenuItem(menuItem);
                    cartItem.setQuantity(rs.getInt("quantity"));
                    cartItem.setSubPrice(rs.getDouble("sub_price"));
                    
                    items.add(cartItem);
                    
                    System.out.println("  ✓ Loaded: " + menuItem.getName() + " x" + cartItem.getQuantity() + 
                        " = $" + String.format("%.2f", cartItem.getSubPrice()));
                }
            }
            
        } catch (SQLException e) {
            System.err.println("ERROR loading items for order #" + orderId + ": " + e.getMessage());
            e.printStackTrace();
        }
        
        return items;
    }
}
