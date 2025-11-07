package javaproject1.DAL.Repo.Implementation;

import javaproject1.DAL.Entity.Delivery;
import javaproject1.DAL.Entity.Employee;
import javaproject1.DAL.Repo.abstraction.IDeliveryRepo;
import javaproject1.DAL.DataBase.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DeliveryRepoImpl implements IDeliveryRepo {

    @Override
    public void addDelivery(Delivery delivery) {
        String sql = "INSERT INTO delivery (delivery_id, delivery_person_id, status, estimated_delivery_time) VALUES (?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, delivery.getDeliveryId());
            if (delivery.getDeliveryPerson() != null) {
                stmt.setInt(2, delivery.getDeliveryPerson().getId());
            } else {
                stmt.setNull(2, Types.INTEGER);
            }
            stmt.setString(3, delivery.getStatus());
            if (delivery.getEstimatedDeliveryTime() != null) {
                stmt.setTimestamp(4, new Timestamp(delivery.getEstimatedDeliveryTime().getTime()));
            } else {
                stmt.setNull(4, Types.TIMESTAMP);
            }

            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Delivery getDeliveryById(int id) {
        String sql = "SELECT * FROM delivery WHERE delivery_id = ?";
        Delivery delivery = null;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                delivery = mapToDelivery(rs);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return delivery;
    }

    @Override
    public void updateDelivery(Delivery delivery) {
        String sql = "UPDATE delivery SET delivery_person_id = ?, status = ?, estimated_delivery_time = ? WHERE delivery_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            if (delivery.getDeliveryPerson() != null) {
                stmt.setInt(1, delivery.getDeliveryPerson().getId());
            } else {
                stmt.setNull(1, Types.INTEGER);
            }
            stmt.setString(2, delivery.getStatus());
            if (delivery.getEstimatedDeliveryTime() != null) {
                stmt.setTimestamp(3, new Timestamp(delivery.getEstimatedDeliveryTime().getTime()));
            } else {
                stmt.setNull(3, Types.TIMESTAMP);
            }
            stmt.setInt(4, delivery.getDeliveryId());

            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteDelivery(int id) {
        String sql = "DELETE FROM delivery WHERE delivery_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Delivery> getAllDeliveries() {
        String sql = "SELECT * FROM delivery";
        List<Delivery> deliveries = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                deliveries.add(mapToDelivery(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return deliveries;
    }
    // Helper to map ResultSet to Delivery object
    private Delivery mapToDelivery(ResultSet rs) throws SQLException {
        int deliveryId = rs.getInt("delivery_id");
        int deliveryPersonId = rs.getInt("delivery_person_id");
        String status = rs.getString("status");
        Timestamp estimatedTime = rs.getTimestamp("estimated_delivery_time");

        Employee employee = null;
        if (deliveryPersonId > 0) {
            employee = new Employee();
            employee.setId(deliveryPersonId);
        }

        Date estimatedDate = (estimatedTime != null) ? new Date(estimatedTime.getTime()) : null;

        return new Delivery(deliveryId, employee, status, estimatedDate);
    }
}
