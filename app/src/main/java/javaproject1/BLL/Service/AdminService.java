package javaproject1.BLL.Service;

import javaproject1.DAL.Repo.Implementation.UserRepo;
import javaproject1.DAL.Repo.Implementation.MenuRepo;
import javaproject1.DAL.Repo.Implementation.OrderRepo;
import javaproject1.DAL.Entity.User;
import javaproject1.DAL.Entity.MenuItem;
import javaproject1.DAL.Entity.Order;

import java.sql.Connection;
import java.util.List;

public class AdminService {
    private final UserRepo userRepo;
    private final MenuRepo menuRepo;
    private final OrderRepo orderRepo;

    public AdminService(Connection connection) {
        this.userRepo = new UserRepo(connection);
        this.menuRepo = new MenuRepo(connection);
        this.orderRepo = new OrderRepo(connection, userRepo);
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
        return menuRepo.getAllMenuItems();
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