package javaproject1.BLL.Service.implementation;

import javaproject1.BLL.Service.abstraction.ISubscriptionService;
import javaproject1.DAL.Entity.Subscription;
import javaproject1.DAL.Repo.Implementation.SubscriptionRepoImpl;
import javaproject1.DAL.Repo.abstraction.ISubscriptionRepo;

import java.util.List;

public class SubscriptionServiceImpl implements ISubscriptionService {

    private final ISubscriptionRepo subscriptionRepo;

    public SubscriptionServiceImpl() {
        this(new SubscriptionRepoImpl());
    }

    public SubscriptionServiceImpl(ISubscriptionRepo subscriptionRepo) {
        this.subscriptionRepo = subscriptionRepo;
    }

    @Override
    public void addSubscription(Subscription subscription) {
        subscriptionRepo.addSubscription(subscription);
        System.out.println("Subscription added with ID: " + subscription.getId());
    }

    @Override
    public Subscription getSubscriptionById(int id) {
        Subscription sub = subscriptionRepo.getSubscriptionById(id);
        if (sub == null) {
            System.out.println("Subscription not found with ID: " + id);
        }
        return sub;
    }

    @Override
    public void updateSubscription(Subscription subscription) {
        subscriptionRepo.updateSubscription(subscription);
        System.out.println("Subscription updated with ID: " + subscription.getId());
    }

    @Override
    public void deleteSubscription(int id) {
        subscriptionRepo.deleteSubscription(id);
        System.out.println("Subscription deleted with ID: " + id);
    }

    @Override
    public List<Subscription> getAllSubscriptions() {
        return subscriptionRepo.getAllSubscriptions();
    }
}
