package javaproject1.BLL.Service.abstraction;

import javaproject1.DAL.Entity.Order;
import javaproject1.DAL.Entity.Subscription;
import javaproject1.DAL.Enums.OrderStatus;
import java.util.List;

public interface IOrderService {

    double calculateTotal(Order order, Subscription subscription);
    void updateStatus(Order order, OrderStatus status);

    void addOrder(Order order);
    Order getOrderById(int id);
    void updateOrder(Order order);
    void deleteOrder(int id);
    List<Order> getAllOrders();
}
