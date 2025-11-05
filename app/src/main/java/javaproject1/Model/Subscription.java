package javaproject1.Model;

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
    /**
     * Checks if the subscription is currently active.
     */
    public boolean isActive() {
        Date now = new Date();
        return now.after(startDate) && now.before(endDate);
    }
    // --- Getters and Setters ---
    public double getCost() {
        return cost;
    }
    public double getDiscountRate() {
        return discountRate;
    }
    

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }
    public boolean getActive() {
        return active;
    }
    public void setActive(boolean active) {
        this.active = active;
    }
    @Override
    public String toString() {
        return "Subscription{" +
                "cost=" + cost +
                ", discountRate=" + discountRate +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                '}';
    }
}
