package javaproject1.BLL.Service.implementation;

import javaproject1.BLL.Service.abstraction.IOrderService;
import javaproject1.DAL.Entity.Order;
import javaproject1.DAL.Entity.Subscription;
import javaproject1.DAL.Enums.OrderStatus;
import javaproject1.DAL.Repo.Implementation.OrderRepoImpl;
import javaproject1.DAL.Repo.Implementation.UserRepoImpl;
import javaproject1.DAL.Repo.abstraction.IUserRepo;

import java.util.List;

public class OrderServiceImpl implements IOrderService {

    private final OrderRepoImpl orderRepo;
    private final IUserRepo userRepo;
    public OrderServiceImpl() {
        this.userRepo = new UserRepoImpl();
        this.orderRepo = new OrderRepoImpl(userRepo);
    }

    @Override
    public double calculateTotal(Order order, Subscription subscription) {
        if (order.getItems() == null || order.getItems().isEmpty()) {
            System.out.println("No items in order!");
            return 0.0;
        }

        double subtotal = 0.0;
        for (var item : order.getItems()) {
            subtotal += item.getSubPrice();
        }

        double tax = subtotal * 0.1;    // 10% tax
        double deliveryFee = 30.0;      // fixed delivery fee
        double discount = 0.0;
        double tempTotal = subtotal + tax + deliveryFee;

        if (subscription != null && subscription.isActive()) {
            discount = tempTotal * 0.10; // 10% discount
            System.out.println("Subscription discount applied: 10%");
        }

        double total = tempTotal - discount;
        order.setTotalAmount(total);    // حفظ المجموع في object
        orderRepo.updateOrder(order);   // حفظ التغيير في DB

        System.out.println("Subtotal: " + subtotal +
                " | Tax: " + tax +
                " | Delivery: " + deliveryFee +
                " | Discount: " + discount +
                " | Total: " + total);

        return total;
    }

    @Override
    public void updateStatus(Order order, OrderStatus status) {
        if (order != null) {
            order.setStatus(status);
            orderRepo.updateOrder(order);  // حفظ التغيير في DB
            System.out.println("Order " + order.getOrderId() + " status updated to " + status);
        }
    }

    @Override
    public void addOrder(Order order) {
        orderRepo.addOrder(order);
        System.out.println("Order added with ID: " + order.getOrderId());
    }

    @Override
    public Order getOrderById(int id) {
        Order order = orderRepo.getOrderById(id);
        if (order == null) {
            System.out.println("Order not found with ID: " + id);
        }
        return order;
    }

    @Override
    public void updateOrder(Order order) {
        orderRepo.updateOrder(order);
        System.out.println("Order updated with ID: " + order.getOrderId());
    }

    @Override
    public void deleteOrder(int id) {
        orderRepo.deleteOrder(id);
        System.out.println("Order deleted with ID: " + id);
    }

    @Override
    public List<Order> getAllOrders() {
        return orderRepo.getAllOrders();
    }
}
