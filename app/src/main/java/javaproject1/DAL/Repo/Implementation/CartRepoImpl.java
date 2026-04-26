package javaproject1.DAL.Repo.Implementation;

import javaproject1.DAL.Entity.Cart;
import javaproject1.DAL.Entity.CartItem;
import javaproject1.DAL.Entity.MenuItem;
import javaproject1.DAL.Repo.abstraction.ICartRepo;
import javaproject1.plato.JPAUtil;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class CartRepoImpl implements ICartRepo {

    @Override
    public void addCart(Cart cart) {
        EntityManager em = JPAUtil.getEntityManager();
        em.getTransaction().begin();

        javaproject1.plato.Cart c = new javaproject1.plato.Cart();
        em.persist(c);
        em.getTransaction().commit();
        cart.setCartId(c.getCartId());
        em.close();
        System.out.println("Cart created with ID: " + cart.getCartId());
    }

    @Override
    public Cart getCartById(int id) {
        EntityManager em = JPAUtil.getEntityManager();
        javaproject1.plato.Cart c = em.find(javaproject1.plato.Cart.class, id);
        em.close();
        return c == null ? new Cart() : mapToDomain(c);
    }

    @Override
    public void updateCart(Cart cart) {
        EntityManager em = JPAUtil.getEntityManager();
        em.getTransaction().begin();

        for (CartItem item : cart.getItems()) {
            if (item.getCartItemID() > 0) {
                javaproject1.plato.CartItem ci = em.find(
                        javaproject1.plato.CartItem.class, item.getCartItemID());
                if (ci != null) {
                    ci.setQuantity(item.getQuantity());
                    ci.setSubPrice(java.math.BigDecimal.valueOf(item.getSubPrice()));
                    em.merge(ci);
                }
            }
        }

        em.getTransaction().commit();
        em.close();
    }

    @Override
    public void deleteCart(int id) {
        EntityManager em = JPAUtil.getEntityManager();
        em.getTransaction().begin();
        javaproject1.plato.Cart c = em.find(javaproject1.plato.Cart.class, id);
        if (c != null) em.remove(c);
        em.getTransaction().commit();
        em.close();
        System.out.println("Cart deleted with ID: " + id);
    }

    @Override
    public List<Cart> getAllCarts() {
        EntityManager em = JPAUtil.getEntityManager();
        List<javaproject1.plato.Cart> jpaList = em
                .createQuery("SELECT c FROM Cart c", javaproject1.plato.Cart.class)
                .getResultList();
        em.close();

        List<Cart> result = new ArrayList<>();
        for (javaproject1.plato.Cart c : jpaList) result.add(mapToDomain(c));
        return result;
    }

    public Cart getOrCreateCartForUser(int userId) {
        EntityManager em = JPAUtil.getEntityManager();
        List<javaproject1.plato.Cart> existing = em
                .createQuery("SELECT c FROM Cart c WHERE c.userId.userId = :uid",
                        javaproject1.plato.Cart.class)
                .setParameter("uid", userId)
                .getResultList();
        em.close();

        if (!existing.isEmpty()) return mapToDomain(existing.get(0));

        // Create new cart
        Cart newCart = new Cart();
        addCart(newCart);
        return newCart;
    }

    private Cart mapToDomain(javaproject1.plato.Cart c) {
        Cart domain = new Cart();
        domain.setCartId(c.getCartId());

        List<CartItem> items = new ArrayList<>();
        if (c.getCartItemSet() != null) {
            for (javaproject1.plato.CartItem ci : c.getCartItemSet()) {
                CartItem item = new CartItem();
                item.setCartItemID(ci.getCartItemId());
                item.setQuantity(ci.getQuantity());
                item.setSubPrice(ci.getSubPrice() != null ? ci.getSubPrice().doubleValue() : 0.0);

                if (ci.getMenuItemId() != null) {
                    MenuItem mi = new MenuItem();
                    mi.setItemId(String.valueOf(ci.getMenuItemId().getId()));
                    mi.setName(ci.getMenuItemId().getName());
                    mi.setPrice(ci.getMenuItemId().getPrice() != null ?
                            ci.getMenuItemId().getPrice().doubleValue() : 0.0);
                    mi.setDescription(ci.getMenuItemId().getDescription());
                    mi.setCategory(ci.getMenuItemId().getCategory());
                    mi.setImagePath(ci.getMenuItemId().getImagePath());
                    item.setMenuItem(mi);
                }
                items.add(item);
            }
        }
        domain.setItems(items);
        return domain;
    }
}