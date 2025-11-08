package javaproject1.BLL.Service.abstraction;

import javaproject1.DAL.Entity.MenuItem;

import java.util.List;

public interface MenuItemServiceAbs {

    void addMenuItem(MenuItem item); 
    MenuItem getMenuItemById(int id); 
    void updateMenuItem(MenuItem item); 
    void deleteMenuItem(int id); 
    List<MenuItem> getAllMenuItems(); 
}
