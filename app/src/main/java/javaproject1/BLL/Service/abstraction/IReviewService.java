package javaproject1.BLL.Service.abstraction;

import javaproject1.DAL.Entity.Review;
import javaproject1.DAL.Entity.Restaurant;
import java.util.List;

public interface IReviewService {

    void submitReview(Review review, Restaurant restaurant);
    void editReview(Review review, int newRating, String newComment);
    void deleteReview(Review review, Restaurant restaurant);
    void viewReviews(Restaurant restaurant);
    Review getReviewById(int id);
    List<Review> getAllReviews();
}
