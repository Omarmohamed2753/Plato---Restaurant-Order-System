package javaproject1.BLL.Service.implementation;

import javaproject1.BLL.Service.abstraction.IDeliveryService;
import javaproject1.DAL.Entity.Delivery;
import javaproject1.DAL.Entity.Employee;
import javaproject1.DAL.Entity.Order;
import javaproject1.DAL.Enums.OrderStatus;
import javaproject1.DAL.Repo.Implementation.DeliveryRepoImpl;

import java.util.List;

public class DeliveryServiceImpl implements IDeliveryService {

    private final DeliveryRepoImpl deliveryRepo;

    public DeliveryServiceImpl() {
        this.deliveryRepo = new DeliveryRepoImpl();
    }

    @Override
    public void addDelivery(Delivery delivery) {
        deliveryRepo.addDelivery(delivery);
        System.out.println("Delivery added with ID: " + delivery.getDeliveryId());
    }

    @Override
    public Delivery getDeliveryById(int id) {
        Delivery delivery = deliveryRepo.getDeliveryById(id);
        if (delivery == null) {
            System.out.println("Delivery not found with ID: " + id);
        }
        return delivery;
    }

    @Override
    public void updateDelivery(Delivery delivery) {
        deliveryRepo.updateDelivery(delivery);
        System.out.println("Delivery updated with ID: " + delivery.getDeliveryId());
    }

    @Override
    public void deleteDelivery(int id) {
        deliveryRepo.deleteDelivery(id);
        System.out.println("Delivery deleted with ID: " + id);
    }

    @Override
    public List<Delivery> getAllDeliveries() {
        return deliveryRepo.getAllDeliveries();
    }

    @Override
    public void assignDeliveryPerson(Delivery delivery, Employee person, Order order) {
        if (delivery == null || person == null || order == null) {
            System.out.println("Invalid input: delivery, employee, or order is null.");
            return;
        }
        if (!person.getRole().equalsIgnoreCase("DeliveryPerson")) {
            System.out.println("Cannot assign: employee is not a delivery person!");
            return;
        }

        delivery.setDeliveryPerson(person);
        delivery.setStatus("Assigned");
        order.setStatus(OrderStatus.OUT_FOR_DELIVERY);

        deliveryRepo.updateDelivery(delivery); 
        System.out.println("Delivery assigned to " + person.getName() + " for order #" + order.getOrderId());
    }

    @Override
    public void updateDeliveryStatus(Delivery delivery, String status) {
        if (delivery == null) {
            System.out.println("Delivery is null!");
            return;
        }
        delivery.setStatus(status);
        deliveryRepo.updateDelivery(delivery); 
        System.out.println("Delivery " + delivery.getDeliveryId() + " status updated to " + status);
    }
}
