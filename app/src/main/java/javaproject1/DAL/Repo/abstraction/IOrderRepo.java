package javaproject1.DAL.Repo.abstraction;

import java.util.List;

import javaproject1.DAL.Entity.Order;

public interface IOrderRepo {
    void addOrder(Order order);
    Order getOrderById(int id);
    void updateOrder(Order order);
    void deleteOrder(int id);
    List<Order> getAllOrders();
}
