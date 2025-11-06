package javaproject1.DAL.Entity;

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
