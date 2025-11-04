package javaproject1.Model;

import java.util.ArrayList;
import java.util.List;

/**
 * Renamed from Customer. Now represents the application user (customer).
 * Inherits from Person.
 * Contains new attributes: email, password, addresses, isElite, subscription.
 */
public class User extends Person {

    private String email;
    private String password;
    private List<Address> addresses;
    private boolean isElite;
    private Subscription subscription;
    
    // Kept from old Customer class, as they are essential for a User
    private Cart cart;
    private List<Order> orders;

    public User(int id, String name, int age, String phoneNumber, 
                String email, String password, Address initialAddress) {
        
        // Call parent constructor
        super(id, name, age, phoneNumber); 
        
        this.email = email;
        this.password = password; // In a real app, this would be hashed
        
        // Initialize lists
        this.addresses = new ArrayList<>();
        if (initialAddress != null) {
            this.addresses.add(initialAddress);
        }
        
        this.orders = new ArrayList<>();
        this.cart = new Cart();
        
        this.isElite = false; // Default to false
    }
    
    // --- User-specific methods ---

    public void addAddress(Address address) {
        this.addresses.add(address);
    }

    public void addOrder(Order order) {
        this.orders.add(order);
    }

    // --- Getters and Setters ---

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return ""; // Don't return the actual password
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<Address> getAddresses() {
        return addresses;
    }

    public boolean isElite() {
        return isElite;
    }

    public void setElite(boolean elite) {
        isElite = elite;
    }

    public Subscription getSubscription() {
        return subscription;
    }

    public void setSubscription(Subscription subscription) {
        this.subscription = subscription;
    }

    public Cart getCart() {
        return cart;
    }

    public List<Order> getOrders() {
        return orders;
    }

    // --- Overriding Object methods ---

    @Override
    public String toString() {
        return "User{" +
                "email='" + email + '\'' +
                ", name='" + name + '\'' + // 'name' is from Person
                ", isElite=" + isElite +
                '}';
    }
}