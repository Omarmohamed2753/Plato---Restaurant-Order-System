package javaproject1.DAL.Entity;
public class Review {
    private String reviewId;
    private User user;
    private Restaurant restaurant;
    private int rating;
    private String comment;

    public Review(String reviewId,User user, Restaurant restaurant, int rating, String comment) {
        this.reviewId = reviewId;
        this.user = user;
        this.restaurant = restaurant;
        this.rating = rating;
        this.comment = comment;
    }
    public Review() {}

    // getters/setters
    public String getReviewId() { return reviewId; }
    public void setReviewId(String reviewId) { this.reviewId = reviewId; }
    public User getUser() { return user; }
    public Restaurant getRestaurant() { return restaurant; }
    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }
    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
    public void setUser(User user) { this.user = user; }
    public void setRestaurant(Restaurant restaurant) { this.restaurant = restaurant; }
    

    @Override
    public String toString() {
        return "Review{" +
                "user=" + (user != null ? user.getName() : "Unknown") +
                ", rating=" + rating +
                ", comment='" + comment + '\'' +
                '}';
    }
}
