package javaproject1.DAL.Repo.Implementation;

import javaproject1.DAL.Entity.Restaurant;
import javaproject1.DAL.Entity.Review;
import javaproject1.DAL.Entity.User;
import javaproject1.DAL.Repo.abstraction.IReviewRepo;
import javaproject1.plato.JPAUtil;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;

public class ReviewRepoImpl implements IReviewRepo {

    @Override
    public void addReview(Review review) {
        EntityManager em = JPAUtil.getEntityManager();
        em.getTransaction().begin();

        javaproject1.plato.Reviews r = new javaproject1.plato.Reviews();
        r.setRating(review.getRating());
        r.setComment(review.getComment());

        if (review.getUser() != null && review.getUser().getId() != null) {
            javaproject1.plato.Users u = em.find(
                    javaproject1.plato.Users.class,
                    Integer.parseInt(review.getUser().getId()));
            r.setUserId(u);
        }
        if (review.getRestaurant() != null && review.getRestaurant().getRestaurantId() != null) {
            javaproject1.plato.Restaurants rest = em.find(
                    javaproject1.plato.Restaurants.class,
                    Integer.parseInt(review.getRestaurant().getRestaurantId()));
            r.setRestaurantId(rest);
        }

        em.persist(r);
        em.getTransaction().commit();
        review.setReviewId(String.valueOf(r.getReviewId()));
        em.close();
    }

    @Override
    public Review getReviewById(int id) {
        EntityManager em = JPAUtil.getEntityManager();
        javaproject1.plato.Reviews r = em.find(javaproject1.plato.Reviews.class, id);
        em.close();
        return r == null ? null : mapToDomain(r);
    }

    @Override
    public void updateReview(Review review) {
        EntityManager em = JPAUtil.getEntityManager();
        em.getTransaction().begin();

        javaproject1.plato.Reviews r = em.find(
                javaproject1.plato.Reviews.class, Integer.parseInt(review.getReviewId()));
        if (r != null) {
            r.setRating(review.getRating());
            r.setComment(review.getComment());
            em.merge(r);
        }

        em.getTransaction().commit();
        em.close();
    }

    @Override
    public void deleteReview(int id) {
        EntityManager em = JPAUtil.getEntityManager();
        em.getTransaction().begin();
        javaproject1.plato.Reviews r = em.find(javaproject1.plato.Reviews.class, id);
        if (r != null) em.remove(r);
        em.getTransaction().commit();
        em.close();
    }

    @Override
    public List<Review> getAllReviews() {
        EntityManager em = JPAUtil.getEntityManager();
        List<javaproject1.plato.Reviews> jpaList = em
                .createQuery("SELECT r FROM Reviews r", javaproject1.plato.Reviews.class)
                .getResultList();
        em.close();

        List<Review> result = new ArrayList<>();
        for (javaproject1.plato.Reviews r : jpaList) result.add(mapToDomain(r));
        return result;
    }

    private Review mapToDomain(javaproject1.plato.Reviews r) {
        Review domain = new Review();
        domain.setReviewId(String.valueOf(r.getReviewId()));
        domain.setRating(r.getRating());
        domain.setComment(r.getComment());

        if (r.getUserId() != null) {
            User user = new User();
            user.setId(String.valueOf(r.getUserId().getUserId()));
            user.setName(r.getUserId().getName() != null ?
                    r.getUserId().getName() : "User #" + r.getUserId().getUserId());
            user.setEmail(r.getUserId().getEmail());
            domain.setUser(user);
        }

        if (r.getRestaurantId() != null) {
            Restaurant rest = new Restaurant();
            rest.setRestaurantId(String.valueOf(r.getRestaurantId().getRestaurantId()));
            rest.setName(r.getRestaurantId().getName());
            domain.setRestaurant(rest);
        }

        return domain;
    }
}