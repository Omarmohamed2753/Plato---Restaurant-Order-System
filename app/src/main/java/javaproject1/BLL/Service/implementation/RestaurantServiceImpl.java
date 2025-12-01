package javaproject1.BLL.Service.implementation;

import javaproject1.BLL.Service.abstraction.RestaurantServiceAbs;
import javaproject1.DAL.Repo.abstraction.IRestaurantRepo;
import javaproject1.DAL.Repo.Implementation.RestaurantRepoImpl;
import javaproject1.DAL.Entity.*;

import java.util.List;

public class RestaurantServiceImpl implements RestaurantServiceAbs {

    private final IRestaurantRepo restaurantRepo;

    public RestaurantServiceImpl() {
        this(new RestaurantRepoImpl());
    }

    public RestaurantServiceImpl(IRestaurantRepo restaurantRepo) {
        this.restaurantRepo = restaurantRepo;
    }

    // CRUD
    @Override
    public void addRestaurant(Restaurant restaurant) {
        restaurantRepo.addRestaurant(restaurant);
    }

    @Override
    public Restaurant getRestaurantById(int id) {
        return restaurantRepo.getRestaurantById(id);
    }

    @Override
    public void updateRestaurant(Restaurant restaurant) {
        restaurantRepo.updateRestaurant(restaurant);
    }

    @Override
    public void deleteRestaurant(int id) {
        restaurantRepo.deleteRestaurant(id);
    }

    

    // Menu management
    @Override
    public Menu getMenu(Restaurant restaurant) {
        return restaurant.getMenu();
    }

    @Override
    public void updateMenu(Restaurant restaurant, Menu newMenu) {
        restaurant.setMenu(newMenu);
        restaurantRepo.updateRestaurant(restaurant);
        System.out.println("Menu updated for restaurant: " + restaurant.getName());
    }

    @Override
    public void addMenuItem(Restaurant restaurant, MenuItem item) {
        if (restaurant.getMenu() == null) {
            restaurant.setMenu(new Menu());
        }
        if (restaurant.getMenu().getItems() == null) {
            restaurant.getMenu().setItems(new java.util.ArrayList<>());
        }
        if (!restaurant.getMenu().getItems().contains(item)) {
            restaurant.getMenu().getItems().add(item);
        }
        restaurantRepo.updateRestaurant(restaurant);
        System.out.println("Item " + item.getName() + " added to " + restaurant.getName() + "'s menu.");
    }

    @Override
    public void removeMenuItem(Restaurant restaurant, MenuItem item) {
        if (restaurant.getMenu() != null && restaurant.getMenu().getItems() != null) {
            restaurant.getMenu().getItems().remove(item);
        }
        restaurantRepo.updateRestaurant(restaurant);
        System.out.println("Item " + item.getName() + " removed from " + restaurant.getName() + "'s menu.");
    }

    @Override
    public void displayMenu(Restaurant restaurant) {
        System.out.println("Menu for " + restaurant.getName() + ":");
        if (restaurant.getMenu() != null && restaurant.getMenu().getItems() != null) {
            for (MenuItem item : restaurant.getMenu().getItems()) {
                System.out.println("- " + item.getName() + ": $" + item.getPrice());
            }
        } else {
            System.out.println("No menu items available for this restaurant.");
        }
    }

    // Employee management
    @Override
    public void hireEmployee(Restaurant restaurant, Employee employee) {
        restaurant.getEmployees().add(employee);
        employee.setRestaurant(restaurant);
        restaurantRepo.updateRestaurant(restaurant);
        System.out.println(employee.getName() + " hired at " + restaurant.getName() + " as " + employee.getRole());
    }

    @Override
    public void fireEmployee(Restaurant restaurant, Employee employee) {
        restaurant.getEmployees().remove(employee);
        if (employee.getRestaurant() == restaurant) employee.setRestaurant(null);
        restaurantRepo.updateRestaurant(restaurant);
        System.out.println(employee.getName() + " fired from " + restaurant.getName());
    }

    @Override
    public void showEmployees(Restaurant restaurant) {
        System.out.println("Employees of " + restaurant.getName() + ":");
        for (Employee e : restaurant.getEmployees()) {
            System.out.println("- " + e.getName() + " (" + e.getRole() + ")");
        }
    }

    // Reviews management
    @Override
    public void addReview(Restaurant restaurant, Review review) {
        restaurant.getReviews().add(review);
        restaurantRepo.updateRestaurant(restaurant);
    }

    @Override
    public void removeReview(Restaurant restaurant, Review review) {
        restaurant.getReviews().remove(review);
        restaurantRepo.updateRestaurant(restaurant);
    }

    @Override
    public List<Review> getReviews(Restaurant restaurant) {
        return restaurant.getReviews();
    }

    @Override
    public void displayReviews(Restaurant restaurant) {
        System.out.println("Reviews for " + restaurant.getName() + ":");
        for (Review r : restaurant.getReviews()) {
            System.out.println("- " + r.getUser().getName() + ": " + r.getComment() + " (Rating: " + r.getRating() + ")");
        }
    }

    // Orders management
    @Override
    public void addOrder(Restaurant restaurant, Order order) {
        restaurant.getOrders().add(order);
        restaurantRepo.updateRestaurant(restaurant);
    }

    @Override
    public List<Order> getOrders(Restaurant restaurant) {
        return restaurant.getOrders();
    }

    @Override
    public void showOrders(Restaurant restaurant) {
        System.out.println("Orders for " + restaurant.getName() + ":");
        for (Order o : restaurant.getOrders()) {
            System.out.println("- Order ID: " + o.getOrderId() + ", Status: " + o.getStatus() 
                + " Delivery person: " + (o.getDelivery() != null && o.getDelivery().getDeliveryPerson() != null ? o.getDelivery().getDeliveryPerson().getName() : "Unassigned"));
        }
    }

    // Other info
    @Override
    public String getRestaurantInfo(Restaurant restaurant) {
        return "Restaurant{" +
                "name='" + restaurant.getName() + '\'' +
                ", address='" + restaurant.getAddress() + '\'' +
                ", phoneNumber='" + restaurant.getPhoneNumber() + '\'' +
                ", email='" + restaurant.getEmail() + '\'' +
                ", openingHours='" + restaurant.getOpeningHours() + '\'' +
                ", rating=" + restaurant.getRating() +
                '}';
    }
}
