package javaproject1.Model;

import java.util.ArrayList;
import java.util.List;
import javaproject1.Enums.*;

/**
 * Represents the restaurant business.
 * Demonstrates "has-a" relationship (Composition/Aggregation - Lecture 4)
 * with Menu and Employee.
 */
public class Restaurant {
    
    private String restaurantId;
    private String name;
    private Address address;
    private String phoneNumber;
    private double rating;
    
    // Composition: Restaurant "owns" its Menu
    private Menu menu;
    
    // Aggregation: Restaurant "has" Employees
    private List<Employee> employees;

    public Restaurant(String restaurantId, String name, Address address, String phoneNumber) {
        this.restaurantId = restaurantId;
        this.name = name;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.rating = 0.0; // Initial rating
        
        // Initialize composite/aggregate objects
        this.menu = new Menu();
        this.employees = new ArrayList<>();
    }

    // --- Methods to manage composite/aggregate objects ---

    public void addMenuItem(MenuItem item) {
        this.menu.addItem(item);
        System.out.println("Item " + item.getName() + " added to " + this.name + "'s menu.");
    }

    public void removeMenuItem(MenuItem item) {
        this.menu.removeItem(item);
        System.out.println("Item " + item.getName() + " removed from " + this.name + "'s menu.");
    }

    public void hireEmployee(Employee employee) {
        this.employees.add(employee);
        employee.setRestaurant(this); // Set the back-reference
        System.out.println(employee.getName() + " hired at " + this.name + " as " + employee.getRole());
    }

    public void manageOrder(Order order, OrderStatus newStatus) {
        System.out.println("Restaurant " + name + " updating order " + order.getOrderId() + " to " + newStatus);
        order.updateStatus(newStatus);
    }
    
    // --- Getters and Setters ---

    public String getRestaurantId() {
        return restaurantId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public Menu getMenu() {
        return menu;
    }

    public List<Employee> getEmployees() {
        return employees;
    }

    @Override
    public String toString() {
        return "Restaurant{" +
                "name='" + name + '\'' +
                ", address=" + address.getCity() +
                ", rating=" + rating +
                '}';
    }
}
