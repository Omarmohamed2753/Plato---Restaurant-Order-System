package javaproject1.DAL.Repo.Implementation;

import javaproject1.DAL.Entity.*;
import javaproject1.DAL.Enums.OrderStatus;
import javaproject1.DAL.Repo.abstraction.IOrderRepo;
import javaproject1.DAL.Repo.abstraction.IUserRepo;
import javaproject1.plato.JPAUtil;

import javax.persistence.EntityManager;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class OrderRepoImpl implements IOrderRepo {

    private final IUserRepo userRepo;

    public OrderRepoImpl(IUserRepo userRepo) {
        this.userRepo = userRepo;
    }

    @Override
    public void addOrder(Order order) {
        EntityManager em = JPAUtil.getEntityManager();
        em.getTransaction().begin();

        javaproject1.plato.Orders o = new javaproject1.plato.Orders();
        o.setTotalAmount(BigDecimal.valueOf(order.getTotalAmount()));
        o.setStatus(order.getStatus() != null ? order.getStatus().name() : "PENDING");
        o.setOrderDate(order.getOrderDate());

        if (order.getUser() != null && order.getUser().getId() != null) {
            javaproject1.plato.Users u = em.find(
                    javaproject1.plato.Users.class,
                    Integer.parseInt(order.getUser().getId()));
            o.setUserId(u);
        }
        if (order.getRestaurant() != null && order.getRestaurant().getRestaurantId() != null) {
            javaproject1.plato.Restaurants r = em.find(
                    javaproject1.plato.Restaurants.class,
                    Integer.parseInt(order.getRestaurant().getRestaurantId()));
            o.setRestaurantId(r);
        }
        if (order.getDeliveryAddress() != null && order.getDeliveryAddress().getId() != null) {
            javaproject1.plato.Address a = em.find(
                    javaproject1.plato.Address.class,
                    Integer.parseInt(order.getDeliveryAddress().getId()));
            o.setAddressId(a);
        }
        if (order.getPayment() != null && order.getPayment().getPaymentId() != null) {
            javaproject1.plato.Payments p = em.find(
                    javaproject1.plato.Payments.class, order.getPayment().getPaymentId());
            o.setPaymentId(p);
        }
        if (order.getDelivery() != null && order.getDelivery().getDeliveryId() != null) {
            javaproject1.plato.Delivery d = em.find(
                    javaproject1.plato.Delivery.class, order.getDelivery().getDeliveryId());
            o.setDeliveryId(d);
        }

        em.persist(o);
        em.getTransaction().commit();
        order.setOrderId(String.valueOf(o.getOrderId()));
        em.close();
        System.out.println("Order added with ID: " + order.getOrderId());
    }

    @Override
    public Order getOrderById(int id) {
        EntityManager em = JPAUtil.getEntityManager();
        javaproject1.plato.Orders o = em.find(javaproject1.plato.Orders.class, id);
        em.close();
        return o == null ? null : mapToDomain(o);
    }

    @Override
    public void updateOrder(Order order) {
        EntityManager em = JPAUtil.getEntityManager();
        em.getTransaction().begin();

        javaproject1.plato.Orders o = em.find(
                javaproject1.plato.Orders.class, Integer.parseInt(order.getOrderId()));
        if (o != null) {
            o.setTotalAmount(BigDecimal.valueOf(order.getTotalAmount()));
            o.setStatus(order.getStatus() != null ? order.getStatus().name() : "PENDING");
            o.setOrderDate(order.getOrderDate());

            // Also update the delivery link if it changed
            if (order.getDelivery() != null && order.getDelivery().getDeliveryId() != null) {
                javaproject1.plato.Delivery d = em.find(
                        javaproject1.plato.Delivery.class, order.getDelivery().getDeliveryId());
                o.setDeliveryId(d);
            }

            em.merge(o);
        }

        em.getTransaction().commit();
        em.close();
    }

    @Override
    public void deleteOrder(int id) {
        EntityManager em = JPAUtil.getEntityManager();
        em.getTransaction().begin();
        javaproject1.plato.Orders o = em.find(javaproject1.plato.Orders.class, id);
        if (o != null) em.remove(o);
        em.getTransaction().commit();
        em.close();
        System.out.println("Order deleted with ID: " + id);
    }

    @Override
    public List<Order> getAllOrders() {
        EntityManager em = JPAUtil.getEntityManager();
        List<javaproject1.plato.Orders> jpaList = em
                .createQuery("SELECT o FROM Orders o ORDER BY o.orderId DESC",
                        javaproject1.plato.Orders.class)
                .getResultList();
        em.close();

        List<Order> result = new ArrayList<>();
        for (javaproject1.plato.Orders o : jpaList) result.add(mapToDomain(o));
        System.out.println("Total orders retrieved: " + result.size());
        return result;
    }

    private Order mapToDomain(javaproject1.plato.Orders o) {
        Order domain = new Order();
        domain.setOrderId(String.valueOf(o.getOrderId()));
        domain.setTotalAmount(o.getTotalAmount() != null ?
                o.getTotalAmount().doubleValue() : 0.0);
        domain.setOrderDate(o.getOrderDate());

        // Status
        if (o.getStatus() != null) {
            try {
                domain.setStatus(OrderStatus.valueOf(o.getStatus().toUpperCase()));
            } catch (IllegalArgumentException e) {
                domain.setStatus(OrderStatus.PENDING);
            }
        }

        // User
        if (o.getUserId() != null) {
            User user = new User();
            user.setId(String.valueOf(o.getUserId().getUserId()));
            user.setName(o.getUserId().getName());
            user.setEmail(o.getUserId().getEmail());
            domain.setUser(user);
        }

        // Restaurant
        if (o.getRestaurantId() != null) {
            Restaurant r = new Restaurant();
            r.setRestaurantId(String.valueOf(o.getRestaurantId().getRestaurantId()));
            r.setName(o.getRestaurantId().getName());
            domain.setRestaurant(r);
        }

        // Address
        if (o.getAddressId() != null) {
            Address a = new Address();
            a.setId(String.valueOf(o.getAddressId().getId()));
            a.setStreet(o.getAddressId().getStreet());
            a.setCity(o.getAddressId().getCity());
            a.setBuildingNumber(o.getAddressId().getBuildingNumber());
            domain.setDeliveryAddress(a);
        }

        // Payment
        if (o.getPaymentId() != null) {
            Payment p = new Payment();
            p.setPaymentId(o.getPaymentId().getPaymentId());
            p.setAmount(o.getPaymentId().getAmount() != null ?
                    o.getPaymentId().getAmount().doubleValue() : 0.0);
            domain.setPayment(p);
        }

        // Delivery — FIXED: now loads the full employee from the employees table
        if (o.getDeliveryId() != null) {
            javaproject1.plato.Delivery jpaDelivery = o.getDeliveryId();
            Delivery d = new Delivery(jpaDelivery.getDeliveryId());
            d.setStatus(jpaDelivery.getStatus());
            d.setEstimatedDeliveryTime(jpaDelivery.getEstimatedDeliveryTime());

            // Load the full employee if delivery_person_id is set
            if (jpaDelivery.getDeliveryPersonId() != null) {
                EntityManager em2 = JPAUtil.getEntityManager();
                try {
                    javaproject1.plato.Employees empJpa = em2.find(
                            javaproject1.plato.Employees.class,
                            jpaDelivery.getDeliveryPersonId());
                    if (empJpa != null) {
                        Employee emp = new Employee();
                        emp.setId(String.valueOf(empJpa.getId()));
                        emp.setName(empJpa.getName() != null ? empJpa.getName() : "Unknown");
                        emp.setRole(empJpa.getRole() != null ? empJpa.getRole() : "Delivery");
                        emp.setPhoneNumber(empJpa.getPhoneNumber() != null ? empJpa.getPhoneNumber() : "");
                        emp.setExperiencesYear(empJpa.getExperiencesYear() != null ? empJpa.getExperiencesYear() : 0);
                        d.setDeliveryPerson(emp);
                        System.out.println("  Loaded delivery person: " + emp.getName()
                                + " for order #" + o.getOrderId());
                    }
                } finally {
                    em2.close();
                }
            }
            domain.setDelivery(d);
        }

        // Order items
        List<CartItem> items = new ArrayList<>();
        if (o.getOrderItemsSet() != null) {
            for (javaproject1.plato.OrderItems oi : o.getOrderItemsSet()) {
                if (oi.getCartItemId() != null) {
                    CartItem ci = new CartItem();
                    ci.setCartItemID(oi.getCartItemId().getCartItemId());
                    ci.setQuantity(oi.getCartItemId().getQuantity());
                    ci.setSubPrice(oi.getCartItemId().getSubPrice() != null ?
                            oi.getCartItemId().getSubPrice().doubleValue() : 0.0);
                    if (oi.getCartItemId().getMenuItemId() != null) {
                        MenuItem mi = new MenuItem();
                        mi.setItemId(String.valueOf(oi.getCartItemId().getMenuItemId().getId()));
                        mi.setName(oi.getCartItemId().getMenuItemId().getName());
                        mi.setPrice(oi.getCartItemId().getMenuItemId().getPrice() != null ?
                                oi.getCartItemId().getMenuItemId().getPrice().doubleValue() : 0.0);
                        mi.setCategory(oi.getCartItemId().getMenuItemId().getCategory());
                        mi.setImagePath(oi.getCartItemId().getMenuItemId().getImagePath());
                        ci.setMenuItem(mi);
                    }
                    items.add(ci);
                }
            }
        }
        domain.setItems(items);
        return domain;
    }
}