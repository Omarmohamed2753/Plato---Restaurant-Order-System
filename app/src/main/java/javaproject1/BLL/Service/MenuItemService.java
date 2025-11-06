package javaproject1.BLL.Service;

import javaproject1.DAL.Entity.MenuItem;
import javaproject1.DAL.Repo.Implementation.MenuItemRepo;

import java.sql.Connection;
import java.util.List;

public class MenuItemService {
    private final MenuItemRepo menuItemRepo;

    public MenuItemService(Connection connection) {
        this.menuItemRepo = new MenuItemRepo(connection);
    }

    public void addMenuItem(String name, String description, double price) {
        MenuItem item = new MenuItem();
        item.setName(name);
        item.setDescription(description);
        item.setPrice(price);
        menuItemRepo.addMenuItem(item);
        System.out.println("Menu item added: " + name);
    }

    public MenuItem getMenuItemById(int id) {
        return menuItemRepo.getMenuItemById(id);
    }

    public void updateMenuItem(int id, String name, String description, double price) {
        MenuItem item = new MenuItem();
        item.setItemId(id);
        item.setName(name);
        item.setDescription(description);
        item.setPrice(price);
        menuItemRepo.updateMenuItem(item);
        System.out.println("Menu item updated: " + name);
    }

    public void deleteMenuItem(int id) {
        menuItemRepo.deleteMenuItem(id);
        System.out.println("Menu item deleted with ID: " + id);
    }

    public List<MenuItem> getAllMenuItems() {
        return menuItemRepo.getAllMenuItems();
    }

    public void displayMenuItems() {
        List<MenuItem> items = getAllMenuItems();
        System.out.println("Menu Items:");
        for (MenuItem item : items) {
            System.out.println("- " + item.getName() + " | $" + item.getPrice() + " | " + item.getDescription());
        }
    }
}