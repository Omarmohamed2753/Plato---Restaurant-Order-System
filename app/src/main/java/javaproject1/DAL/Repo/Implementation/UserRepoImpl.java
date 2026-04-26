package javaproject1.DAL.Repo.Implementation;

import javaproject1.DAL.Entity.*;
import javaproject1.DAL.Repo.abstraction.IUserRepo;
import javaproject1.plato.JPAUtil;

import javax.persistence.EntityManager;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class UserRepoImpl implements IUserRepo {

    // ── CRUD ──────────────────────────────────────────────────────────────────

    @Override
    public void addUser(User user) {
        EntityManager em = JPAUtil.getEntityManager();
        em.getTransaction().begin();

        javaproject1.plato.Users u = new javaproject1.plato.Users();
        u.setName(user.getName());
        u.setAge(user.getAge());
        u.setPhone(user.getPhoneNumber());
        u.setEmail(user.getEmail());
        u.setPassword(user.getPassword());
        u.setIsElite(user.isElite());

        em.persist(u);

        // Create a cart row for the new user right here in the same transaction
        javaproject1.plato.Cart cart = new javaproject1.plato.Cart();
        cart.setUserId(u);
        em.persist(cart);

        em.getTransaction().commit();

        user.setId(String.valueOf(u.getUserId()));
        // Attach an empty in-memory cart so callers never see null
        Cart domainCart = new Cart();
        domainCart.setCartId(cart.getCartId());
        user.setCart(domainCart);

        em.close();
        System.out.println("User added with ID: " + user.getId()
                + " | Cart ID: " + cart.getCartId());
    }

    @Override
    public User getUserById(int id) {
        EntityManager em = JPAUtil.getEntityManager();
        javaproject1.plato.Users u = em.find(javaproject1.plato.Users.class, id);
        em.close();
        return u == null ? null : mapToDomain(u);
    }

    @Override
    public void updateUser(User user) {
        EntityManager em = JPAUtil.getEntityManager();
        em.getTransaction().begin();

        javaproject1.plato.Users u = em.find(
                javaproject1.plato.Users.class, Integer.parseInt(user.getId()));
        if (u != null) {
            u.setName(user.getName());
            u.setAge(user.getAge());
            u.setPhone(user.getPhoneNumber());
            u.setEmail(user.getEmail());
            u.setPassword(user.getPassword());
            u.setIsElite(user.isElite());
            em.merge(u);
        }

        em.getTransaction().commit();
        em.close();
    }

    @Override
    public void deleteUser(int id) {
        EntityManager em = JPAUtil.getEntityManager();
        em.getTransaction().begin();
        javaproject1.plato.Users u = em.find(javaproject1.plato.Users.class, id);
        if (u != null) em.remove(u);
        em.getTransaction().commit();
        em.close();
        System.out.println("User deleted with ID: " + id);
    }

    @Override
    public List<User> getAllUsers() {
        EntityManager em = JPAUtil.getEntityManager();
        List<javaproject1.plato.Users> jpaList = em
                .createQuery("SELECT u FROM Users u", javaproject1.plato.Users.class)
                .getResultList();
        em.close();

        List<User> result = new ArrayList<>();
        for (javaproject1.plato.Users u : jpaList) result.add(mapToDomain(u));
        return result;
    }

    @Override
    public User getUserByEmail(String email) {
        EntityManager em = JPAUtil.getEntityManager();
        List<javaproject1.plato.Users> results = em
                .createQuery("SELECT u FROM Users u WHERE u.email = :email",
                        javaproject1.plato.Users.class)
                .setParameter("email", email)
                .getResultList();
        em.close();
        return results.isEmpty() ? null : mapToDomain(results.get(0));
    }

    // ── Mapping ───────────────────────────────────────────────────────────────

    private User mapToDomain(javaproject1.plato.Users u) {
        User user = new User();
        user.setId(String.valueOf(u.getUserId()));
        user.setName(u.getName() != null ? u.getName() : "");
        user.setAge(u.getAge());
        user.setPhoneNumber(u.getPhone() != null ? u.getPhone() : "");
        user.setEmail(u.getEmail() != null ? u.getEmail() : "");
        user.setPassword(u.getPassword() != null ? u.getPassword() : "");
        user.setElite(Boolean.TRUE.equals(u.getIsElite()));

        user.setAddresses(mapAddresses(u));
        user.setOrders(mapOrders(u));
        user.setSubscription(mapSubscription(u));
        user.setCart(mapCart(u));

        System.out.println("DEBUG UserRepo - Mapped user ID: " + user.getId());
        return user;
    }

    // ── Cart ──────────────────────────────────────────────────────────────────

    private Cart mapCart(javaproject1.plato.Users u) {
        if (u.getCartSet() == null || u.getCartSet().isEmpty()) {
            // No cart yet — return an empty placeholder (addUser creates one on registration)
            Cart empty = new Cart();
            System.out.println("DEBUG UserRepo - No cart found for user " + u.getUserId());
            return empty;
        }

        javaproject1.plato.Cart jpaCart = u.getCartSet().iterator().next();
        Cart cart = new Cart();
        cart.setCartId(jpaCart.getCartId());

        List<CartItem> activeItems = new ArrayList<>();
        if (jpaCart.getCartItemSet() != null) {
            for (javaproject1.plato.CartItem ci : jpaCart.getCartItemSet()) {
                // Skip items that are already part of a completed order
                if (isAlreadyOrdered(ci)) continue;

                CartItem item = buildCartItem(ci);
                activeItems.add(item);
            }
        }
        cart.setItems(activeItems);
        System.out.println("DEBUG UserRepo - Loaded " + activeItems.size()
                + " active (non-ordered) items in cart " + jpaCart.getCartId());
        return cart;
    }

    /**
     * Returns true when the cart_item is already referenced in order_items,
     * meaning it belongs to a placed order and must not re-appear in the cart.
     */
    private boolean isAlreadyOrdered(javaproject1.plato.CartItem ci) {
        if (ci.getOrderItemsSet() == null) return false;
        return !ci.getOrderItemsSet().isEmpty();
    }

    private CartItem buildCartItem(javaproject1.plato.CartItem ci) {
        CartItem item = new CartItem();
        item.setCartItemID(ci.getCartItemId());
        item.setQuantity(ci.getQuantity());
        item.setSubPrice(ci.getSubPrice() != null ? ci.getSubPrice().doubleValue() : 0.0);

        if (ci.getMenuItemId() != null) {
            MenuItem mi = new MenuItem();
            mi.setItemId(String.valueOf(ci.getMenuItemId().getId()));
            mi.setName(ci.getMenuItemId().getName() != null ? ci.getMenuItemId().getName() : "");
            mi.setPrice(ci.getMenuItemId().getPrice() != null
                    ? ci.getMenuItemId().getPrice().doubleValue() : 0.0);
            mi.setDescription(ci.getMenuItemId().getDescription() != null
                    ? ci.getMenuItemId().getDescription() : "");
            mi.setCategory(ci.getMenuItemId().getCategory() != null
                    ? ci.getMenuItemId().getCategory() : "");
            mi.setImagePath(ci.getMenuItemId().getImagePath() != null
                    ? ci.getMenuItemId().getImagePath() : "");
            item.setMenuItem(mi);
        }
        return item;
    }

    // ── Orders ────────────────────────────────────────────────────────────────

    private List<Order> mapOrders(javaproject1.plato.Users u) {
        List<Order> orders = new ArrayList<>();
        if (u.getOrdersSet() == null) return orders;

        for (javaproject1.plato.Orders o : u.getOrdersSet()) {
            Order order = new Order();
            order.setOrderId(String.valueOf(o.getOrderId()));
            order.setTotalAmount(o.getTotalAmount() != null
                    ? o.getTotalAmount().doubleValue() : 0.0);
            order.setOrderDate(o.getOrderDate());

            if (o.getStatus() != null) {
                try {
                    order.setStatus(javaproject1.DAL.Enums.OrderStatus
                            .valueOf(o.getStatus().toUpperCase()));
                } catch (IllegalArgumentException ex) {
                    order.setStatus(javaproject1.DAL.Enums.OrderStatus.PENDING);
                }
            }
            orders.add(order);
        }
        System.out.println("DEBUG UserRepo - Loaded " + orders.size()
                + " orders for user " + u.getUserId());
        return orders;
    }

    // ── Addresses ─────────────────────────────────────────────────────────────

    private List<Address> mapAddresses(javaproject1.plato.Users u) {
        List<Address> addresses = new ArrayList<>();
        if (u.getAddressSet() == null) return addresses;

        for (javaproject1.plato.Address a : u.getAddressSet()) {
            Address domain = new Address();
            domain.setId(String.valueOf(a.getId()));
            domain.setStreet(a.getStreet() != null ? a.getStreet() : "");
            domain.setCity(a.getCity() != null ? a.getCity() : "");
            domain.setBuildingNumber(a.getBuildingNumber());
            addresses.add(domain);
        }
        System.out.println("DEBUG UserRepo - Loaded " + addresses.size()
                + " addresses for user " + u.getUserId());
        return addresses;
    }

    // ── Subscription ──────────────────────────────────────────────────────────

    private Subscription mapSubscription(javaproject1.plato.Users u) {
        if (u.getSubscriptionsSet() == null) return null;

        return u.getSubscriptionsSet().stream()
                .filter(s -> Boolean.TRUE.equals(s.getActive()))
                .findFirst()
                .map(s -> {
                    Subscription sub = new Subscription(s.getStartDate(), s.getEndDate());
                    sub.setId(s.getSubscriptionId());
                    sub.setActive(Boolean.TRUE.equals(s.getActive()));
                    System.out.println("DEBUG UserRepo - Loaded subscription for user "
                            + u.getUserId() + ", active: " + sub.isActive());
                    return sub;
                })
                .orElseGet(() -> {
                    System.out.println("DEBUG UserRepo - No active subscription for user "
                            + u.getUserId());
                    return null;
                });
    }
}