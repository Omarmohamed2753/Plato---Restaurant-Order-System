package javaproject1.DAL.Repo.Implementation;

import javaproject1.DAL.DataBase.DBConnection;
import javaproject1.DAL.Entity.Subscription;
import javaproject1.DAL.Repo.abstraction.ISubscriptionRepo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SubscriptionRepoImpl implements ISubscriptionRepo {

    @Override
    public void addSubscription(Subscription subscription) {
        // Note: user_id is optional - can be set later via updateSubscription
        String sql = "INSERT INTO subscriptions (start_date, end_date, active, user_id) VALUES (?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setDate(1, new Date(subscription.getStartDate().getTime()));
            stmt.setDate(2, new Date(subscription.getEndDate().getTime()));
            stmt.setBoolean(3, subscription.getActive());
            // user_id is set to NULL if not provided - can be updated later
            stmt.setNull(4, Types.INTEGER);
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    subscription.setId(rs.getInt(1));
                }
            }

        } catch (SQLException e) {
            System.err.println("Error adding subscription: " + e.getMessage());
        }
    }

    @Override
    public Subscription getSubscriptionById(int id) {
        String sql = "SELECT * FROM subscriptions WHERE subscription_id = ?";
        Subscription subscription = null;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    subscription = extractSubscriptionFromResultSet(rs);
                }
            }

        } catch (SQLException e) {
            System.err.println("Error fetching subscription by ID: " + e.getMessage());
        }
        return subscription;
    }

    @Override
    public void updateSubscription(Subscription subscription) {
        String sql = "UPDATE subscriptions SET start_date = ?, end_date = ?, active = ? WHERE subscription_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, new Date(subscription.getStartDate().getTime()));
            stmt.setDate(2, new Date(subscription.getEndDate().getTime()));
            stmt.setBoolean(3, subscription.getActive());
            stmt.setInt(4, subscription.getId());

            stmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error updating subscription: " + e.getMessage());
        }
    }

    @Override
    public void deleteSubscription(int id) {
        String sql = "DELETE FROM subscriptions WHERE subscription_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error deleting subscription: " + e.getMessage());
        }
    }

    @Override
    public List<Subscription> getAllSubscriptions() {
        List<Subscription> subscriptions = new ArrayList<>();
        String sql = "SELECT * FROM subscriptions";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                subscriptions.add(extractSubscriptionFromResultSet(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error fetching subscriptions: " + e.getMessage());
        }
        return subscriptions;
    }

    // Helper method to convert ResultSet to Subscription object
    private Subscription extractSubscriptionFromResultSet(ResultSet rs) throws SQLException {
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

