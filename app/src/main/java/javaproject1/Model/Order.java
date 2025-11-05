package javaproject1.Model;

import java.util.Date;
import java.util.List;
import javaproject1.Enums.*;

public class Order {
    private String orderId;
    private User user;
    private Restaurant restaurant;
    private List<CartItem> items;
    private double totalAmount;
    private OrderStatus status;
    private Date orderDate;
    private Address deliveryAddress;
    private Payment payment;
    private Delivery delivery;

    public Order(String orderId, User user, Restaurant restaurant, List<CartItem> items, Address deliveryAddress) {
        this.orderId = orderId;
        this.user = user;
        this.restaurant = restaurant;
        this.items = items;
        this.deliveryAddress = deliveryAddress;
        this.orderDate = new Date();
        this.status = OrderStatus.PENDING;
        this.totalAmount = calculateTotal(user.getSubscription());

        // register this order with restaurant and user
        if (restaurant != null) restaurant.addOrder(this);
        if (user != null) user.getOrders().add(this);
    }

    public double calculateTotal(Subscription subscription) {
        if (items == null || items.isEmpty()) {
            System.out.println("No items in order!");
            return 0.0;
        }

        double subtotal = 0.0;
        for (CartItem item : items) {
            subtotal += item.getSubPrice();
        }

        double tax = subtotal * 0.1;    // Fixed 10% tax
        double deliveryFee = 30.0;      //  Fixed delivery fee
        double discount = 0.0;          //   virtual discount

        //  If there's an active subscription, apply discount
        if (subscription != null && subscription.isActive()) {
            discount = subtotal * subscription.getDiscountRate();
            System.out.println("Subscription discount applied: " + (subscription.getDiscountRate() * 100) + "%");
        }

        double total = subtotal + tax + deliveryFee - discount;

        this.totalAmount = total;       
        System.out.println("Subtotal: " + subtotal +
                " | Tax: " + tax +
                " | Delivery: " + deliveryFee +
                " | Discount: " + discount +
                " | Total: " + total);

        return total;
}


    public void updateStatus(OrderStatus status) {
        this.status = status;
        System.out.println("Order " + orderId + " status updated to " + status);
    }

    // getters/setters
    public String getOrderId() { return orderId; }
    public User getUser() { return user; }
    public Restaurant getRestaurant() { return restaurant; }
    public List<CartItem> getItems() { return items; }
    public double getTotalAmount() { return totalAmount; }
    public OrderStatus getStatus() { return status; }
    public Date getOrderDate() { return orderDate; }
    public Address getDeliveryAddress() { return deliveryAddress; }
    public Payment getPayment() { return payment; }
    public void setPayment(Payment payment) { this.payment = payment; }
    public Delivery getDelivery() { return delivery; }
    public void setDelivery(Delivery delivery) { this.delivery = delivery; }

    @Override
    public String toString() {
        return "Order{" +
                "orderId='" + orderId + '\'' +
                ", totalAmount=" + totalAmount +
                ", status=" + status +
                '}';
    }
}
