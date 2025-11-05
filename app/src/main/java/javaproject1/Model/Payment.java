package javaproject1.Model;

import java.util.Date;

public class Payment {
    private String paymentId;
    private double amount;
    private String status;
    private String paymentMethod;
    private Date transactionDate;
    private String orderId;

    public Payment(String paymentId, double amount, String paymentMethod, String orderId) {
        this.paymentId = paymentId;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.orderId = orderId;
        this.status = "Pending";
    }

    public boolean processPayment() {
        if (this.amount <= 0) {
            System.out.println("Invalid payment amount!");
            this.status = "Failed";
            return false;
        }
        System.out.println("Processing payment of $" + amount + " via " + paymentMethod);
        this.status = "Completed";
        this.transactionDate = new Date();
        System.out.println("Payment successful.");
        return true;
    }

    // getters/setters
    public String getPaymentId() { return paymentId; }
    public double getAmount() { return amount; }
    public String getStatus() { return status; }
    public String getPaymentMethod() { return paymentMethod; }
    public Date getTransactionDate() { return transactionDate; }
    public String getOrderId() { return orderId; }

    @Override
    public String toString() {
        return "Payment{" +
                "paymentId='" + paymentId + '\'' +
                ", amount=" + amount +
                ", status='" + status + '\'' +
                '}';
    }
}
