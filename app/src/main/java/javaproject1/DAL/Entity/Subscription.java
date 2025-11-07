package javaproject1.DAL.Entity;
import java.util.Date;
public class Subscription {
    private final int cost=100;
    private final double discountRate=10.0;
    private Date startDate;
    private Date endDate;
    private boolean active;
    public Subscription(Date startDate, Date endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
    }
    public boolean isActive() {
        Date now = new Date();
        return now.after(startDate) && now.before(endDate);
    }
    public double getCost() {return cost;}
    public double getDiscountRate() {return discountRate;}
    public Date getStartDate() {return startDate;}
    public void setStartDate(Date startDate) {this.startDate = startDate;}
    public Date getEndDate() {return endDate;}
    public void setEndDate(Date endDate) {this.endDate = endDate;}
    public boolean getActive() {return active;}
    public void setActive(boolean active) {this.active = active;}
    @Override
    public String toString() {
        return "Subscription{" +
                "cost=" + cost +
                ", discountRate=" + discountRate +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                '}';
    }
    @Override
    public boolean equals(Object o) {   
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Subscription that = (Subscription) o;
        return cost == that.cost &&
               Double.compare(that.discountRate, discountRate) == 0 &&
               ((startDate == null && that.startDate == null) ||
                (startDate != null && startDate.equals(that.startDate))) &&
               ((endDate == null && that.endDate == null) ||
                (endDate != null && endDate.equals(that.endDate)));
    }
}
