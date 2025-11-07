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
            (order_id, user_id, restaurant_id, total_amount, status, order_date, address_id, payment_id, delivery_id)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, order.getOrderId());

            if (order.getUser() != null)
                stmt.setInt(2, order.getUser().getId());
            else stmt.setNull(2, Types.INTEGER);

            if (order.getRestaurant() != null)
                stmt.setInt(3, order.getRestaurant().getRestaurantId());
            else stmt.setNull(3, Types.INTEGER);

            stmt.setDouble(4, order.getTotalAmount());
            stmt.setString(5, order.getStatus().name());
            stmt.setTimestamp(6, new Timestamp(order.getOrderDate().getTime()));

            if (order.getDeliveryAddress() != null)
                stmt.setInt(7, order.getDeliveryAddress().getId());
            else stmt.setNull(7, Types.INTEGER);

            if (order.getPayment() != null)
                stmt.setInt(8, order.getPayment().getPaymentId());
            else stmt.setNull(8, Types.INTEGER);

            if (order.getDelivery() != null)
                stmt.setInt(9, order.getDelivery().getDeliveryId());
            else stmt.setNull(9, Types.INTEGER);

            stmt.executeUpdate();

        } catch (SQLException e) {
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
                stmt.setInt(1, order.getUser().getId());
            else stmt.setNull(1, Types.INTEGER);

            if (order.getRestaurant() != null)
                stmt.setInt(2, order.getRestaurant().getRestaurantId());
            else stmt.setNull(2, Types.INTEGER);

            stmt.setDouble(3, order.getTotalAmount());
            stmt.setString(4, order.getStatus().name());
            stmt.setTimestamp(5, new Timestamp(order.getOrderDate().getTime()));

            if (order.getDeliveryAddress() != null)
                stmt.setInt(6, order.getDeliveryAddress().getId());
            else stmt.setNull(6, Types.INTEGER);

            if (order.getPayment() != null)
                stmt.setInt(7, order.getPayment().getPaymentId());
            else stmt.setNull(7, Types.INTEGER);

            if (order.getDelivery() != null)
                stmt.setInt(8, order.getDelivery().getDeliveryId());
            else stmt.setNull(8, Types.INTEGER);

            stmt.setInt(9, order.getOrderId());

            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteOrder(int id) {
        String sql = "DELETE FROM orders WHERE order_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();

        } catch (SQLException e) {
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
            e.printStackTrace();
        }

        return orders;
    }

    private Order mapToOrder(ResultSet rs) throws SQLException {
        int orderId = rs.getInt("order_id");
        int userId = rs.getInt("user_id");
        int restaurantId = rs.getInt("restaurant_id");
        double totalAmount = rs.getDouble("total_amount");
        String status = rs.getString("status");
        Timestamp orderDate = rs.getTimestamp("order_date");
        int addressId = rs.getInt("address_id");
        int paymentId = rs.getInt("payment_id");
        int deliveryId = rs.getInt("delivery_id");

        User user = (userId > 0) ? userRepo.getUserById(userId) : null;

        Restaurant restaurant = new Restaurant();
        restaurant.setRestaurantId(restaurantId);

        Address address = new Address();
        address.setId(addressId);

        Payment payment = new Payment();
        payment.setPaymentId(paymentId);

        Delivery delivery = new Delivery();
        delivery.setDeliveryId(deliveryId);

        OrderStatus orderStatus = OrderStatus.valueOf(status);
        Date date = (orderDate != null) ? new Date(orderDate.getTime()) : null;

        Order order = new Order();
        order.setOrderId(orderId);
        order.setUser(user);
        order.setRestaurant(restaurant);
        order.setTotalAmount(totalAmount);
        order.setStatus(orderStatus);
        order.setOrderDate(date);
        order.setDeliveryAddress(address);
        order.setPayment(payment);
        order.setDelivery(delivery);

        return order;
    }
}
