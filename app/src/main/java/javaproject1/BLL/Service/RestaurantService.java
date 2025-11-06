package javaproject1.BLL.Service;

import javaproject1.DAL.Entity.*;


public class RestaurantService {
    private final Restaurant restaurant;

    public RestaurantService(Restaurant restaurant) {
        this.restaurant = restaurant;
    }

    public Menu getMenu() {
        return restaurant.getMenu();
    }

    public void updateMenu(Menu newMenu) {
        restaurant.setMenu(newMenu);
        System.out.println("Menu updated for restaurant: " + restaurant.getName());
    }
    public void displayMenu() {
        System.out.println("Menu for " + restaurant.getName() + ":");
        for (MenuItem item : restaurant.getMenu().getItems()) {
            System.out.println("- " + item.getName() + ": $" + item.getPrice());
        }
    }

    public void hireEmployee(Employee employee) {
        restaurant.getEmployees().add(employee);
        employee.setRestaurant(restaurant);
        System.out.println(employee.getName() + " hired at " + restaurant.getName() + " as " + employee.getRole());
    }

    public void fireEmployee(Employee employee) {
        restaurant.getEmployees().remove(employee);
        if (employee.getRestaurant() == restaurant) employee.setRestaurant(null);
        System.out.println(employee.getName() + " fired from " + restaurant.getName());
    }

    public void showEmployees() {
        System.out.println("Employees of " + restaurant.getName() + ":");
        for (Employee e : restaurant.getEmployees()) {
            System.out.println("- " + e.getName() + " (" + e.getRole() + ")");
        }
    }

    public void addReview(Review review) {
        restaurant.getReviews().add(review);
    }

    public void removeReview(Review review) {
        restaurant.getReviews().remove(review);
    }

    public void displayReviews() {
        System.out.println("Reviews for " + restaurant.getName() + ":");
        for (Review r : restaurant.getReviews()) {
            System.out.println("- " + r.getUser().getName() + ": " + r.getComment() + " (Rating: " + r.getRating() + ")");
        }
    }

    public void addOrder(Order order) {
        restaurant.getOrders().add(order);
    }

    public void showOrders() {
        System.out.println("Orders for " + restaurant.getName() + ":");
        for (Order o : restaurant.getOrders()) {
            System.out.println("- Order ID: " + o.getOrderId() + ", Status: " + o.getStatus() +
                    ", Delivery person: " + (o.getDelivery() != null && o.getDelivery().getDeliveryPerson() != null
                    ? o.getDelivery().getDeliveryPerson().getName() : "Unassigned"));
        }
    }
}