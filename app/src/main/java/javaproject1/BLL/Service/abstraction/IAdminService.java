package javaproject1.BLL.Service.abstraction;

import javaproject1.DAL.Entity.Admin;
import javaproject1.DAL.Entity.User;
import javaproject1.DAL.Entity.Employee;
import javaproject1.DAL.Entity.Order;
import javaproject1.DAL.Entity.Delivery;
import javaproject1.DAL.Entity.Menu;
import javaproject1.DAL.Entity.MenuItem;

import java.util.List;

public interface IAdminService {
    void addAdmin(Admin admin);
    Admin getAdminById(int id);
    void updateAdmin(Admin admin);
    void deleteAdmin(int id);
    List<Admin> getAllAdmins();
    boolean loginAdmin(Admin admin, String username, String password);
    void manageUser(Admin admin, List<User> users, int userId);
    void manageOrder(Admin admin, Order order, Delivery delivery);
    void addEmployee(Admin admin, Employee employee);
    void removeEmployee(Admin admin, Employee employee);
    void addMenuItem(Admin admin, MenuItem item, Menu menu);
    void removeMenuItem(Admin admin, MenuItem item, Menu menu);
    void manageMenu(Admin admin, Menu menu);
    void showEmployees(Admin admin);
    void showReviews(Admin admin);
    void showMenu(Admin admin);
    void displayOrders(Admin admin);
}
