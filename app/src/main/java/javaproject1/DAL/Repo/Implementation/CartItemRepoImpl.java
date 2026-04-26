package javaproject1.DAL.Repo.Implementation;

import javaproject1.DAL.Entity.CartItem;
import javaproject1.DAL.Entity.MenuItem;
import javaproject1.DAL.Repo.abstraction.ICartItemRepo;
import javaproject1.plato.JPAUtil;

import javax.persistence.EntityManager;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class CartItemRepoImpl implements ICartItemRepo {

    @Override
    public void addCartItem(CartItem cartItem) {
        addCartItemWithCartId(cartItem, cartItem.getCartItemID());
    }

    public void addCartItemWithCartId(CartItem cartItem, int cartId) {
        EntityManager em = JPAUtil.getEntityManager();
        em.getTransaction().begin();

        javaproject1.plato.CartItem ci = new javaproject1.plato.CartItem();
        ci.setQuantity(cartItem.getQuantity());
        ci.setSubPrice(BigDecimal.valueOf(cartItem.getSubPrice()));

        javaproject1.plato.Cart cart = em.find(javaproject1.plato.Cart.class, cartId);
        ci.setCartId(cart);

        if (cartItem.getMenuItem() != null && cartItem.getMenuItem().getItemId() != null) {
            javaproject1.plato.MenuItems mi = em.find(
                    javaproject1.plato.MenuItems.class,
                    Integer.parseInt(cartItem.getMenuItem().getItemId()));
            ci.setMenuItemId(mi);
        }

        em.persist(ci);
        em.getTransaction().commit();
        cartItem.setCartItemID(ci.getCartItemId());
        em.close();
        System.out.println("CartItem added with ID: " + cartItem.getCartItemID());
    }

    @Override
    public CartItem getCartItemById(int id) {
        EntityManager em = JPAUtil.getEntityManager();
        javaproject1.plato.CartItem ci = em.find(javaproject1.plato.CartItem.class, id);
        em.close();
        return ci == null ? null : mapToDomain(ci);
    }

    @Override
    public void updateCartItem(CartItem cartItem) {
        EntityManager em = JPAUtil.getEntityManager();
        em.getTransaction().begin();

        javaproject1.plato.CartItem ci = em.find(
                javaproject1.plato.CartItem.class, cartItem.getCartItemID());
        if (ci != null) {
            ci.setQuantity(cartItem.getQuantity());
            ci.setSubPrice(BigDecimal.valueOf(cartItem.getSubPrice()));
            em.merge(ci);
        }

        em.getTransaction().commit();
        em.close();
    }

    @Override
    public void deleteCartItem(int id) {
        EntityManager em = JPAUtil.getEntityManager();
        em.getTransaction().begin();
        javaproject1.plato.CartItem ci = em.find(javaproject1.plato.CartItem.class, id);
        if (ci != null) em.remove(ci);
        em.getTransaction().commit();
        em.close();
        System.out.println("CartItem deleted with ID: " + id);
    }

    @Override
    public List<CartItem> getAllCartItems() {
        EntityManager em = JPAUtil.getEntityManager();
        List<javaproject1.plato.CartItem> jpaList = em
                .createQuery("SELECT c FROM CartItem c", javaproject1.plato.CartItem.class)
                .getResultList();
        em.close();

        List<CartItem> result = new ArrayList<>();
        for (javaproject1.plato.CartItem ci : jpaList) result.add(mapToDomain(ci));
        return result;
    }

    private CartItem mapToDomain(javaproject1.plato.CartItem ci) {
        CartItem domain = new CartItem();
        domain.setCartItemID(ci.getCartItemId());
        domain.setQuantity(ci.getQuantity());
        domain.setSubPrice(ci.getSubPrice() != null ? ci.getSubPrice().doubleValue() : 0.0);

        if (ci.getMenuItemId() != null) {
            MenuItem mi = new MenuItem();
            mi.setItemId(String.valueOf(ci.getMenuItemId().getId()));
            mi.setName(ci.getMenuItemId().getName());
            mi.setPrice(ci.getMenuItemId().getPrice() != null ?
                    ci.getMenuItemId().getPrice().doubleValue() : 0.0);
            mi.setDescription(ci.getMenuItemId().getDescription());
            mi.setCategory(ci.getMenuItemId().getCategory());
            mi.setImagePath(ci.getMenuItemId().getImagePath());
            domain.setMenuItem(mi);
        }
        return domain;
    }
}