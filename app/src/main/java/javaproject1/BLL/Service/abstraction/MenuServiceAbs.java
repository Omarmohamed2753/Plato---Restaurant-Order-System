package javaproject1.BLL.Service.abstraction;

import javaproject1.DAL.Entity.Menu;
import javaproject1.DAL.Entity.MenuItem;


public interface MenuServiceAbs {

    void addMenu(Menu menu, int restaurantId);
    Menu getMenuByRestaurant(int restaurantId);
    void updateMenu(int restaurantId, Menu newMenu);
    void addItem(int restaurantId, MenuItem item);
    void removeItem(int restaurantId, MenuItem item);
    void displayMenu(int restaurantId);
}
