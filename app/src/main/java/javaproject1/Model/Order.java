package javaproject1.Model;

import java.util.Date;
import java.util.List;
import javaproject1.Enums.*;    ;

/**
 * Represents a completed order.
 * This class links all other entities together.
 */
public class Order {
    
    private String orderId;
    private List<CartItem> items;
    private double totalAmount;
    private OrderStatus status;
    private Date orderDate;
    private Address deliveryAddress;
    
    // Composition: Order "has a" Payment and Delivery
    private Payment payment;
    private Delivery delivery;

    public Order(String orderId, User user, Restaurant restaurant, List<CartItem> items, Address deliveryAddress) {
        this.orderId = orderId;
        this.items = items;
        this.deliveryAddress = deliveryAddress;
        
        // Set defaults
        this.orderDate = new Date(); // Set to current time
        this.status = OrderStatus.PENDING;
        this.totalAmount = calculateTotal();
    }

    /**
     * Calculates the total amount for the order.
     */
    public double calculateTotal() {
        double total = 0.0;
        for (CartItem item : items) {
            total += item.getSubPrice();
        }
        // Could add taxes or delivery fees here
        return total;
    }

    public void updateStatus(OrderStatus status) {
        this.status = status;
        System.out.println("Order " + orderId + " status updated to " + status);
    }

    // --- Getters and Setters ---

    public String getOrderId() {
        return orderId;
    }

    public List<CartItem> getItems() {
        return items;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public Address getDeliveryAddress() {
        return deliveryAddress;
    }

    public Payment getPayment() {
        return payment;
    }

    public void setPayment(Payment payment) {
        this.payment = payment;
    }

    public Delivery getDelivery() {
        return delivery;
    }

    public void setDelivery(Delivery delivery) {
        this.delivery = delivery;
    }

    @Override
    public String toString() {
        return "Order{" +
                "orderId='" + orderId + '\'' +
                ", totalAmount=" + totalAmount +
                ", status=" + status +
                '}';
    }
}
