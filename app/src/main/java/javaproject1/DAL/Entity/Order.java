package javaproject1.DAL.Entity;

import java.util.Date;
import java.util.List;
import javaproject1.DAL.Enums.*;

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

    public Order() {
    }
    public Order(String orderId, User user, Restaurant restaurant, List<CartItem> items, Address deliveryAddress) {
        this.orderId = orderId;
        this.user = user;
        this.restaurant = restaurant;
        this.items = items;
        this.deliveryAddress = deliveryAddress;
        this.orderDate = new Date();
        this.status = OrderStatus.PENDING;
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


    public void setOrderId(String orderId) {
    this.orderId = orderId;
}

public void setUser(User user) {
    this.user = user;
}

public void setRestaurant(Restaurant restaurant) {
    this.restaurant = restaurant;
}

public void setItems(List<CartItem> items) {
    this.items = items;
}

public void setTotalAmount(double totalAmount) {
    this.totalAmount = totalAmount;
}

public void setStatus(OrderStatus status) {
    this.status = status;
}

public void setOrderDate(Date orderDate) {
    this.orderDate = orderDate;
}

public void setDeliveryAddress(Address deliveryAddress) {
    this.deliveryAddress = deliveryAddress;
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
