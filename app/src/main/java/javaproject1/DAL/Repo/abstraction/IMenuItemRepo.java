package javaproject1.DAL.Repo.abstraction;

import java.util.List;
import javaproject1.DAL.Entity.MenuItem;

public interface IMenuItemRepo {
    void addMenuItem(MenuItem item);
    void addMenuItemWithMenuId(MenuItem item, String menuId); // New method
    MenuItem getMenuItemById(int id);
    void updateMenuItem(MenuItem item);
    void deleteMenuItem(int id);
    List<MenuItem> getAllMenuItems();
}