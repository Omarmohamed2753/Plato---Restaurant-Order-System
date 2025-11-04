package javaproject1.Model;

import java.util.Date;

/**
 * Represents the payment for an order.
 */
public class Payment {
    
    private String paymentId;
    private double amount;
    private String status; // e.g., "Pending", "Completed", "Failed"
    private String paymentMethod; // e.g., "Credit Card", "COD"
    private Date transactionDate;
    private String orderId; // Link back to the order

    public Payment(String paymentId, double amount, String paymentMethod, String orderId) {
        this.paymentId = paymentId;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.orderId = orderId;
        this.status = "Pending";
    }

    /**
     * Simulates processing the payment.
     * @return true if payment is successful, false otherwise.
     */
    public boolean processPayment() {
        // In a real app, this would connect to a payment gateway
        System.out.println("Processing payment of $" + amount + " via " + paymentMethod);
        
        // Simulate success
        this.status = "Completed";
        this.transactionDate = new Date();
        System.out.println("Payment successful.");
        return true;
    }

    // --- Getters and Setters ---

    public String getPaymentId() {
        return paymentId;
    }

    public double getAmount() {
        return amount;
    }

    public String getStatus() {
        return status;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public Date getTransactionDate() {
        return transactionDate;
    }

    public String getOrderId() {
        return orderId;
    }

    @Override
    public String toString() {
        return "Payment{" +
                "paymentId='" + paymentId + '\'' +
                ", amount=" + amount +
                ", status='" + status + '\'' +
                '}';
    }
}
