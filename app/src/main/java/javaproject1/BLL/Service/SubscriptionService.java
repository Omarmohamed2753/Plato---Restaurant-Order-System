package javaproject1.BLL.Service;

import javaproject1.DAL.Entity.Subscription;
import java.util.Date;

public class SubscriptionService {

    public Subscription createSubscription(Date startDate, Date endDate) {
        Subscription subscription = new Subscription(startDate, endDate);
        subscription.setActive(true);
        System.out.println("Subscription created from " + startDate + " to " + endDate);
        return subscription;
    }

    public void activateSubscription(Subscription subscription) {
        subscription.setActive(true);
        System.out.println("Subscription activated.");
    }

    public void deactivateSubscription(Subscription subscription) {
        subscription.setActive(false);
        System.out.println("Subscription deactivated.");
    }

    public boolean isSubscriptionActive(Subscription subscription) {
        return subscription.isActive();
    }

    public double applyDiscount(double totalAmount, Subscription subscription) {
        if (subscription != null && subscription.isActive()) {
            double discount = totalAmount * (subscription.getDiscountRate() / 100);
            System.out.println("Discount applied: " + discount);
            return totalAmount - discount;
        }
        return totalAmount;
    }
}