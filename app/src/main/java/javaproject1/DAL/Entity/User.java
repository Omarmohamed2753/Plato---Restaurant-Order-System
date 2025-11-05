package javaproject1.DAL.Entity;

import java.util.ArrayList;
import java.util.List;

public class User extends Person {
    private String email;
    private String password;
    private List<Address> addresses;
    private boolean isElite;
    private Subscription subscription;
    private Cart cart;
    private List<Order> orders;
    public User() {
    }

    public User(int id, String name, int age, String phoneNumber,
                String email, String password, Address initialAddress) {
        super(id, name, age, phoneNumber);
        this.email = email;
        this.password = password;
        this.addresses = new ArrayList<>();
        if (initialAddress != null) this.addresses.add(initialAddress);
        this.isElite = false;
        this.subscription = null;
        this.cart = new Cart();
        this.orders = new ArrayList<>();
    }

    // // Person account creation
    // public boolean createAccount(List<User> allUsers) {
    //     for (User u : allUsers) {
    //         if (u.getEmail().equalsIgnoreCase(this.email)) {
    //             System.out.println("Email already exists!");
    //             return false;
    //         }
    //     }
    //     allUsers.add(this);
    //     System.out.println("Account created successfully for: " + this.name);
    //     return true;
    // }
    // // Authentication
    // public boolean login(String email, String password) {
    //     return this.email != null && this.email.equals(email) && this.password.equals(password);
    // }

    // public void updateProfile(String name, int age, String phoneNumber, String email, String password, boolean isElite, Subscription subscription) {
    //     this.setName(name);
    //     this.setAge(age);
    //     this.setPhoneNumber(phoneNumber);
    //     this.email = email;
    //     this.password = password;
    //     this.isElite = isElite;
    //     this.subscription = subscription;
    // }

    // public List<MenuItem> browseMenu(Restaurant restaurant) {
    //     if (restaurant == null || restaurant.getMenu() == null) {
    //         System.out.println("No menu available for this restaurant.");
    //         return new ArrayList<>();
    //     }
    //     System.out.println("Menu for " + restaurant.getName() + ":");
    //     for (MenuItem item : restaurant.getMenu().getItems()) {
    //         System.out.println("- " + item.getName() + " : " + item.getPrice());
    //     }
    //     return restaurant.getMenu().getItems();
    // }
    
    // public void subscribeElite(Subscription subscription) {
    //     this.isElite = true;
    //     this.subscription = subscription;
    // }

    // public void addAddress(Address address) {
    //     this.addresses.add(address);
    // }

    // public void removeAddress(Address address) {
    //     this.addresses.remove(address);
    // }

    // getters/setters
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public List<Address> getAddresses() { return addresses; }
    public void setAddresses(List<Address> addresses) { this.addresses = addresses; }
    public boolean isElite() { return isElite; }
    public void setElite(boolean elite) { isElite = elite; }
    public Subscription getSubscription() { return subscription; }
    public void setSubscription(Subscription subscription) { this.subscription = subscription; }
    public Cart getCart() { return cart; }
    public void setCart(Cart cart) { this.cart = cart; }
    public List<Order> getOrders() { return orders; }
    public void setOrders(List<Order> orders) { this.orders = orders; }

    @Override
    public String toString() {
        return "User{" +
                "email='" + email + '\'' +
                ", name='" + name + '\'' +
                ", isElite=" + isElite +
                '}';
    }
}
