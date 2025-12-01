package javaproject1.BLL.Service.implementation;

import javaproject1.BLL.Service.abstraction.IReviewService;
import javaproject1.DAL.Entity.Review;
import javaproject1.DAL.Entity.Restaurant;
import javaproject1.DAL.Repo.Implementation.ReviewRepoImpl;
import javaproject1.DAL.Repo.abstraction.IReviewRepo;

import java.util.List;

public class ReviewServiceImpl implements IReviewService {

    private final IReviewRepo reviewRepo;

    public ReviewServiceImpl() {
        this(new ReviewRepoImpl());
    }

    public ReviewServiceImpl(IReviewRepo reviewRepo) {
        this.reviewRepo = reviewRepo;
    }

    @Override
    public void submitReview(Review review, Restaurant restaurant) {
        if (restaurant == null || review == null) {
            System.out.println("Invalid restaurant or review!");
            return;
        }
        reviewRepo.addReview(review);          
        System.out.println("Review submitted by " + review.getUser().getName() +
                           " for " + restaurant.getName());
    }

    @Override
    public void editReview(Review review, int newRating, String newComment) {
        if (review != null) {
            review.setRating(newRating);
            review.setComment(newComment);
            reviewRepo.updateReview(review);  
            System.out.println("Review updated with ID: " + review.getReviewId());
        }
    }

    @Override
    public void deleteReview(Review review, Restaurant restaurant) {
        if (restaurant != null && review != null) {
            reviewRepo.deleteReview(Integer.parseInt(review.getReviewId())); 
            System.out.println("Review deleted with ID: " + review.getReviewId());
        }
    }

    @Override
    public void viewReviews(Restaurant restaurant) {
        if (restaurant != null && restaurant.getReviews() != null) {
            for (Review review : restaurant.getReviews()) {
                System.out.println(review);
            }
        } else {
            System.out.println("No reviews found for this restaurant.");
        }
    }

    @Override
    public Review getReviewById(int id) {
        return reviewRepo.getReviewById(id);
    }

    @Override
    public List<Review> getAllReviews() {
        return reviewRepo.getAllReviews();
    }
}
