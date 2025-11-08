package javaproject1.BLL.Service.abstraction;

import javaproject1.DAL.Entity.Subscription;
import java.util.List;

public interface ISubscriptionService {
    void addSubscription(Subscription subscription);
    Subscription getSubscriptionById(int id);
    void updateSubscription(Subscription subscription);
    void deleteSubscription(int id);
    List<Subscription> getAllSubscriptions();
}
