package javaproject1.BLL.Service.implementation;

import javaproject1.BLL.Service.abstraction.IUserService;
import javaproject1.DAL.Entity.*;
import javaproject1.DAL.Repo.Implementation.UserRepoImpl;
import javaproject1.DAL.Repo.abstraction.IUserRepo;

import java.util.ArrayList;
import java.util.List;

public class UserServiceImpl implements IUserService {

    private final IUserRepo userRepo;
    private final List<User> allUsers; // for createAccount tracking (temporary, could sync with DB)

    public UserServiceImpl() {
        this(new UserRepoImpl());
    }

    public UserServiceImpl(IUserRepo userRepo) {
        this.userRepo = userRepo;
        this.allUsers = userRepo.getAllUsers(); // load from DB
    }

    // CRUD 
    @Override
    public void addUser(User user) {
        userRepo.addUser(user);
        allUsers.add(user);
    }

    @Override
    public User getUserById(int id) {
        return userRepo.getUserById(id);
    }

    @Override
    public void updateUser(User user) {
        userRepo.updateUser(user);
    }

    @Override
    public void deleteUser(int id) {
        userRepo.deleteUser(id);
        allUsers.removeIf(u -> u.getId().equals(String.valueOf(id)));
    }

    @Override
    public List<User> getAllUsers() {
        return userRepo.getAllUsers();
    }

    @Override
    public User getUserByEmail(String email) {
        return userRepo.getUserByEmail(email);
    }

    // Account & Auth
    @Override
    public boolean createAccount(User user) {
        for (User u : allUsers) {
            if (u.getEmail().equalsIgnoreCase(user.getEmail())) {
                System.out.println("Email already exists!");
                return false;
            }
        }
        addUser(user);
        System.out.println("Account created successfully for: " + user.getName());
        return true;
    }

    @Override
    public boolean login(String email, String password) {
        User u = getUserByEmail(email);
        if (u == null) return false;
        return u.getPassword().equals(password);
    }

    // Profile management
    @Override
    public void updateProfile(User user, String name, int age, String phoneNumber, String email, String password, boolean isElite, Subscription subscription) {
        user.setName(name);
        user.setAge(age);
        user.setPhoneNumber(phoneNumber);
        user.setEmail(email);
        user.setPassword(password);
        user.setElite(isElite);
        user.setSubscription(subscription);
        updateUser(user);
    }

    // Menu browsing
    @Override
    public List<MenuItem> browseMenu(User user, Restaurant restaurant) {
        if (restaurant == null || restaurant.getMenu() == null) {
            System.out.println("No menu available for this restaurant.");
            return new ArrayList<>();
        }
        System.out.println("Menu for " + restaurant.getName() + ":");
        for (MenuItem item : restaurant.getMenu().getItems()) {
            System.out.println("- " + item.getName() + " : " + item.getPrice());
        }
        return restaurant.getMenu().getItems();
    }

    // Subscription 
    @Override
    public void subscribeElite(User user, Subscription subscription) {
        user.setElite(true);
        user.setSubscription(subscription);
        updateUser(user);
    }

    // Address management 
    @Override
    public void addAddress(User user, Address address) {
        user.getAddresses().add(address);
        updateUser(user);
    }

    @Override
    public void removeAddress(User user, Address address) {
        user.getAddresses().remove(address);
        updateUser(user);
    }
}
