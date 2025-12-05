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
            
            // Get the generated order_id
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
                order = mapToOrder(rs);
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
        String sql = "SELECT * FROM orders";
        List<Order> orders = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                orders.add(mapToOrder(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error getting all orders: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("DEBUG OrderRepo - Total orders retrieved: " + orders.size());
        return orders;
    }

    private Order mapToOrder(ResultSet rs) throws SQLException {
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

        // Load user
        User user = (userId > 0) ? userRepo.getUserById(userId) : null;

        // Create restaurant reference
        Restaurant restaurant = null;
        if (restaurantId != null) {
            restaurant = new Restaurant();
            restaurant.setRestaurantId(restaurantId);
        }

        // Create address reference
        Address address = null;
        if (addressId != null) {
            address = new Address();
            address.setId(addressId);
        }

        // Create payment reference
        Payment payment = null;
        if (paymentId != null) {
            payment = new Payment();
            payment.setPaymentId(paymentId);
        }

        // Create delivery reference
        Delivery delivery = null;
        if (deliveryId != null) {
            delivery = new Delivery();
            delivery.setDeliveryId(deliveryId);
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

        System.out.println("DEBUG OrderRepo - Mapped order ID: " + orderId + ", Status: " + orderStatus);
        
        return order;
    }
}