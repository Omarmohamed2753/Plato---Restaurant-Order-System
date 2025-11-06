package javaproject1.BLL.Service;

import javaproject1.DAL.Entity.*;
import javaproject1.DAL.Repo.Implementation.UserRepo;

import java.sql.Connection;
import java.util.List;

public class UserService {
    private final UserRepo userRepo;

    public UserService(Connection connection) {
        this.userRepo = new UserRepo(connection);
    }

    public void registerUser(String name, String email, String password) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(password);
        userRepo.addUser(user);
        System.out.println("User registered: " + name);
    }

    public boolean login(String email, String password) {
        List<User> users = userRepo.getAllUsers();
        for (User u : users) {
            if (u.getEmail().equals(email) && u.getPassword().equals(password)) {
                System.out.println("Login successful for: " + email);
                return true;
            }
        }
        System.out.println("Login failed for: " + email);
        return false;
    }

    public User getUserById(int id) {
        return userRepo.getUserById(id);
    }

    public void updateUser(int id, String name, String email, String password) {
        User user = new User();
        user.setId(id);
        user.setName(name);
        user.setEmail(email);
        user.setPassword(password);
        userRepo.updateUser(user);
        System.out.println("User updated: " + name);
    }

    public void deleteUser(int id) {
        userRepo.deleteUser(id);
        System.out.println("User deleted with ID: " + id);
    }

    public List<User> getAllUsers() {
        return userRepo.getAllUsers();
    }

    public void displayUsers() {
        List<User> users = getAllUsers();
        System.out.println("Users:");
        for (User u : users) {
            System.out.println("- ID: " + u.getId() + " | Name: " + u.getName() + " | Email: " + u.getEmail());
        }
    }
    public void subscribeUser(User user, Subscription subscription) {
    user.setSubscription(subscription);
    user.setElite(true);
    System.out.println("User " + user.getName() + " subscribed successfully.");
}
}