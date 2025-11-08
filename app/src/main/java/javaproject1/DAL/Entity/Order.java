package javaproject1.DAL.Entity;
import java.util.Date;
import java.util.List;
import javaproject1.DAL.Enums.*;
public class Order {
    private int orderId;
    private User user;
    private Restaurant restaurant;
    private List<CartItem> items;
    private double totalAmount;
    private OrderStatus status;
    private Date orderDate;
    private Address deliveryAddress;
    private Payment payment;
    private Delivery delivery;
    public Order() {}
    public Order(int orderId, User user, Restaurant restaurant, List<CartItem> items, Address deliveryAddress) {
        this.orderId = orderId;
        this.user = user;
        this.restaurant = restaurant;
        this.items = items;
        this.deliveryAddress = deliveryAddress;
        this.orderDate = new Date();
        this.status = OrderStatus.PENDING;
    }
    public int getOrderId() { return orderId; }
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
    public void setOrderId(int orderId) {this.orderId = orderId;}
    public void setUser(User user) {this.user = user;}
    public void setRestaurant(Restaurant restaurant) {this.restaurant = restaurant;}
    public void setItems(List<CartItem> items) {this.items = items;}
    public void setTotalAmount(double totalAmount) {this.totalAmount = totalAmount;}
    public void setStatus(OrderStatus status) {this.status = status;}
    public void setOrderDate(Date orderDate) {this.orderDate = orderDate;}
    public void setDeliveryAddress(Address deliveryAddress) {this.deliveryAddress = deliveryAddress;}
    
    @Override
    public String toString() {
        return "Order{" +
                "orderId='" + orderId + '\'' +
                ", totalAmount=" + totalAmount +
                ", status=" + status +
                '}';
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Order other = (Order) obj;
        return orderId == other.orderId &&
               Double.compare(other.totalAmount, totalAmount) == 0 &&
               status == other.status &&
               (user != null ? user.equals(other.user) : other.user == null) &&
               (restaurant != null ? restaurant.equals(other.restaurant) : other.restaurant == null) &&
               (items != null ? items.equals(other.items) : other.items == null) &&
               (orderDate != null ? orderDate.equals(other.orderDate) : other.orderDate == null) &&
               (deliveryAddress != null ? deliveryAddress.equals(other.deliveryAddress) : other.deliveryAddress == null) &&
               (payment != null ? payment.equals(other.payment) : other.payment == null) &&
               (delivery != null ? delivery.equals(other.delivery) : other.delivery == null);
    }
}
