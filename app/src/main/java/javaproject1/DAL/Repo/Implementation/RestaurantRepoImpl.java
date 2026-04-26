package javaproject1.DAL.Repo.Implementation;

import javaproject1.DAL.Entity.*;
import javaproject1.DAL.Repo.abstraction.IRestaurantRepo;
import javaproject1.plato.JPAUtil;

import javax.persistence.EntityManager;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class RestaurantRepoImpl implements IRestaurantRepo {

    @Override
    public void addRestaurant(Restaurant restaurant) {
        EntityManager em = JPAUtil.getEntityManager();
        em.getTransaction().begin();

        javaproject1.plato.Restaurants r = new javaproject1.plato.Restaurants();
        r.setName(restaurant.getName());
        r.setAddress(restaurant.getAddress());
        r.setPhoneNumber(restaurant.getPhoneNumber());
        r.setEmail(restaurant.getEmail());
        r.setOpeningHours(restaurant.getOpeningHours());
        r.setRating(BigDecimal.valueOf(restaurant.getRating()));
        r.setImagePath(restaurant.getImagePath());

        em.persist(r);
        em.getTransaction().commit();
        restaurant.setRestaurantId(String.valueOf(r.getRestaurantId()));
        em.close();
        System.out.println("Restaurant added with ID: " + restaurant.getRestaurantId());
    }

    @Override
    public Restaurant getRestaurantById(int id) {
        EntityManager em = JPAUtil.getEntityManager();
        javaproject1.plato.Restaurants r = em.find(javaproject1.plato.Restaurants.class, id);
        if (r != null) {
            em.refresh(r); 
        }
        em.close();
        return r == null ? null : mapToDomain(r);
    }

    @Override
    public void updateRestaurant(Restaurant restaurant) {
        EntityManager em = JPAUtil.getEntityManager();
        em.getTransaction().begin();

        javaproject1.plato.Restaurants r = em.find(
                javaproject1.plato.Restaurants.class,
                Integer.parseInt(restaurant.getRestaurantId()));
        if (r != null) {
            r.setName(restaurant.getName());
            r.setAddress(restaurant.getAddress());
            r.setPhoneNumber(restaurant.getPhoneNumber());
            r.setEmail(restaurant.getEmail());
            r.setOpeningHours(restaurant.getOpeningHours());
            r.setRating(BigDecimal.valueOf(restaurant.getRating()));
            r.setImagePath(restaurant.getImagePath());
            em.merge(r);
            System.out.println("Restaurant updated: " + restaurant.getRestaurantId());
        }

        em.getTransaction().commit();
        em.close();
    }

    @Override
    public void deleteRestaurant(int id) {
        EntityManager em = JPAUtil.getEntityManager();
        em.getTransaction().begin();
        javaproject1.plato.Restaurants r = em.find(javaproject1.plato.Restaurants.class, id);
        if (r != null) em.remove(r);
        em.getTransaction().commit();
        em.close();
        System.out.println("Restaurant deleted with ID: " + id);
    }

    @Override
    public List<Restaurant> getAllRestaurants() {
        EntityManager em = JPAUtil.getEntityManager();
        List<javaproject1.plato.Restaurants> jpaList = em
                .createQuery("SELECT r FROM Restaurants r", javaproject1.plato.Restaurants.class)
                .getResultList();
        em.close();

        List<Restaurant> result = new ArrayList<>();
        for (javaproject1.plato.Restaurants r : jpaList) result.add(mapToDomain(r));
        return result;
    }

    private Restaurant mapToDomain(javaproject1.plato.Restaurants r) {
        Restaurant domain = new Restaurant();
        domain.setRestaurantId(String.valueOf(r.getRestaurantId()));
        domain.setName(r.getName() != null ? r.getName() : "");
        domain.setAddress(r.getAddress() != null ? r.getAddress() : "");
        domain.setPhoneNumber(r.getPhoneNumber() != null ? r.getPhoneNumber() : "");
        domain.setEmail(r.getEmail() != null ? r.getEmail() : "");
        domain.setOpeningHours(r.getOpeningHours() != null ? r.getOpeningHours() : "");
        domain.setRating(r.getRating() != null ? r.getRating().doubleValue() : 0.0);
        domain.setImagePath(r.getImagePath() != null ? r.getImagePath() : "");

        // Load menu items
        Menu menu = new Menu();
        if (r.getMenuSet() != null) {
            for (javaproject1.plato.Menu m : r.getMenuSet()) {
                menu.setMenuId(m.getMenuId());
                List<MenuItem> items = new ArrayList<>();
                if (m.getMenuItemsSet() != null) {
                    for (javaproject1.plato.MenuItems mi : m.getMenuItemsSet()) {
                        MenuItem item = new MenuItem();
                        item.setItemId(String.valueOf(mi.getId()));
                        item.setName(mi.getName() != null ? mi.getName() : "");
                        item.setPrice(mi.getPrice() != null ? mi.getPrice().doubleValue() : 0.0);
                        item.setDescription(mi.getDescription() != null ? mi.getDescription() : "");
                        item.setCategory(mi.getCategory() != null ? mi.getCategory() : "");
                        item.setImagePath(mi.getImagePath() != null ? mi.getImagePath() : "");
                        items.add(item);
                    }
                }
                menu.setItems(items);
                break; // one menu per restaurant
            }
        }
        domain.setMenu(menu);

        // Load employees
        List<Employee> employees = new ArrayList<>();
        if (r.getEmployeesSet() != null) {
            for (javaproject1.plato.Employees e : r.getEmployeesSet()) {
                Employee emp = new Employee();
                emp.setId(String.valueOf(e.getId()));
                emp.setName(e.getName());
                emp.setRole(e.getRole());
                emp.setPhoneNumber(e.getPhoneNumber());
                employees.add(emp);
            }
        }
        domain.setEmployees(employees);

        // Load reviews
        List<Review> reviews = new ArrayList<>();
        if (r.getReviewsSet() != null) {
            for (javaproject1.plato.Reviews rev : r.getReviewsSet()) {
                Review review = new Review();
                review.setReviewId(String.valueOf(rev.getReviewId()));
                review.setRating(rev.getRating());
                review.setComment(rev.getComment());
                reviews.add(review);
            }
        }
        domain.setReviews(reviews);

        return domain;
    }
}