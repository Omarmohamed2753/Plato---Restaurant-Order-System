package javaproject1.DAL.Entity;
import java.util.Date;
public class Payment {
    private int paymentId;
    private int orderId;
    private double amount;
    private String method;
    private String status;
    private Date paymentDate;
    public Payment() {}
    public Payment(int paymentId, int orderId, double amount, String method) {
        this.paymentId = paymentId;
        this.orderId = orderId;
        this.amount = amount;
        this.method = method;
        this.status = "Pending";
        this.paymentDate = new Date();
    }
    public int getPaymentId() { return paymentId; }
    public int getOrderId() { return orderId; }
    public double getAmount() { return amount; }
    public String getMethod() { return method; }
    public String getStatus() { return status; }
    public Date getPaymentDate() { return paymentDate; }
    public void setPaymentId(int paymentId) { this.paymentId = paymentId; }
    public void setOrderId(int orderId) { this.orderId = orderId; }
    public void setAmount(double amount) { this.amount = amount; }
    public void setMethod(String method) { this.method = method; }
    public void setStatus(String status) { this.status = status; }
    public void setPaymentDate(Date paymentDate) { this.paymentDate = paymentDate; }
    @Override
    public String toString() {
        return "Payment{" +
                "paymentId=" + paymentId +
                ", orderId=" + orderId +
                ", amount=" + amount +
                ", method='" + method + '\'' +
                ", status='" + status + '\'' +
                ", paymentDate=" + paymentDate +
                '}';
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Payment other = (Payment) obj;
        return paymentId == other.paymentId &&
               orderId == other.orderId &&
               Double.compare(other.amount, amount) == 0 &&
               ((method == null && other.method == null) ||
                (method != null && method.equals(other.method))) &&
               ((status == null && other.status == null) ||
                (status != null && status.equals(other.status))) &&
               ((paymentDate == null && other.paymentDate == null) ||
                (paymentDate != null && paymentDate.equals(other.paymentDate)));
    }
}
