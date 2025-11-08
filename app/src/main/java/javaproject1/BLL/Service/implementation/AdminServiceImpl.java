package javaproject1.BLL.Service.implementation;

import javaproject1.BLL.Service.abstraction.IAdminService;
import javaproject1.DAL.Entity.*;
import javaproject1.DAL.Enums.OrderStatus;
import javaproject1.DAL.Repo.Implementation.AdminRepoImpl;

import java.util.List;

public class AdminServiceImpl implements IAdminService {

    private final AdminRepoImpl adminRepo;
    private final MenuServiceImpl menuService;
    private final OrderServiceImpl orderService;
    private final DeliveryServiceImpl deliveryService;
    private final RestaurantServiceImpl restaurantService;

    public AdminServiceImpl() {
        this.adminRepo = new AdminRepoImpl();
        this.menuService = new MenuServiceImpl();
        this.orderService = new OrderServiceImpl();
        this.deliveryService = new DeliveryServiceImpl();
        this.restaurantService = new RestaurantServiceImpl();
    }

    @Override
    public void addAdmin(Admin admin) {
        adminRepo.addAdmin(admin);
        System.out.println("Admin added: " + admin.getName());
    }

    @Override
    public Admin getAdminById(int id) {
        return adminRepo.getAdminById(id);
    }

    @Override
    public void updateAdmin(Admin admin) {
        adminRepo.updateAdmin(admin);
        System.out.println("Admin updated: " + admin.getName());
    }

    @Override
    public void deleteAdmin(int id) {
        adminRepo.deleteAdmin(id);
        System.out.println("Admin deleted with ID: " + id);
    }

    @Override
    public List<Admin> getAllAdmins() {
        return adminRepo.getAllAdmins();
    }

    @Override
    public boolean loginAdmin(Admin admin, String username, String password) {
        return admin != null && admin.getEmail() != null &&
               admin.getEmail().equals(username) && admin.getPassword().equals(password);
    }

    @Override
    public void manageUser(Admin admin, List<User> users, int userId) {
        if (admin == null || users == null) return;
        for (User u : users) {
            if (u.getId() == userId) {
                System.out.println("Managing user: " + u.getName());
                if (u.getSubscription() != null) {
                    u.getSubscription().setActive(!u.getSubscription().isActive());
                    System.out.println("User " + (u.getSubscription().isActive() ? "activated" : "deactivated"));
                } else {
                    System.out.println("User has no subscription to manage!");
                }
                return;
            }
        }
        System.out.println("User not found!");
    }

    @Override
    public void manageOrder(Admin admin, Order order, Delivery delivery) {
        if (admin == null || order == null) return;
        Restaurant restaurant = admin.getRestaurant();
        if (restaurant == null || !restaurant.getOrders().contains(order)) {
            System.out.println("Order not found in this restaurant!");
            return;
        }

        switch (order.getStatus()) {
            case PENDING -> orderService.updateStatus(order, OrderStatus.CONFIRMED);
            case CONFIRMED -> orderService.updateStatus(order, OrderStatus.PREPARING);
            case PREPARING -> orderService.updateStatus(order, OrderStatus.READY_FOR_DELIVERY);
            case READY_FOR_DELIVERY -> {
                if (delivery != null && delivery.getDeliveryPerson() != null) {
                    deliveryService.assignDeliveryPerson(delivery,delivery.getDeliveryPerson(), order);
                    orderService.updateStatus(order, OrderStatus.OUT_FOR_DELIVERY);
                } else {
                    System.out.println("No delivery person assigned for order #" + order.getOrderId());
                }
            }
            case OUT_FOR_DELIVERY -> orderService.updateStatus(order, OrderStatus.DELIVERED);
            case DELIVERED, CANCELLED -> System.out.println("Order #" + order.getOrderId() + " is already " + order.getStatus());
            default -> System.out.println("Unknown order status for order #" + order.getOrderId());
        }
    }

    @Override
    public void addEmployee(Admin admin, Employee employee) {
        if (admin != null && admin.getRestaurant() != null) {
            restaurantService.hireEmployee(admin.getRestaurant(), employee);
        }
    }

    @Override
    public void removeEmployee(Admin admin, Employee employee) {
        if (admin != null && admin.getRestaurant() != null) {
            restaurantService.fireEmployee(admin.getRestaurant(), employee);
        }
    }

    @Override
    public void addMenuItem(Admin admin, MenuItem item, Menu menu) {
        if (admin != null && admin.getRestaurant() != null) {
            menuService.addItem(admin.getRestaurant().getMenu().getMenuId(), item);
        }
    }

    @Override
    public void removeMenuItem(Admin admin, MenuItem item, Menu menu) {
        if (admin != null && admin.getRestaurant() != null) {
            menuService.removeItem(admin.getRestaurant().getMenu().getMenuId(), item);
        }
    }

    @Override
    public void manageMenu(Admin admin, Menu menu) {
        if (admin != null && admin.getRestaurant() != null) {
            menuService.updateMenu(admin.getRestaurant().getMenu().getMenuId(), menu);
        }
    }

    @Override
    public void showEmployees(Admin admin) {
        if (admin != null && admin.getRestaurant() != null) {
           restaurantService.showEmployees((Restaurant) admin.getRestaurant().getEmployees());
        }
    }

    @Override
    public void showReviews(Admin admin) {
        if (admin != null && admin.getRestaurant() != null) {
           restaurantService.displayReviews((Restaurant) admin.getRestaurant().getReviews());
        }
    }

    @Override
    public void showMenu(Admin admin) {
        if (admin != null && admin.getRestaurant() != null) {
            menuService.displayMenu(admin.getRestaurant().getMenu().getMenuId());
        }
    }

    @Override
    public void displayOrders(Admin admin) {
        if (admin != null && admin.getRestaurant() != null) {
            orderService.getAllOrders().stream()
                .filter(order -> order.getRestaurant().getRestaurantId() == admin.getRestaurant().getRestaurantId())
                .forEach(order -> System.out.println("Order ID: " + order.getOrderId() + ", Status: " + order.getStatus()));
        }
    }
}
