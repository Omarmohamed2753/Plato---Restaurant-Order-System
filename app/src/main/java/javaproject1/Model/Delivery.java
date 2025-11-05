package javaproject1.Model;

import java.util.Date;

import javaproject1.Enums.OrderStatus;

public class Delivery {
    private String deliveryId;
    private Employee deliveryPerson;
    private String status;
    private Date estimatedDeliveryTime;

    public Delivery(String deliveryId) {
        this.deliveryId = deliveryId;
        this.status = "Pending Assignment";
    }

    public void assignDeliveryPerson(Employee person, Order order) {
        if (person == null || !person.getRole().equalsIgnoreCase("DeliveryPerson")) {
            System.out.println("Cannot assign: employee is not a delivery person!");
            return;
        }
        this.deliveryPerson = person;
        this.status = "Assigned";
        order.updateStatus(OrderStatus.OUT_FOR_DELIVERY);
        System.out.println("Delivery assigned to " + person.getName() + " for order #" + order.getOrderId());
    }

    public void updateDeliveryStatus(String status) {
        this.status = status;
        System.out.println("Delivery " + deliveryId + " status updated to " + status);
    }

    // getters/setters
    public void setDeliveryId(String deliveryId) { this.deliveryId = deliveryId; }
    public void setDeliveryPerson(Employee deliveryPerson) { this.deliveryPerson = deliveryPerson; }
    public void setStatus(String status) { this.status = status; }
    public void setEstimatedDeliveryTime(Date estimatedDeliveryTime) { this.estimatedDeliveryTime = estimatedDeliveryTime; }
    public String getDeliveryId() { return deliveryId; }
    public Employee getDeliveryPerson() { return deliveryPerson; }
    public String getStatus() { return status; }
    public Date getEstimatedDeliveryTime() { return estimatedDeliveryTime; }

    @Override
    public String toString() {
        return "Delivery{" +
                "deliveryId='" + deliveryId + '\'' +
                ", deliveryPerson=" + (deliveryPerson != null ? deliveryPerson.getName() : "Unassigned") +
                ", status='" + status + '\'' +
                '}';
    }
}
