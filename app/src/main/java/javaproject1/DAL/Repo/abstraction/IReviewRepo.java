package javaproject1.DAL.Repo.abstraction;

import java.util.List;
import javaproject1.DAL.Entity.Review;

public interface IReviewRepo {
    
    public void addReview(Review review);
    public Review getReviewById(int id);
    public void updateReview(Review review);
    public void deleteReview(int id);
    List<Review> getAllReviews();
    
}