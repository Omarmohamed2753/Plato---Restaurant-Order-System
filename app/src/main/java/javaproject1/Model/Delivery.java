package javaproject1.Model;

import java.util.Date;

/**
 * Represents the delivery logistics for an order.
 */
public class Delivery {
    
    private String deliveryId;
    private String orderId;
    private Employee deliveryPerson;
    private String status; // e.g., "Assigned", "In Transit", "Delivered"
    private Date estimatedDeliveryTime;

    public Delivery(String deliveryId, String orderId) {
        this.deliveryId = deliveryId;
        this.orderId = orderId;
        this.status = "Pending Assignment";
    }

    public void assignDeliveryPerson(Employee person) {
        if ("DeliveryPerson".equals(person.getRole())) {
            this.deliveryPerson = person;
            this.status = "Assigned";
            // Simulate setting an estimated time
            this.estimatedDeliveryTime = new Date(System.currentTimeMillis() + 30 * 60 * 1000); // 30 mins from now
            System.out.println(person.getName() + " assigned to deliver order " + orderId);
        } else {
            System.out.println("Cannot assign " + person.getName() + ", not a delivery person.");
        }
    }

    public void updateDeliveryStatus(String status) {
        this.status = status;
        System.out.println("Delivery " + deliveryId + " status updated to " + status);
    }

    // --- Getters and Setters ---

    public String getDeliveryId() {
        return deliveryId;
    }

    public String getOrderId() {
        return orderId;
    }

    public Employee getDeliveryPerson() {
        return deliveryPerson;
    }

    public String getStatus() {
        return status;
    }

    public Date getEstimatedDeliveryTime() {
        return estimatedDeliveryTime;
    }

    @Override
    public String toString() {
        return "Delivery{" +
                "deliveryId='" + deliveryId + '\'' +
                ", deliveryPerson=" + (deliveryPerson != null ? deliveryPerson.getName() : "Unassigned") +
                ", status='" + status + '\'' +
                '}';
    }
}
