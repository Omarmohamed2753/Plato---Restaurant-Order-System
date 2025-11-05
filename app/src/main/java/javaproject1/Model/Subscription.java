package javaproject1.Model;

import java.util.Date;
public class Subscription {
    private double cost;
    private double discountRate;
    private Date startDate;
    private Date endDate;
    private boolean active;

    public Subscription(double cost, double discountRate, Date startDate, Date endDate) {
        this.cost = cost;
        this.discountRate = discountRate;
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
    public void setCost(double cost) {
        this.cost = cost;
    }
    public double getDiscountRate() {
        return discountRate;
    }
    public void setDiscountRate(double discountRate) {
        this.discountRate = discountRate;
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
