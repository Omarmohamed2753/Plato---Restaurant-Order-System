package javaproject1.Model;
import java.util.ArrayList;
import java.util.List;
public class Restaurant {
    private String restaurantId;
    private String name;
    private String address;
    private String phoneNumber;
    private String email;
    private String openingHours;
    private double rating;
    private Menu menu;
    private List<Employee> employees;
    private String imagePath;
    private List<Review> reviews;
    private List<Order> orders;

    public Restaurant(String restaurantId, String name, String address, String phoneNumber, String email, String openingHours, double rating, String imagePath) {
        this.restaurantId = restaurantId;
        this.name = name;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.openingHours = openingHours;
        this.rating = rating;
        this.menu = new Menu();
        this.employees = new ArrayList<>();
        this.imagePath = imagePath;
        this.reviews = new ArrayList<>();
        this.orders = new ArrayList<>();
    }

    public String getRestaurantinfo(){
        return "Restaurant{" +
                "name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", email='" + email + '\'' +
                ", openingHours='" + openingHours + '\'' +
                ", rating=" + rating +
                '}';
    }
    // Menu management
    public Menu getMenu() { return menu; }
    
    public void updateMenu(Menu newMenu) {
        this.menu = newMenu;
        System.out.println("Menu updated for restaurant: " + this.name);
    }

    public void addMenuItem(MenuItem item) {
        this.menu.addItem(item);
        System.out.println("Item " + item.getName() + " added to " + this.name + "'s menu.");
    }

    public void removeMenuItem(MenuItem item) {
        this.menu.removeItem(item);
        System.out.println("Item " + item.getName() + " removed from " + this.name + "'s menu.");
    }
    public void displayMenu() {
        System.out.println("Menu for " + this.name + ":");
        for (MenuItem item : menu.getItems()) {
            System.out.println("- " + item.getName() + ": $" + item.getPrice());
        }
    }
    // Employee management
    public void hireEmployee(Employee employee) {
        this.employees.add(employee);
        employee.setRestaurant(this);
        System.out.println(employee.getName() + " hired at " + this.name + " as " + employee.getRole());
    }

    public void fireEmployee(Employee employee) {
        this.employees.remove(employee);
        if (employee.getRestaurant() == this) employee.setRestaurant(null);
        System.out.println(employee.getName() + " fired from " + this.name);
    }
    public void showEmployees() {
        System.out.println("Employees of " + name + ":");
        for (Employee e : employees) {
            System.out.println("- " + e.getName() + " (" + e.getRole() + ")");
        }
    }
    
    // Reviews management
    public void addReview(Review review) {
        this.reviews.add(review);
    }
    public void removeReview(Review review) {
        this.reviews.remove(review);
    }
    public List<Review> getReviews() {
        return reviews;
    }
    public void displayReviews() {
        System.out.println("Reviews for " + this.name + ":");
        for (Review r : reviews) {
            System.out.println("- " + r.getUser().getName() + ": " + r.getComment() + " (Rating: " + r.getRating() + ")");
        }
    }
    // Orders management
    public void addOrder(Order order) {
        this.orders.add(order);
    }
    public List<Order> getOrders() {
        return orders;
    }
    public void showorders() {
        System.out.println("Orders for " + this.name + ":");
        for (Order o : orders) {
            System.out.println("- Order ID: " + o.getOrderId() + ", Status: " + o.getStatus() 
            + "Delivery person : " + (o.getDelivery() != null && o.getDelivery().getDeliveryPerson() != null ? o.getDelivery().getDeliveryPerson().getName() : "Unassigned"));
        }
    }
    
    // getters/setters for the rest
    public String getRestaurantId() { return restaurantId; }
    public void setRestaurantId(String restaurantId) { this.restaurantId = restaurantId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getOpeningHours() { return openingHours; }
    public void setOpeningHours(String openingHours) { this.openingHours = openingHours; }
    public double getRating() { return rating; }
    public void setRating(double rating) { this.rating = rating; }
    public List<Employee> getEmployees() { return employees; }
    public void setEmployees(List<Employee> employees) { this.employees = employees; }
    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }

    @Override
    public String toString() {
        return "Restaurant{" +
                "restaurantId='" + restaurantId + '\'' +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", email='" + email + '\'' +
                ", openingHours='" + openingHours + '\'' +
                ", rating=" + rating +
                ", menu=" + menu +
                ", employees=" + employees +
                ", imagePath='" + imagePath + '\'' +
                '}';
    }
}
