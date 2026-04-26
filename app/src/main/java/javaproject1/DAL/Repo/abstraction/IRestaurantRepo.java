package javaproject1.DAL.Repo.abstraction;

import java.util.List;

import javaproject1.DAL.Entity.Restaurant;

public interface IRestaurantRepo {
    
    public void addRestaurant(Restaurant restaurant);
    public Restaurant getRestaurantById(int id);
    public void updateRestaurant(Restaurant restaurant);
    public void deleteRestaurant(int id);
    public List<Restaurant> getAllRestaurants();
    
}