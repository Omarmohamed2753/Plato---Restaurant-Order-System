package javaproject1.Model;

import java.util.Date;

/**
 * Represents a customer's subscription plan.
 */
public class Subscription {
    
    private String planName;
    private Date startDate;
    private Date endDate;

    public Subscription(String planName, Date startDate, Date endDate) {
        this.planName = planName;
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

    public String getPlanName() {
        return planName;
    }

    public void setPlanName(String planName) {
        this.planName = planName;
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

    @Override
    public String toString() {
        return "Subscription{" +
                "planName='" + planName + '\'' +
                ", active=" + isActive() +
                '}';
    }
}
