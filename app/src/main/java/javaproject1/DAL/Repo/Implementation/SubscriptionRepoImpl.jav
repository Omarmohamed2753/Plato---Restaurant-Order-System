package javaproject1.DAL.Repo.Implementation;

import javaproject1.DAL.DataBase.DBConnection;
import javaproject1.DAL.Entity.Subscription;
import javaproject1.DAL.Repo.abstraction.ISubscriptionRepo;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SubscriptionRepoImpl implements ISubscriptionRepo {

    @Override
    public void addSubscription(Subscription subscription) {
        String sql = "INSERT INTO subscriptions (start_date, end_date, active) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, new java.sql.Date(subscription.getStartDate().getTime()));
            stmt.setDate(2, new java.sql.Date(subscription.getEndDate().getTime()));
            stmt.setBoolean(3, subscription.getActive());
            stmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Add Subscription Error: " + e.getMessage());
        }
    }

    @Override
    public Subscription getSubscriptionById(int id) {
        String sql = "SELECT * FROM subscriptions WHERE subscription_id = ?";
        Subscription subscription = null;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                subscription = mapToSubscription(rs);
            }

        } catch (SQLException e) {
            System.out.println("Get Subscription Error: " + e.getMessage());
        }

        return subscription;
    }

    @Override
    public void updateSubscription(Subscription subscription) {
        String sql = "UPDATE subscriptions SET start_date=?, end_date=?, active=? WHERE subscription_id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, new java.sql.Date(subscription.getStartDate().getTime()));
            stmt.setDate(2, new java.sql.Date(subscription.getEndDate().getTime()));
            stmt.setBoolean(3, subscription.getActive());
            stmt.setInt(4, getSubscriptionIdByDates(subscription.getStartDate(), subscription.getEndDate())); // temporary key
            stmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Update Subscription Error: " + e.getMessage());
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
            System.out.println("Delete Subscription Error: " + e.getMessage());
        }
    }

    @Override
    public List<Subscription> getAllSubscriptions() {
        String sql = "SELECT * FROM subscriptions";
        List<Subscription> subscriptions = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                subscriptions.add(mapToSubscription(rs));
            }

        } catch (SQLException e) {
            System.out.println("Get All Subscriptions Error: " + e.getMessage());
        }

        return subscriptions;
    }

    private Subscription mapToSubscription(ResultSet rs) throws SQLException {
        Date startDate = rs.getDate("start_date");
        Date endDate = rs.getDate("end_date");
        boolean active = rs.getBoolean("active");

        Subscription sub = new Subscription(startDate, endDate);
        sub.setActive(active);

        return sub;
    }

    private int getSubscriptionIdByDates(Date start, Date end) {
        String sql = "SELECT subscription_id FROM subscriptions WHERE start_date = ? AND end_date = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, new java.sql.Date(start.getTime()));
            stmt.setDate(2, new java.sql.Date(end.getTime()));
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("subscription_id");
            }

        } catch (SQLException e) {
            System.out.println("Get Subscription ID Error: " + e.getMessage());
        }
        return -1;
    }
}
