package javaproject1.DAL.Repo.Implementation;

import javaproject1.DAL.Entity.MenuItem;
import javaproject1.DAL.Repo.abstraction.IMenuItemRepo;
import javaproject1.plato.JPAUtil;

import javax.persistence.EntityManager;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class MenuItemRepoImpl implements IMenuItemRepo {

    @Override
    public void addMenuItem(MenuItem item) {
        EntityManager em = JPAUtil.getEntityManager();
        em.getTransaction().begin();

        javaproject1.plato.MenuItems mi = new javaproject1.plato.MenuItems();
        mi.setName(item.getName());
        mi.setPrice(BigDecimal.valueOf(item.getPrice()));
        mi.setDescription(item.getDescription());
        mi.setCategory(item.getCategory());
        mi.setImagePath(item.getImagePath());

        em.persist(mi);
        em.getTransaction().commit();
        item.setItemId(String.valueOf(mi.getId()));
        em.close();
        System.out.println("MenuItem added with ID: " + item.getItemId());
    }

    public void addMenuItemWithMenuId(MenuItem item, String menuId) {
        EntityManager em = JPAUtil.getEntityManager();
        em.getTransaction().begin();

        javaproject1.plato.Menu menu = em.find(javaproject1.plato.Menu.class, menuId);

        javaproject1.plato.MenuItems mi = new javaproject1.plato.MenuItems();
        mi.setName(item.getName());
        mi.setPrice(BigDecimal.valueOf(item.getPrice()));
        mi.setDescription(item.getDescription());
        mi.setCategory(item.getCategory());
        mi.setImagePath(item.getImagePath());
        mi.setMenuId(menu);

        em.persist(mi);
        em.getTransaction().commit();
        item.setItemId(String.valueOf(mi.getId()));
        em.close();
        System.out.println("MenuItem added with ID: " + item.getItemId() + " to menu: " + menuId);
    }

    @Override
    public MenuItem getMenuItemById(int id) {
        EntityManager em = JPAUtil.getEntityManager();
        javaproject1.plato.MenuItems mi = em.find(javaproject1.plato.MenuItems.class, id);
        em.close();
        return mi == null ? null : mapToDomain(mi);
    }

    @Override
    public void updateMenuItem(MenuItem item) {
        EntityManager em = JPAUtil.getEntityManager();
        em.getTransaction().begin();

        javaproject1.plato.MenuItems mi = em.find(
                javaproject1.plato.MenuItems.class, Integer.parseInt(item.getItemId()));
        if (mi != null) {
            mi.setName(item.getName());
            mi.setPrice(BigDecimal.valueOf(item.getPrice()));
            mi.setDescription(item.getDescription());
            mi.setCategory(item.getCategory());
            mi.setImagePath(item.getImagePath());
            em.merge(mi);
            System.out.println("MenuItem updated: " + item.getItemId());
        }

        em.getTransaction().commit();
        em.close();
    }

    @Override
    public void deleteMenuItem(int id) {
        EntityManager em = JPAUtil.getEntityManager();
        em.getTransaction().begin();
        javaproject1.plato.MenuItems mi = em.find(javaproject1.plato.MenuItems.class, id);
        if (mi != null) em.remove(mi);
        em.getTransaction().commit();
        em.close();
        System.out.println("MenuItem deleted with ID: " + id);
    }

    @Override
    public List<MenuItem> getAllMenuItems() {
        EntityManager em = JPAUtil.getEntityManager();
        List<javaproject1.plato.MenuItems> jpaList = em
                .createQuery("SELECT m FROM MenuItems m", javaproject1.plato.MenuItems.class)
                .getResultList();
        em.close();

        List<MenuItem> result = new ArrayList<>();
        for (javaproject1.plato.MenuItems mi : jpaList) result.add(mapToDomain(mi));
        return result;
    }

    private MenuItem mapToDomain(javaproject1.plato.MenuItems mi) {
        MenuItem domain = new MenuItem();
        domain.setItemId(String.valueOf(mi.getId()));
        domain.setName(mi.getName());
        domain.setPrice(mi.getPrice() != null ? mi.getPrice().doubleValue() : 0.0);
        domain.setDescription(mi.getDescription());
        domain.setCategory(mi.getCategory());
        domain.setImagePath(mi.getImagePath());
        return domain;
    }
}