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
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, review.getUser() != null ? review.getUser().getId() : "");
            stmt.setString(2, review.getRestaurant() != null ? review.getRestaurant().getRestaurantId() : "");
            stmt.setInt(3, review.getRating());
            stmt.setString(4, review.getComment());
            stmt.executeUpdate();

            // Get generated review ID
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    review.setReviewId(rs.getString(1));
                }
            }

        } catch (SQLException e) {
            System.out.println("Add Review Error: " + e.getMessage());
            e.printStackTrace();
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
                review = mapToReview(rs, conn);
            }

        } catch (SQLException e) {
            System.out.println("Get Review Error: " + e.getMessage());
            e.printStackTrace();
        }
        return review;
    }

    @Override
    public void updateReview(Review review) {
        String sql = "UPDATE reviews SET user_id=?, restaurant_id=?, rating=?, comment=? WHERE review_id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, review.getUser() != null ? review.getUser().getId() : "");
            stmt.setString(2, review.getRestaurant() != null ? review.getRestaurant().getRestaurantId() : "");
            stmt.setInt(3, review.getRating());
            stmt.setString(4, review.getComment());
            stmt.setString(5, review.getReviewId());
            stmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Update Review Error: " + e.getMessage());
            e.printStackTrace();
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
            e.printStackTrace();
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
                reviews.add(mapToReview(rs, conn));
            }

        } catch (SQLException e) {
            System.out.println("Get All Reviews Error: " + e.getMessage());
            e.printStackTrace();
        }

        return reviews;
    }

    /**
     * Maps a ResultSet row to a Review object with full user details loaded
     */
    private Review mapToReview(ResultSet rs, Connection conn) throws SQLException {
        Review review = new Review();
        review.setReviewId(rs.getString("review_id"));
        review.setRating(rs.getInt("rating"));
        review.setComment(rs.getString("comment"));

        // Load full user details from users table
        int userId = rs.getInt("user_id");
        if (!rs.wasNull() && userId > 0) {
            User user = loadUserDetails(conn, userId);
            review.setUser(user);
        } else {
            // Fallback: create anonymous user
            User anonymousUser = new User();
            anonymousUser.setName("Anonymous");
            review.setUser(anonymousUser);
        }

        // Load restaurant reference (lightweight - just ID)
        int restaurantId = rs.getInt("restaurant_id");
        if (!rs.wasNull() && restaurantId > 0) {
            Restaurant restaurant = new Restaurant();
            restaurant.setRestaurantId(String.valueOf(restaurantId));
            review.setRestaurant(restaurant);
        }

        return review;
    }

    /**
     * Loads complete user details from the users table
     * This fixes the "Anonymous" issue by fetching actual user data
     */
    private User loadUserDetails(Connection conn, int userId) throws SQLException {
        String sql = "SELECT user_id, name, email, age, phone, is_elite FROM users WHERE user_id = ?";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    User user = new User();
                    user.setId(String.valueOf(rs.getInt("user_id")));
                    
                    // Get name with fallback
                    String name = rs.getString("name");
                    user.setName(name != null && !name.trim().isEmpty() ? name : "User #" + userId);
                    
                    // Get other details
                    user.setEmail(rs.getString("email"));
                    user.setAge(rs.getInt("age"));
                    user.setPhoneNumber(rs.getString("phone"));
                    user.setElite(rs.getBoolean("is_elite"));
                    
                    System.out.println("DEBUG ReviewRepo: Loaded user " + user.getName() + " (ID: " + userId + ")");
                    return user;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error loading user details for user_id " + userId + ": " + e.getMessage());
            // Don't throw - return fallback instead
        }
        
        // Fallback: return user with just ID and generic name
        User fallbackUser = new User();
        fallbackUser.setId(String.valueOf(userId));
        fallbackUser.setName("User #" + userId);
        System.out.println("DEBUG ReviewRepo: Using fallback for user ID " + userId);
        return fallbackUser;
    }
}