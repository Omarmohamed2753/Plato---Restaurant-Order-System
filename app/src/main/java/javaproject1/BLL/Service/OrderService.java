package javaproject1.BLL.Service;

import javaproject1.DAL.Entity.Order;
import javaproject1.DAL.Enums.OrderStatus;
import javaproject1.DAL.Repo.Implementation.OrderRepo;

import java.sql.Connection;
import java.util.List;

public class OrderService {
    private final OrderRepo orderRepo;

    public OrderService(Connection connection, OrderRepo orderRepo) {
        this.orderRepo = orderRepo;
    }

    public void addOrder(Order order) {
        orderRepo.addOrder(order);
        System.out.println("Order added: " + order.getOrderId());
    }

    public Order getOrderById(String id) {
        return orderRepo.getOrderById(id);
    }

    public void updateOrder(Order order) {
        orderRepo.updateOrder(order);
        System.out.println("Order updated: " + order.getOrderId());
    }

    public void deleteOrder(String id) {
        orderRepo.deleteOrder(id);
        System.out.println("Order deleted with ID: " + id);
    }

    public List<Order> getAllOrders() {
        return orderRepo.getAllOrders();
    }

    public void updateOrderStatus(String orderId, OrderStatus status) {
        Order order = getOrderById(orderId);
        if (order != null) {
            order.setStatus(status);
            updateOrder(order);
            System.out.println("Order status updated to: " + status);
        } else {
            System.out.println("Order not found with ID: " + orderId);
        }
    }

    public void displayOrders() {
        List<Order> orders = getAllOrders();
        System.out.println("Orders:");
        for (Order o : orders) {
            System.out.println("- ID: " + o.getOrderId() + " | Status: " + o.getStatus() + " | Total: $" + o.getTotalAmount());
        }
    }
}