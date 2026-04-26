package javaproject1.DAL.Repo.Implementation;

import javaproject1.DAL.Entity.Payment;
import javaproject1.DAL.Enums.PaymentM;
import javaproject1.DAL.Repo.abstraction.IPaymentRepo;
import javaproject1.plato.JPAUtil;

import javax.persistence.EntityManager;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class PaymentRepoImpl implements IPaymentRepo {

    @Override
    public void addPayment(Payment payment) {
        EntityManager em = JPAUtil.getEntityManager();
        em.getTransaction().begin();

        javaproject1.plato.Payments p = new javaproject1.plato.Payments();
        p.setPaymentId(payment.getPaymentId());
        p.setOrderId(payment.getOrderId());
        p.setAmount(BigDecimal.valueOf(payment.getAmount()));
        p.setMethod(payment.getPaymentMethod() != null ?
                payment.getPaymentMethod().toString() : "Cash");
        p.setStatus(payment.getStatus());
        p.setPaymentDate(payment.getTransactionDate());

        em.persist(p);
        em.getTransaction().commit();
        em.close();
    }

    @Override
    public Payment getPaymentById(int id) {
        EntityManager em = JPAUtil.getEntityManager();
        // payment_id is a String in JPA entity
        List<javaproject1.plato.Payments> result = em
                .createQuery("SELECT p FROM Payments p WHERE p.paymentId = :pid",
                        javaproject1.plato.Payments.class)
                .setParameter("pid", String.valueOf(id))
                .getResultList();
        em.close();

        return result.isEmpty() ? null : mapToDomain(result.get(0));
    }

    @Override
    public void updatePayment(Payment payment) {
        EntityManager em = JPAUtil.getEntityManager();
        em.getTransaction().begin();

        javaproject1.plato.Payments p = em.find(
                javaproject1.plato.Payments.class, payment.getPaymentId());
        if (p != null) {
            p.setOrderId(payment.getOrderId());
            p.setAmount(BigDecimal.valueOf(payment.getAmount()));
            p.setMethod(payment.getPaymentMethod() != null ?
                    payment.getPaymentMethod().toString() : "Cash");
            p.setStatus(payment.getStatus());
            p.setPaymentDate(payment.getTransactionDate());
            em.merge(p);
        }

        em.getTransaction().commit();
        em.close();
    }

    @Override
    public void deletePayment(int id) {
        EntityManager em = JPAUtil.getEntityManager();
        em.getTransaction().begin();
        javaproject1.plato.Payments p = em.find(
                javaproject1.plato.Payments.class, String.valueOf(id));
        if (p != null) em.remove(p);
        em.getTransaction().commit();
        em.close();
    }

    @Override
    public List<Payment> getAllPayments() {
        EntityManager em = JPAUtil.getEntityManager();
        List<javaproject1.plato.Payments> jpaList = em
                .createQuery("SELECT p FROM Payments p", javaproject1.plato.Payments.class)
                .getResultList();
        em.close();

        List<Payment> result = new ArrayList<>();
        for (javaproject1.plato.Payments p : jpaList) result.add(mapToDomain(p));
        return result;
    }

    private Payment mapToDomain(javaproject1.plato.Payments p) {
        Payment domain = new Payment();
        domain.setPaymentId(p.getPaymentId());
        domain.setOrderId(p.getOrderId());
        domain.setAmount(p.getAmount() != null ? p.getAmount().doubleValue() : 0.0);
        domain.setStatus(p.getStatus());
        domain.setTransactionDate(p.getPaymentDate());

        try {
            domain.setPaymentMethod(PaymentM.valueOf(p.getMethod()));
        } catch (Exception e) {
            domain.setPaymentMethod(PaymentM.Cash);
        }
        return domain;
    }
}