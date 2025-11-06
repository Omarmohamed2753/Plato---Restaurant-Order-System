package javaproject1.DAL.Repo.Implementation;

import javaproject1.DAL.Repo.abstraction.IOrderRepo;
import javaproject1.DAL.Repo.abstraction.IUserRepo;
import javaproject1.DAL.Entity.Order;
import javaproject1.DAL.Enums.OrderStatus;

import java.sql.*;
import java.util.List;

public class OrderRepo implements IOrderRepo {

    private Connection connection;
    private IUserRepo userRepo;

    public OrderRepo(Connection connection, IUserRepo userRepo) {
        this.connection = connection;
        this.userRepo = userRepo;
    }

    @Override
    public void addOrder(Order order) {
        String sql = "INSERT INTO Orders(orderId, userId, totalAmount, status, orderDate) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, order.getOrderId());
            stmt.setInt(2, order.getUser().getId());
            stmt.setDouble(3, order.getTotalAmount());
            stmt.setString(4, order.getStatus().name()); // Enum → String
            stmt.setTimestamp(5, new Timestamp(order.getOrderDate().getTime()));
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Order getOrderById(String id) {
        String sql = "SELECT * FROM Orders WHERE orderId = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Order order = new Order();
                
                order.setOrderId(rs.getString("orderId"));
                order.setTotalAmount(rs.getDouble("totalAmount"));
                order.setStatus(OrderStatus.valueOf(rs.getString("status")));
                order.setOrderDate(rs.getTimestamp("orderDate"));

                int userId = rs.getInt("userId");
                order.setUser(userRepo.getUserById(userId));

                return order;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void updateOrder(Order order) {
        String sql = "UPDATE Orders SET userId = ?, totalAmount = ?, status = ?, orderDate = ? WHERE orderId = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, order.getUser().getId());
            stmt.setDouble(2, order.getTotalAmount());
            stmt.setString(3, order.getStatus().name());
            stmt.setTimestamp(4, new Timestamp(order.getOrderDate().getTime()));
            stmt.setString(5, order.getOrderId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteOrder(String id) {
        String sql = "DELETE FROM Orders WHERE orderId = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Order> getAllOrders() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getAllOrders'");
    }
}
