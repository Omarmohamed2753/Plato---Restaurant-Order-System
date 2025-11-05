package javaproject1.DAL.Repo.abstraction;

import javaproject1.DAL.Entity.MenuItem;

public interface IMenuItemRepo {
    void addMenuItem(MenuItem item);
    MenuItem getMenuItemById(int id);
    void updateMenuItem(MenuItem item);
    void deleteMenuItem(int id);
}
