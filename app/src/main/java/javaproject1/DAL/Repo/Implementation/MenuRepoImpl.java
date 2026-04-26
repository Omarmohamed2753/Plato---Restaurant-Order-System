package javaproject1.DAL.Repo.Implementation;

import javaproject1.DAL.Entity.Menu;
import javaproject1.DAL.Repo.abstraction.IMenuRepo;
import javaproject1.plato.JPAUtil;

import javax.persistence.EntityManager;

public class MenuRepoImpl implements IMenuRepo {

    @Override
    public void addMenu(Menu menu) {
        EntityManager em = JPAUtil.getEntityManager();
        em.getTransaction().begin();

        javaproject1.plato.Menu m = new javaproject1.plato.Menu();
        m.setMenuId(menu.getMenuId());

        em.persist(m);
        em.getTransaction().commit();
        em.close();
        System.out.println("Menu added with ID: " + menu.getMenuId());
    }

    @Override
    public Menu getMenuById(int id) {
        EntityManager em = JPAUtil.getEntityManager();
        javaproject1.plato.Menu m = em.find(
                javaproject1.plato.Menu.class, String.valueOf(id));
        em.close();

        if (m == null) return null;
        Menu domain = new Menu();
        domain.setMenuId(m.getMenuId());
        return domain;
    }

    @Override
    public void updateMenu(Menu menu) {
        // Menu only has menu_id as PK — nothing meaningful to update
        System.out.println("Menu updated: " + menu.getMenuId());
    }

    @Override
    public void deleteMenu(int id) {
        EntityManager em = JPAUtil.getEntityManager();
        em.getTransaction().begin();
        javaproject1.plato.Menu m = em.find(
                javaproject1.plato.Menu.class, String.valueOf(id));
        if (m != null) em.remove(m);
        em.getTransaction().commit();
        em.close();
        System.out.println("Menu deleted with ID: " + id);
    }
}