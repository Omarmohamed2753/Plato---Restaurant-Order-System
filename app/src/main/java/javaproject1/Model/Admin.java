package javaproject1.Model;

import java.util.List;

import javaproject1.Enums.OrderStatus;

public class Admin extends Person {
    private String email;
    private String password;
    private Restaurant restaurant; // Admin manages one restaurant

    public Admin(int id, String name, int age, String phoneNumber, String email, String password, Restaurant restaurant) {
        super(id, name, age, phoneNumber);
        this.email = email;
        this.password = password;
        this.restaurant = restaurant;
    }

    // Admin account creation
    public boolean createAdmin(List<Admin> allAdmins) {
        if (!allAdmins.isEmpty()) {
            System.out.println("An Admin already exists! Only one admin allowed.");
            return false;
        }

        allAdmins.add(this);
        System.out.println("Admin account created successfully for: " + this.name);
        return true;
    }

    // Authentication
    public boolean loginAdmin(String username, String password) {
        return this.email != null && this.email.equals(username) && this.password.equals(password);
    }

    // User management
    public void manageUser(List<User> users, int userId) {
        for (User u : users) {
            if (u.getId() == userId) {
                System.out.println("Managing user: " + u.getName());
                
                if (u.getSubscription() == null) {
                    System.out.println("User has no subscription to manage!");
                    return;
                }
                u.getSubscription().setActive(!u.getSubscription().isActive());
                System.out.println("User " + (u.getSubscription().isActive() ? "activated" : "deactivated"));
                return;
            }
        }
        System.out.println("User not found!");
    }
    // Order management
    public void manageOrder(Order order, Delivery delivery) {
    if (restaurant == null || !restaurant.getOrders().contains(order)) {
        System.out.println("Order not found in this restaurant!");
        return;
    }

    switch (order.getStatus()) {
            case PENDING:
                order.updateStatus(OrderStatus.CONFIRMED);
                System.out.println("Order #" + order.getOrderId() + " has been CONFIRMED.");
                break;

            case CONFIRMED:
                order.updateStatus(OrderStatus.PREPARING);
                System.out.println("Order #" + order.getOrderId() + " is now PREPARING.");
                break;

            case PREPARING:
                order.updateStatus(OrderStatus.PREPARING);
                System.out.println("Order #" + order.getOrderId() + " is READY FOR DELIVERY.");
                break;

            case READY_FOR_DELIVERY:
                if (delivery != null && delivery.getDeliveryPerson() != null) {
                    delivery.assignDeliveryPerson(delivery.getDeliveryPerson(), order);
                    order.updateStatus(OrderStatus.OUT_FOR_DELIVERY);
                    System.out.println("Order #" + order.getOrderId() + " is now OUT FOR DELIVERY.");
                } else {
                    System.out.println("No delivery person assigned for order #" + order.getOrderId());
                }
                break;

            case OUT_FOR_DELIVERY:
                order.updateStatus(OrderStatus.DELIVERED);
                System.out.println("Order #" + order.getOrderId() + " has been DELIVERED.");
                break;

            case CANCELLED:
                System.out.println("Order #" + order.getOrderId() + " has been CANCELLED.");
                break;

            case DELIVERED:
                System.out.println("Order #" + order.getOrderId() + " is already DELIVERED.");
                break;

            default:
                System.out.println("Unknown order status for order #" + order.getOrderId());
        }
    }

    // Employee management
    public void addEmployee(Employee employee) {
        if (restaurant != null) restaurant.hireEmployee(employee);
    }
    public void removeEmployee(Employee employee) {
        if (restaurant != null) restaurant.fireEmployee(employee);
    }
    // Menu management
    public void addMenuItem(MenuItem item, Menu menu) {
        restaurant.addMenuItem(item);
    }
    public void removeMenuItem(MenuItem item, Menu menu) {
        restaurant.removeMenuItem(item);
    }
    public void manageMenu(Menu menu) {
        if (restaurant != null) {
            restaurant.updateMenu(menu);
        }
    }
    // View restaurant details
    public void showEmployees() {
        if (restaurant != null) restaurant.showEmployees();
    }
    public void showReviews() {
        if (restaurant != null) restaurant.displayReviews();
    }
    public void showMenu() {
        if (restaurant != null) restaurant.displayMenu();
    }
    public void displayOrders() {
        if (restaurant != null) restaurant.showorders();
    }
    // getters/setters
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public Restaurant getRestaurant() { return restaurant; }
    public void setRestaurant(Restaurant restaurant) { this.restaurant = restaurant; }

    @Override
    public String toString() {
        return "Admin{" +
                "name='" + name + '\'' +
                ", restaurant=" + (restaurant != null ? restaurant.getName() : "None") +
                '}';
    }
}
