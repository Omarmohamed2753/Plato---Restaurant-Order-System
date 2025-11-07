package javaproject1.BLL.Service;

import javaproject1.DAL.Repo.Implementation.UserRepoImpl;
import javaproject1.DAL.Repo.Implementation.MenuRepoImpl;
import javaproject1.DAL.Entity.User;
import javaproject1.DAL.Entity.MenuItem;
import javaproject1.DAL.Entity.Order;

import java.sql.Connection;
import java.util.List;

public class AdminService {
    private final UserRepoImpl userRepo;
    private final MenuRepoImpl menuRepo;
    private final Order orderRepo;

    public AdminService(Connection connection) {
        this.userRepo = new UserRepoImpl();
        this.menuRepo = new MenuRepoImpl();
        this.orderRepo = new Order();
    }

    public List<User> getAllUsers() {
        return userRepo.getAllUsers();
    }

    public void deleteUser(int id) {
        userRepo.deleteUser(id);
    }

    public void addUser(int id,String name, String email, String password) {
        User user = new User();
        user.setId(id);
        user.setName(name);
        user.setEmail(email);
        user.setPassword(password);
        userRepo.addUser(user);
    }

    public List<MenuItem> getAllMenuItems() {
        return ((AdminService) menuRepo).getAllMenuItems();
    }

    public void addMenuItem(String name, String description, double price) {
        MenuItem item = new MenuItem();
        item.setName(name);
        item.setDescription(description);
        item.setPrice(price);
        menuRepo.addMenuItem(item);
    }

    public void deleteMenuItem(int id) {
        menuRepo.deleteMenuItem(id);
    }

    public List<Order> getAllOrders() {
        return orderRepo.getAllOrders();
    }

    public void updateOrder(Order order) {
        orderRepo.updateOrder(order);
    }
}