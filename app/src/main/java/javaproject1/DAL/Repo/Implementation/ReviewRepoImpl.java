package javaproject1.DAL.Repo.Implementation;

import javaproject1.DAL.DataBase.DBConnection;
import javaproject1.DAL.Entity.*;
import javaproject1.DAL.Repo.abstraction.IReviewRepo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReviewRepoImpl implements IReviewRepo {

    @Override
    public void addReview(Review review) {
        String sql = "INSERT INTO reviews (user_id, restaurant_id, rating, comment) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, review.getUser() != null ? review.getUser().getId() : Types.NULL);
            stmt.setInt(2, review.getRestaurant() != null ? review.getRestaurant().getRestaurantId() : Types.NULL);
            stmt.setInt(3, review.getRating());
            stmt.setString(4, review.getComment());
            stmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Add Review Error: " + e.getMessage());
        }
    }

    @Override
    public Review getReviewById(int id) {
        String sql = "SELECT * FROM reviews WHERE review_id = ?";
        Review review = null;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                review = mapToReview(rs);
            }

        } catch (SQLException e) {
            System.out.println("Get Review Error: " + e.getMessage());
        }
        return review;
    }

    @Override
    public void updateReview(Review review) {
        String sql = "UPDATE reviews SET user_id=?, restaurant_id=?, rating=?, comment=? WHERE review_id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, review.getUser() != null ? review.getUser().getId() : Types.NULL);
            stmt.setInt(2, review.getRestaurant() != null ? review.getRestaurant().getRestaurantId() : Types.NULL);
            stmt.setInt(3, review.getRating());
            stmt.setString(4, review.getComment());
            stmt.setInt(5, review.getId());
            stmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Update Review Error: " + e.getMessage());
        }
    }

    @Override
    public void deleteReview(int id) {
        String sql = "DELETE FROM reviews WHERE review_id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Delete Review Error: " + e.getMessage());
        }
    }

    @Override
    public List<Review> getAllReviews() {
        String sql = "SELECT * FROM reviews";
        List<Review> reviews = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                reviews.add(mapToReview(rs));
            }

        } catch (SQLException e) {
            System.out.println("Get All Reviews Error: " + e.getMessage());
        }

        return reviews;
    }

    private Review mapToReview(ResultSet rs) throws SQLException {
        Review review = new Review(0, null, null, 0, null);
        review.setId(rs.getInt("review_id"));
        review.setRating(rs.getInt("rating"));
        review.setComment(rs.getString("comment"));

        User user = new User();
        user.setId(rs.getInt("user_id"));
        review.setUser(user);

        Restaurant restaurant = new Restaurant();
        restaurant.setRestaurantId(rs.getInt("restaurant_id"));
        review.setRestaurant(restaurant);

        return review;
    }
}
