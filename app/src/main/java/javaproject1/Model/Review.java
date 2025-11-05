package javaproject1.Model;

public class Review {
    private int id;
    private User user;
    private Restaurant restaurant;
    private int rating;
    private String comment;

    public Review(int id,User user, Restaurant restaurant, int rating, String comment) {
        this.id = id;
        this.user = user;
        this.restaurant = restaurant;
        this.rating = rating;
        this.comment = comment;
    }
    public void submitReview(Restaurant restaurant) {
        if (restaurant == null) {
            System.out.println("Invalid restaurant!");
            return;
        }
        restaurant.addReview(this);
        System.out.println("Review submitted by " + user.getName() + " for " + restaurant.getName());
    }
    public void editReview(Review review, int newRating, String newComment) {
        review.setRating(newRating);
        review.setComment(newComment);
    }
    public void deleteReview(Restaurant restaurant, Review review) {
        restaurant.removeReview(review);
    }
    public void viewReviews(Restaurant restaurant) {
        for (Review review : restaurant.getReviews()) {
            System.out.println(review);
        }
    }
    // getters/setters
    public int getId() { return id; }
    public User getUser() { return user; }
    public Restaurant getRestaurant() { return restaurant; }
    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }
    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    @Override
    public String toString() {
        return "Review{" +
                "user=" + (user != null ? user.getName() : "Unknown") +
                ", rating=" + rating +
                ", comment='" + comment + '\'' +
                '}';
    }
}
