package javaproject1.BLL.Service.implementation;

import javaproject1.BLL.Service.abstraction.MenuItemServiceAbs;
import javaproject1.DAL.Entity.MenuItem;
import javaproject1.DAL.Repo.Implementation.MenuItemRepoImpl;
import javaproject1.DAL.Repo.abstraction.IMenuItemRepo;

import java.util.List;

public class MenuItemServiceImpl implements MenuItemServiceAbs {

    private final IMenuItemRepo menuItemRepo;

    public MenuItemServiceImpl() {
        this(new MenuItemRepoImpl());
    }

    public MenuItemServiceImpl(IMenuItemRepo menuItemRepo) {
        this.menuItemRepo = menuItemRepo;
    }

    @Override
    public void addMenuItem(MenuItem item) {
        menuItemRepo.addMenuItem(item);
    }

    @Override
    public MenuItem getMenuItemById(int id) {
        return menuItemRepo.getMenuItemById(id);
    }

    @Override
    public void updateMenuItem(MenuItem item) {
        menuItemRepo.updateMenuItem(item);
    }

    @Override
    public void deleteMenuItem(int id) {
        menuItemRepo.deleteMenuItem(id);
    }

    @Override
    public List<MenuItem> getAllMenuItems() {
        return menuItemRepo.getAllMenuItems();
    }
}
