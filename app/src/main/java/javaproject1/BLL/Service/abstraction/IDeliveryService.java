package javaproject1.BLL.Service.abstraction;

import javaproject1.DAL.Entity.Delivery;
import javaproject1.DAL.Entity.Employee;
import javaproject1.DAL.Entity.Order;
import java.util.List;

public interface IDeliveryService {

    void addDelivery(Delivery delivery);
    Delivery getDeliveryById(int id);
    void updateDelivery(Delivery delivery);
    void deleteDelivery(int id);
    List<Delivery> getAllDeliveries();

    void assignDeliveryPerson(Delivery delivery, Employee person, Order order);
    void updateDeliveryStatus(Delivery delivery, String status);
}
