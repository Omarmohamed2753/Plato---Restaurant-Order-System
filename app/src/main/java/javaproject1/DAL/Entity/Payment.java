package javaproject1.DAL.Entity;
import java.util.Date;

import javaproject1.DAL.Enums.PaymentM;

public class Payment {
    private String paymentId;
    private double amount;
    private String status;
    private PaymentM paymentMethod; 
    private Date transactionDate;
    private String orderId;

    public Payment(String paymentId, double amount, PaymentM paymentMethod, String orderId) {
        this.paymentId = paymentId;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.orderId = orderId;
        this.status = "Pending";
    }

    public Payment() {}

    // getters/setters
    public String getPaymentId() { return paymentId; }
    public double getAmount() { return amount; }
    public String getStatus() { return status; }
    public PaymentM getPaymentMethod() { return paymentMethod; } // Changed return type
    public Date getTransactionDate() { return transactionDate; }
    public String getOrderId() { return orderId; }
    public void setPaymentId(String paymentId) { this.paymentId = paymentId; }
    public void setAmount(double amount) { this.amount = amount; }
    public void setStatus(String status) { this.status = status; }
    public void setPaymentMethod(PaymentM paymentMethod) { this.paymentMethod = paymentMethod; }
    public void setTransactionDate(Date transactionDate) { this.transactionDate = transactionDate; }
    public void setOrderId(String orderId) { this.orderId = orderId; }

    @Override
    public String toString() {
        return "Payment{" +
                "paymentId='" + paymentId + '\'' +
                ", amount=" + amount +
                ", status='" + status + '\'' +
                '}';
    }
}