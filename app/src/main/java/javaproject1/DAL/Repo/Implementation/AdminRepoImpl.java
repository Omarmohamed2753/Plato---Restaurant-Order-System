package javaproject1.DAL.Repo.Implementation;

import javaproject1.DAL.Entity.Admin;
import javaproject1.DAL.Entity.Restaurant;
import javaproject1.DAL.Repo.abstraction.IAdminRepo;
import javaproject1.plato.JPAUtil;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;

public class AdminRepoImpl implements IAdminRepo {

    @Override
    public void addAdmin(Admin admin) {
        EntityManager em = JPAUtil.getEntityManager();
        em.getTransaction().begin();

        javaproject1.plato.Admin a = new javaproject1.plato.Admin();
        a.setName(admin.getName());
        a.setAge(admin.getAge());
        a.setPhoneNumber(admin.getPhoneNumber());
        a.setEmail(admin.getEmail());
        a.setPassword(admin.getPassword());

        if (admin.getRestaurant() != null && admin.getRestaurant().getRestaurantId() != null) {
            javaproject1.plato.Restaurants r = em.find(
                    javaproject1.plato.Restaurants.class,
                    Integer.parseInt(admin.getRestaurant().getRestaurantId()));
            a.setRestaurantId(r);
        }

        em.persist(a);
        em.getTransaction().commit();
        admin.setId(String.valueOf(a.getId()));
        em.close();
        System.out.println("Admin added with ID: " + admin.getId());
    }

    @Override
    public Admin getAdminById(int id) {
        EntityManager em = JPAUtil.getEntityManager();
        javaproject1.plato.Admin a = em.find(javaproject1.plato.Admin.class, id);
        em.close();
        return a == null ? null : mapToDomain(a);
    }

    @Override
    public void updateAdmin(Admin admin) {
        EntityManager em = JPAUtil.getEntityManager();
        em.getTransaction().begin();

        javaproject1.plato.Admin a = em.find(
                javaproject1.plato.Admin.class, Integer.parseInt(admin.getId()));
        if (a != null) {
            a.setName(admin.getName());
            a.setAge(admin.getAge());
            a.setPhoneNumber(admin.getPhoneNumber());
            a.setEmail(admin.getEmail());
            a.setPassword(admin.getPassword());
            em.merge(a);
        }

        em.getTransaction().commit();
        em.close();
    }

    @Override
    public void deleteAdmin(int id) {
        EntityManager em = JPAUtil.getEntityManager();
        em.getTransaction().begin();
        javaproject1.plato.Admin a = em.find(javaproject1.plato.Admin.class, id);
        if (a != null) em.remove(a);
        em.getTransaction().commit();
        em.close();
    }

    @Override
    public List<Admin> getAllAdmins() {
        EntityManager em = JPAUtil.getEntityManager();
        List<javaproject1.plato.Admin> jpaList = em
                .createQuery("SELECT a FROM Admin a", javaproject1.plato.Admin.class)
                .getResultList();
        em.close();

        List<Admin> result = new ArrayList<>();
        for (javaproject1.plato.Admin a : jpaList) result.add(mapToDomain(a));
        return result;
    }

    private Admin mapToDomain(javaproject1.plato.Admin a) {
        Restaurant restaurant = null;
        if (a.getRestaurantId() != null) {
            restaurant = new Restaurant();
            restaurant.setRestaurantId(String.valueOf(a.getRestaurantId().getRestaurantId()));
            restaurant.setName(a.getRestaurantId().getName());
        }
        return new Admin(
                String.valueOf(a.getId()),
                a.getName(),
                a.getAge(),
                a.getPhoneNumber(),
                a.getEmail(),
                a.getPassword(),
                restaurant
        );
    }
}