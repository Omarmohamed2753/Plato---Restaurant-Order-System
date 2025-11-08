package javaproject1.BLL.Service.abstraction;

import javaproject1.DAL.Entity.*;

import java.util.List;

public interface RestaurantServiceAbs {
    
    // CRUD
    void addRestaurant(Restaurant restaurant);
    Restaurant getRestaurantById(int id);
    void updateRestaurant(Restaurant restaurant);
    void deleteRestaurant(int id);
    

    // Menu management
    Menu getMenu(Restaurant restaurant);
    void updateMenu(Restaurant restaurant, Menu newMenu);
    void addMenuItem(Restaurant restaurant, MenuItem item);
    void removeMenuItem(Restaurant restaurant, MenuItem item);
    void displayMenu(Restaurant restaurant);

    // Employee management
    void hireEmployee(Restaurant restaurant, Employee employee);
    void fireEmployee(Restaurant restaurant, Employee employee);
    void showEmployees(Restaurant restaurant);

    // Reviews management
    void addReview(Restaurant restaurant, Review review);
    void removeReview(Restaurant restaurant, Review review);
    List<Review> getReviews(Restaurant restaurant);
    void displayReviews(Restaurant restaurant);

    // Orders management
    void addOrder(Restaurant restaurant, Order order);
    List<Order> getOrders(Restaurant restaurant);
    void showOrders(Restaurant restaurant);

    // Other info
    String getRestaurantInfo(Restaurant restaurant);
}
