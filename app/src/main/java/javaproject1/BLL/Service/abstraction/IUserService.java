package javaproject1.BLL.Service.abstraction;

import javaproject1.DAL.Entity.*;

import java.util.List;

public interface IUserService {

    // CRUD
    void addUser(User user);
    User getUserById(int id);
    void updateUser(User user);
    void deleteUser(int id);
    List<User> getAllUsers();
    User getUserByEmail(String email);

    // Account & Auth
    boolean createAccount(User user);
    boolean login(String email, String password);

    // Profile management
    void updateProfile(User user, String name, int age, String phoneNumber, String email, String password, boolean isElite, Subscription subscription);

    // Menu browsing
    List<MenuItem> browseMenu(User user, Restaurant restaurant);

    // Subscription
    void subscribeElite(User user, Subscription subscription);

    // Address management
    void addAddress(User user, Address address);
    void removeAddress(User user, Address address);
}
