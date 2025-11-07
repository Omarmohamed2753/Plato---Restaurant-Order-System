package javaproject1.BLL.Service;

import javaproject1.DAL.Entity.MenuItem;
import javaproject1.DAL.Repo.Implementation.MenuRepoImpl;
import java.sql.Connection;
import javaproject1.DAL.Entity.Menu;
import java.util.List;

public class MenuService {
    private final MenuRepoImpl menuRepo;
    private final Menu menuModel;

    public MenuService(Connection connection) {
        this.menuRepo = new MenuRepoImpl();
        this.menuModel = new Menu(); 
    }

    public void addMenuItem(MenuItem item) {
        menuRepo.addMenu(item);
        System.out.println("Item added: " + item.getName());
    }

    public void removeMenuItem(MenuItem item) {
        menuRepo.deleteMenu(item.getItemId());
        System.out.println("Item removed: " + item.getName());
    }

    public void updateMenuItem(MenuItem item) {
        menuRepo.updateMenu(item);
        System.out.println("Item updated: " + item.getName());
    }

    public List<MenuItem> getAllMenuItems() {
        List<MenuItem> items = menuRepo.getAllMenuItems();
        menuModel.setItems(items);
        return items;
    }

    public void displayMenu() {
        System.out.println("Menu:");
        for (MenuItem item : menuModel.getItems()) {
            System.out.println("- " + item.getName() + ": $" + item.getPrice());
        }
    }
}