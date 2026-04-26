package javaproject1.DAL.Repo.Implementation;

import javaproject1.DAL.Entity.Address;
import javaproject1.DAL.Repo.abstraction.IAddressRepo;
import javaproject1.plato.JPAUtil;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;

public class AddressRepoImpl implements IAddressRepo {

    @Override
    public void addAddress(Address address) {
        EntityManager em = JPAUtil.getEntityManager();
        em.getTransaction().begin();

        javaproject1.plato.Address a = new javaproject1.plato.Address();
        a.setStreet(address.getStreet());
        a.setCity(address.getCity());
        a.setBuildingNumber(address.getBuildingNumber());

        em.persist(a);
        em.getTransaction().commit();
        address.setId(String.valueOf(a.getId()));
        em.close();
        System.out.println("Address added with ID: " + address.getId());
    }

    @Override
    public Address getAddressById(int id) {
        EntityManager em = JPAUtil.getEntityManager();
        javaproject1.plato.Address a = em.find(javaproject1.plato.Address.class, id);
        em.close();
        return a == null ? null : mapToDomain(a);
    }

    @Override
    public void updateAddress(Address address) {
        EntityManager em = JPAUtil.getEntityManager();
        em.getTransaction().begin();

        javaproject1.plato.Address a = em.find(
                javaproject1.plato.Address.class, Integer.parseInt(address.getId()));
        if (a != null) {
            a.setStreet(address.getStreet());
            a.setCity(address.getCity());
            a.setBuildingNumber(address.getBuildingNumber());
            em.merge(a);
        }

        em.getTransaction().commit();
        em.close();
    }

    @Override
    public void deleteAddress(int id) {
        EntityManager em = JPAUtil.getEntityManager();
        em.getTransaction().begin();
        javaproject1.plato.Address a = em.find(javaproject1.plato.Address.class, id);
        if (a != null) em.remove(a);
        em.getTransaction().commit();
        em.close();
    }

    @Override
    public List<Address> getAllAddresses() {
        EntityManager em = JPAUtil.getEntityManager();
        List<javaproject1.plato.Address> jpaList = em
                .createQuery("SELECT a FROM Address a", javaproject1.plato.Address.class)
                .getResultList();
        em.close();

        List<Address> result = new ArrayList<>();
        for (javaproject1.plato.Address a : jpaList) result.add(mapToDomain(a));
        return result;
    }

    private Address mapToDomain(javaproject1.plato.Address a) {
        Address domain = new Address();
        domain.setId(String.valueOf(a.getId()));
        domain.setStreet(a.getStreet() != null ? a.getStreet() : "");
        domain.setCity(a.getCity() != null ? a.getCity() : "");
        domain.setBuildingNumber(a.getBuildingNumber());
        return domain;
    }
}