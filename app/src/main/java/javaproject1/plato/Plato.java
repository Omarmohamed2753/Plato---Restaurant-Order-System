package javaproject1.plato;

import java.math.BigDecimal;
import java.util.List;
import javax.persistence.*;

public class Plato {

    static EntityManagerFactory emf =
            Persistence.createEntityManagerFactory("platoPU");

    public static Restaurants insertRestaurant(String name, String address) {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();

        Restaurants r = new Restaurants();
        r.setName(name);
        r.setAddress(address);
        r.setPhoneNumber("01000000000");
        r.setEmail(name.toLowerCase().replace(" ", "") + "@plato.com");
        r.setOpeningHours("10 AM - 12 AM");
        r.setRating(new BigDecimal("4.5"));

        em.persist(r);
        em.getTransaction().commit();
        em.close();

        return r;
    }

    public static Users insertUser(String name, int age, String email) {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();

        Users u = new Users();
        u.setName(name);
        u.setAge(age);
        u.setEmail(email);
        u.setPassword("123456");
        u.setPhone("01111111111");
        u.setIsElite(false);

        em.persist(u);
        em.getTransaction().commit();
        em.close();

        return u;
    }

    public static Menu insertMenu(String menuId, int restaurantId) {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();

        Restaurants r = em.find(Restaurants.class, restaurantId);

        Menu m = new Menu();
        m.setMenuId(menuId);
        m.setRestaurantId(r);

        em.persist(m);
        em.getTransaction().commit();
        em.close();

        return m;
    }

    public static MenuItems insertMenuItem(String name, BigDecimal price, String category, String menuId) {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();

        Menu menu = em.find(Menu.class, menuId);

        MenuItems item = new MenuItems();
        item.setName(name);
        item.setPrice(price);
        item.setCategory(category);
        item.setDescription("Test item added by JPA");
        item.setMenuId(menu);

        em.persist(item);
        em.getTransaction().commit();
        em.close();

        return item;
    }

    public static void updateRestaurantRating(int restaurantId, BigDecimal newRating) {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();

        Restaurants r = em.find(Restaurants.class, restaurantId);
        if (r != null) {
            r.setRating(newRating);
            em.merge(r);
        }

        em.getTransaction().commit();
        em.close();
    }

    public static void updateMenuItemPrice(int itemId, BigDecimal newPrice) {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();

        MenuItems item = em.find(MenuItems.class, itemId);
        if (item != null) {
            item.setPrice(newPrice);
            em.merge(item);
        }

        em.getTransaction().commit();
        em.close();
    }

    public static void deleteMenuItem(int itemId) {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();

        MenuItems item = em.find(MenuItems.class, itemId);
        if (item != null) {
            em.remove(item);
        }

        em.getTransaction().commit();
        em.close();
    }

    // 6  QUERIES

    // 1
    public static List<Restaurants> getAllRestaurants() {
        EntityManager em = emf.createEntityManager();
        List<Restaurants> result =
                em.createQuery("SELECT r FROM Restaurants r", Restaurants.class)
                  .getResultList();
        em.close();
        return result;
    }

    // 2
    public static List<MenuItems> getItemsMoreThan(BigDecimal price) {
        EntityManager em = emf.createEntityManager();
        List<MenuItems> result =
                em.createQuery("SELECT m FROM MenuItems m WHERE m.price > :price", MenuItems.class)
                  .setParameter("price", price)
                  .getResultList();
        em.close();
        return result;
    }

    // 3 - join between MenuItems, Menu, Restaurants
    public static List<MenuItems> getItemsByRestaurantName(String restaurantName) {
        EntityManager em = emf.createEntityManager();
        List<MenuItems> result =
                em.createQuery(
                    "SELECT i FROM MenuItems i " +
                    "JOIN i.menuId m " +
                    "JOIN m.restaurantId r " +
                    "WHERE r.name = :name", MenuItems.class)
                  .setParameter("name", restaurantName)
                  .getResultList();
        em.close();
        return result;
    }
    // 4 - join between Orders, Users, Restaurants
    public static List<Object[]> getOrdersWithUserAndRestaurant() {
    EntityManager em = emf.createEntityManager();

    List<Object[]> result =
            em.createQuery(
                "SELECT o.orderId, u.name, r.name, o.totalAmount " +
                "FROM Orders o " +
                "JOIN o.userId u " +
                "JOIN o.restaurantId r")
              .getResultList();

    em.close();
    return result;
    }

    // 5 - join Reviews with Restaurants
    public static List<Reviews> getReviewsForRestaurant(String restaurantName) {
        EntityManager em = emf.createEntityManager();
        List<Reviews> result =
                em.createQuery(
                    "SELECT rev FROM Reviews rev " +
                    "JOIN rev.restaurantId r " +
                    "WHERE r.name = :name", Reviews.class)
                  .setParameter("name", restaurantName)
                  .getResultList();
        em.close();
        return result;
    }

    // 6 - aggregate query
    public static List<Object[]> countItemsPerRestaurant() {
        EntityManager em = emf.createEntityManager();
        List<Object[]> result =
                em.createQuery(
                    "SELECT r.name, COUNT(i) " +
                    "FROM Restaurants r " +
                    "JOIN r.menuSet m " +
                    "JOIN m.menuItemsSet i " +
                    "GROUP BY r.name")
                  .getResultList();
        em.close();
        return result;
    }

    public static void main(String[] args) {
        Restaurants r = insertRestaurant("Plato Restaurant", "Cairo");
        Users u = insertUser("Omar Ahmed", 21, "omar" + System.currentTimeMillis() + "@test.com");

        Menu menu = insertMenu("MENU-1", r.getRestaurantId());

        MenuItems item1 = insertMenuItem("Chicken Pasta", new BigDecimal("120"), "Pasta", menu.getMenuId());
        MenuItems item2 = insertMenuItem("Burger", new BigDecimal("95"), "Fast Food", menu.getMenuId());

        updateRestaurantRating(r.getRestaurantId(), new BigDecimal("4.8"));
        updateMenuItemPrice(item1.getId(), new BigDecimal("135"));

        System.out.println("===== Query 1: All Restaurants =====");
        for (Restaurants res : getAllRestaurants()) {
            System.out.println(res.getRestaurantId() + " - " + res.getName() + " - " + res.getRating());
        }

        System.out.println("===== Query 2: Items more than 100 =====");
        for (MenuItems item : getItemsMoreThan(new BigDecimal("100"))) {
            System.out.println(item.getName() + " - " + item.getPrice());
        }

        System.out.println("===== Query 3: Items by Restaurant Name =====");
        for (MenuItems item : getItemsByRestaurantName("Plato Restaurant")) {
            System.out.println(item.getName() + " - " + item.getCategory());
        }

        System.out.println("===== Query 4 (New): Orders with User & Restaurant =====");
for (Object[] row : getOrdersWithUserAndRestaurant()) {
    System.out.println("Order ID: " + row[0] +
            ", User: " + row[1] +
            ", Restaurant: " + row[2] +
            ", Total: " + row[3]);
}

        System.out.println("===== Query 5: Reviews for Restaurant =====");
        for (Reviews rev : getReviewsForRestaurant("Plato Restaurant")) {
            System.out.println(rev.getReviewId() + " - " + rev.getRating());
        }

        System.out.println("===== Query 6: Count Items Per Restaurant =====");
        for (Object[] row : countItemsPerRestaurant()) {
            System.out.println(row[0] + " has " + row[1] + " items");
        }

        deleteMenuItem(item2.getId());

        emf.close();
    }
}