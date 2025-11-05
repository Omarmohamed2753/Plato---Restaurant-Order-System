package javaproject1.DAL.Repo.abstraction;

import javaproject1.DAL.Entity.Order;

public interface IOrderRepo {
    void addOrder(Order order);
    Order getOrderById(String id);
    void updateOrder(Order order);
    void deleteOrder(String id);
}
