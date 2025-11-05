package javaproject1.DAL.Entity;

import java.util.Date;

public class Delivery {
    private String deliveryId;
    private Employee deliveryPerson;
    private String status;
    private Date estimatedDeliveryTime;

    public Delivery(String deliveryId) {
        this.deliveryId = deliveryId;
        this.status = "Pending Assignment";
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
