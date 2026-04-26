package javaproject1.DAL.Repo.Implementation;

import javaproject1.DAL.Entity.Subscription;
import javaproject1.DAL.Repo.abstraction.ISubscriptionRepo;
import javaproject1.plato.JPAUtil;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;

public class SubscriptionRepoImpl implements ISubscriptionRepo {

    @Override
    public void addSubscription(Subscription subscription) {
        EntityManager em = JPAUtil.getEntityManager();
        em.getTransaction().begin();

        javaproject1.plato.Subscriptions s = new javaproject1.plato.Subscriptions();
        s.setStartDate(subscription.getStartDate());
        s.setEndDate(subscription.getEndDate());
        s.setActive(subscription.getActive());

        // user مش متحدد (زي JDBC version)
        s.setUserId(null);

        em.persist(s);
        em.getTransaction().commit();

        subscription.setId(s.getSubscriptionId());
        em.close();

        System.out.println("Subscription added with ID: " + subscription.getId());
    }

    @Override
    public Subscription getSubscriptionById(int id) {
        EntityManager em = JPAUtil.getEntityManager();

        javaproject1.plato.Subscriptions s =
                em.find(javaproject1.plato.Subscriptions.class, id);

        em.close();
        return s == null ? null : mapToDomain(s);
    }

    @Override
    public void updateSubscription(Subscription subscription) {
        EntityManager em = JPAUtil.getEntityManager();
        em.getTransaction().begin();

        javaproject1.plato.Subscriptions s = em.find(
                javaproject1.plato.Subscriptions.class,
                subscription.getId());

        if (s != null) {
            s.setStartDate(subscription.getStartDate());
            s.setEndDate(subscription.getEndDate());
            s.setActive(subscription.getActive());
        }

        em.getTransaction().commit();
        em.close();
    }

    @Override
    public void deleteSubscription(int id) {
        EntityManager em = JPAUtil.getEntityManager();
        em.getTransaction().begin();

        javaproject1.plato.Subscriptions s =
                em.find(javaproject1.plato.Subscriptions.class, id);

        if (s != null) em.remove(s);

        em.getTransaction().commit();
        em.close();
    }

    @Override
    public List<Subscription> getAllSubscriptions() {
        EntityManager em = JPAUtil.getEntityManager();

        List<javaproject1.plato.Subscriptions> list =
                em.createQuery("SELECT s FROM Subscriptions s",
                        javaproject1.plato.Subscriptions.class)
                        .getResultList();

        em.close();

        List<Subscription> result = new ArrayList<>();
        for (var s : list) result.add(mapToDomain(s));

        return result;
    }

    // ================= Mapping =================

    private Subscription mapToDomain(javaproject1.plato.Subscriptions s) {

        Subscription sub = new Subscription(
                s.getStartDate(),
                s.getEndDate()
        );

        sub.setId(s.getSubscriptionId());
        sub.setActive(s.getActive());

        return sub;
    }
}