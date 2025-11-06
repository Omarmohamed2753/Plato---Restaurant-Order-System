package javaproject1.DAL.Repo.abstraction;

import java.util.List;
import javaproject1.DAL.Entity.Subscription;

public interface ISubscriptionRepo {
    
    public void addSubscription(Subscription subscription);
    public Subscription getSubscriptionById(int id);
    public void updateSubscription(Subscription subscription);
    public void deleteSubscription(int id);
    List<Subscription> getAllSubscriptions();
    
}