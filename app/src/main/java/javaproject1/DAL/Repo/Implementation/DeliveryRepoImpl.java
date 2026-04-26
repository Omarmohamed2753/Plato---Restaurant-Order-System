package javaproject1.DAL.Repo.Implementation;

import javaproject1.DAL.Entity.Delivery;
import javaproject1.DAL.Entity.Employee;
import javaproject1.DAL.Repo.abstraction.IDeliveryRepo;
import javaproject1.plato.JPAUtil;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;

public class DeliveryRepoImpl implements IDeliveryRepo {

    @Override
    public void addDelivery(Delivery delivery) {
        EntityManager em = JPAUtil.getEntityManager();
        em.getTransaction().begin();

        javaproject1.plato.Delivery d = new javaproject1.plato.Delivery();
        d.setDeliveryId(delivery.getDeliveryId());
        d.setStatus(delivery.getStatus());

        if (delivery.getDeliveryPerson() != null && delivery.getDeliveryPerson().getId() != null) {
            d.setDeliveryPersonId(Integer.parseInt(delivery.getDeliveryPerson().getId()));
        }
        if (delivery.getEstimatedDeliveryTime() != null) {
            d.setEstimatedDeliveryTime(delivery.getEstimatedDeliveryTime());
        }

        em.persist(d);
        em.getTransaction().commit();
        em.close();
    }

    @Override
    public Delivery getDeliveryById(int id) {
        EntityManager em = JPAUtil.getEntityManager();
        javaproject1.plato.Delivery d = em.find(javaproject1.plato.Delivery.class,
                String.valueOf(id));
        em.close();
        return d == null ? null : mapToDomain(d);
    }

    @Override
    public void updateDelivery(Delivery delivery) {
        EntityManager em = JPAUtil.getEntityManager();
        em.getTransaction().begin();

        javaproject1.plato.Delivery d = em.find(
                javaproject1.plato.Delivery.class, delivery.getDeliveryId());
        if (d != null) {
            d.setStatus(delivery.getStatus());
            if (delivery.getDeliveryPerson() != null && delivery.getDeliveryPerson().getId() != null) {
                d.setDeliveryPersonId(Integer.parseInt(delivery.getDeliveryPerson().getId()));
            }
            if (delivery.getEstimatedDeliveryTime() != null) {
                d.setEstimatedDeliveryTime(delivery.getEstimatedDeliveryTime());
            }
            em.merge(d);
        }

        em.getTransaction().commit();
        em.close();
    }

    @Override
    public void deleteDelivery(int id) {
        EntityManager em = JPAUtil.getEntityManager();
        em.getTransaction().begin();
        javaproject1.plato.Delivery d = em.find(javaproject1.plato.Delivery.class,
                String.valueOf(id));
        if (d != null) em.remove(d);
        em.getTransaction().commit();
        em.close();
    }

    @Override
    public List<Delivery> getAllDeliveries() {
        EntityManager em = JPAUtil.getEntityManager();
        List<javaproject1.plato.Delivery> jpaList = em
                .createQuery("SELECT d FROM Delivery d", javaproject1.plato.Delivery.class)
                .getResultList();
        em.close();

        List<Delivery> result = new ArrayList<>();
        for (javaproject1.plato.Delivery d : jpaList) result.add(mapToDomain(d));
        return result;
    }

    /**
     * Loads the full Employee from the employees table when delivery_person_id is set.
     * This fixes the "Not Assigned" display bug caused by only storing the ID stub.
     */
    private Delivery mapToDomain(javaproject1.plato.Delivery d) {
        Delivery domain = new Delivery(d.getDeliveryId());
        domain.setStatus(d.getStatus());
        domain.setEstimatedDeliveryTime(d.getEstimatedDeliveryTime());

        if (d.getDeliveryPersonId() != null) {
            // Load the full employee record from DB so name/phone are available
            EntityManager em = JPAUtil.getEntityManager();
            try {
                javaproject1.plato.Employees empJpa =
                        em.find(javaproject1.plato.Employees.class, d.getDeliveryPersonId());
                if (empJpa != null) {
                    Employee emp = new Employee();
                    emp.setId(String.valueOf(empJpa.getId()));
                    emp.setName(empJpa.getName() != null ? empJpa.getName() : "Unknown");
                    emp.setRole(empJpa.getRole() != null ? empJpa.getRole() : "Delivery");
                    emp.setPhoneNumber(empJpa.getPhoneNumber() != null ? empJpa.getPhoneNumber() : "");
                    emp.setExperiencesYear(empJpa.getExperiencesYear() != null ? empJpa.getExperiencesYear() : 0);
                    domain.setDeliveryPerson(emp);
                } else {
                    // Fallback: at least store the ID so it's not null
                    Employee emp = new Employee();
                    emp.setId(String.valueOf(d.getDeliveryPersonId()));
                    emp.setName("Employee #" + d.getDeliveryPersonId());
                    domain.setDeliveryPerson(emp);
                }
            } finally {
                em.close();
            }
        }
        return domain;
    }
}