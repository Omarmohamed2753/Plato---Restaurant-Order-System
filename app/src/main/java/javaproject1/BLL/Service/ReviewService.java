package javaproject1.BLL.Service;
import javaproject1.DAL.Entity.Restaurant;
import javaproject1.DAL.Entity.Review;
import java.util.List;


public class ReviewService {

    public void submitReview(Restaurant restaurant, Review review) {
        if (restaurant == null) {
            System.out.println("Invalid restaurant!");
            return;
        }
        restaurant.addReview(review);
        System.out.println("Review submitted by " + review.getUser().getName() + " for " + restaurant.getName());
    }

    public void editReview(Review review, int newRating, String newComment) {
        review.setRating(newRating);
        review.setComment(newComment);
        System.out.println("Review updated: " + review);
    }

    public void deleteReview(Restaurant restaurant, Review review) {
        if (restaurant != null) {
            restaurant.removeReview(review);
            System.out.println("Review deleted for restaurant: " + restaurant.getName());
        }
    }

    public void viewReviews(Restaurant restaurant) {
        if (restaurant == null || restaurant.getReviews().isEmpty()) {
            System.out.println("No reviews available for this restaurant.");
            return;
        }
        System.out.println("Reviews for " + restaurant.getName() + ":");
        for (Review review : restaurant.getReviews()) {
            System.out.println(review);
        }
    }

    public List<Review> getAllReviews(Restaurant restaurant) {
        return restaurant != null ? restaurant.getReviews() : null;
    }
}