package javaproject1.DAL.Repo.Implementation;

import javaproject1.DAL.DataBase.DBConnection;
import javaproject1.DAL.Entity.Payment;
import javaproject1.DAL.Repo.abstraction.IPaymentRepo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PaymentRepoImpl implements IPaymentRepo {

    @Override
    public void addPayment(Payment payment) {
        String sql = """
            INSERT INTO payments (payment_id, order_id, amount, method, status, payment_date)
            VALUES (?, ?, ?, ?, ?, ?)
            """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, payment.getPaymentId());
            stmt.setInt(2, payment.getOrderId());
            stmt.setDouble(3, payment.getAmount());
            stmt.setString(4, payment.getMethod());
            stmt.setString(5, payment.getStatus());
            stmt.setTimestamp(6, new Timestamp(payment.getPaymentDate().getTime()));

            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Payment getPaymentById(int id) {
        String sql = "SELECT * FROM payments WHERE payment_id = ?";
        Payment payment = null;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                payment = mapToPayment(rs);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return payment;
    }

    @Override
    public void updatePayment(Payment payment) {
        String sql = """
            UPDATE payments 
            SET order_id = ?, amount = ?, method = ?, status = ?, payment_date = ?
            WHERE payment_id = ?
            """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, payment.getOrderId());
            stmt.setDouble(2, payment.getAmount());
            stmt.setString(3, payment.getMethod());
            stmt.setString(4, payment.getStatus());
            stmt.setTimestamp(5, new Timestamp(payment.getPaymentDate().getTime()));
            stmt.setInt(6, payment.getPaymentId());

            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deletePayment(int id) {
        String sql = "DELETE FROM payments WHERE payment_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Payment> getAllPayments() {
        String sql = "SELECT * FROM payments";
        List<Payment> payments = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                payments.add(mapToPayment(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return payments;
    }

    private Payment mapToPayment(ResultSet rs) throws SQLException {
        Payment payment = new Payment();
        payment.setPaymentId(rs.getInt("payment_id"));
        payment.setOrderId(rs.getInt("order_id"));
        payment.setAmount(rs.getDouble("amount"));
        payment.setMethod(rs.getString("method"));
        payment.setStatus(rs.getString("status"));
        payment.setPaymentDate(rs.getTimestamp("payment_date"));
        return payment;
    }
}
