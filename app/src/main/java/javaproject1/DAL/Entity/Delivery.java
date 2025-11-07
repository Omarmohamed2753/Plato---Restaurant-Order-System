package javaproject1.DAL.Entity;
import java.util.Date;
public class Delivery {
    private int deliveryId;
    private Employee deliveryPerson;
    private String status;
    private Date estimatedDeliveryTime;
    public Delivery(int deliveryId) {
        this.deliveryId = deliveryId;
        this.status = "Pending Assignment";
    }
    public Delivery(int deliveryId, Employee deliveryPerson, String status, Date estimatedDeliveryTime) {
        this.deliveryId = deliveryId;
        this.deliveryPerson = deliveryPerson;
        this.status = status;
        this.estimatedDeliveryTime = estimatedDeliveryTime;
    }
    public Delivery() {}
    public void setDeliveryId(int deliveryId) { this.deliveryId = deliveryId; }
    public void setDeliveryPerson(Employee deliveryPerson) { this.deliveryPerson = deliveryPerson; }
    public void setStatus(String status) { this.status = status; }
    public void setEstimatedDeliveryTime(Date estimatedDeliveryTime) { this.estimatedDeliveryTime = estimatedDeliveryTime; }
    public int getDeliveryId() { return deliveryId; }
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
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Delivery other = (Delivery) obj;
        return deliveryId == other.deliveryId &&
               ((deliveryPerson == null && other.deliveryPerson == null) ||
                (deliveryPerson != null && deliveryPerson.equals(other.deliveryPerson))) &&
               ((status == null && other.status == null) ||
                (status != null && status.equals(other.status))) &&
               ((estimatedDeliveryTime == null && other.estimatedDeliveryTime == null) ||
                (estimatedDeliveryTime != null && estimatedDeliveryTime.equals(other.estimatedDeliveryTime)));
    }
}
