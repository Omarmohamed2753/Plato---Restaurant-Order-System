package javaproject1.DAL.Repo.Implementation;

import javaproject1.DAL.DataBase.DBConnection;
import javaproject1.DAL.Entity.Employee;
import javaproject1.DAL.Entity.Menu;
import javaproject1.DAL.Entity.Order;
import javaproject1.DAL.Entity.Restaurant;
import javaproject1.DAL.Entity.Review;
import javaproject1.DAL.Repo.abstraction.IRestaurantRepo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RestaurantRepoImpl implements IRestaurantRepo {

    @Override
    public void addRestaurant(Restaurant restaurant) {
        String sql = "INSERT INTO restaurants (name, address, phone_number, email, opening_hours, rating, image_path) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, restaurant.getName());
            stmt.setString(2, restaurant.getAddress());
            stmt.setString(3, restaurant.getPhoneNumber());
            stmt.setString(4, restaurant.getEmail());
            stmt.setString(5, restaurant.getOpeningHours());
            stmt.setDouble(6, restaurant.getRating());
            stmt.setString(7, restaurant.getImagePath());

            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    restaurant.setRestaurantId(rs.getString(1));
                }
            }

            System.out.println("Restaurant added (id=" + restaurant.getRestaurantId() + ").");

        } catch (SQLException e) {
            System.out.println("Add Restaurant Error: " + e.getMessage());
        }
    }

    @Override
    public Restaurant getRestaurantById(int id) {
        String sql = "SELECT * FROM restaurants WHERE restaurant_id = ?";
        Restaurant restaurant = null;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    restaurant = mapToRestaurant(rs, conn);
                }
            }

        } catch (SQLException e) {
            System.out.println("Get Restaurant Error: " + e.getMessage());
        }

        return restaurant;
    }

    @Override
    public void updateRestaurant(Restaurant restaurant) {
        String sql = "UPDATE restaurants SET name = ?, address = ?, phone_number = ?, email = ?, opening_hours = ?, rating = ?, image_path = ? WHERE restaurant_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, restaurant.getName());
            stmt.setString(2, restaurant.getAddress());
            stmt.setString(3, restaurant.getPhoneNumber());
            stmt.setString(4, restaurant.getEmail());
            stmt.setString(5, restaurant.getOpeningHours());
            stmt.setDouble(6, restaurant.getRating());
            stmt.setString(7, restaurant.getImagePath());
            stmt.setString(8, restaurant.getRestaurantId());

            int rows = stmt.executeUpdate();
            if (rows > 0) System.out.println("Restaurant updated (id=" + restaurant.getRestaurantId() + ").");
            else System.out.println("No restaurant found with id=" + restaurant.getRestaurantId());

        } catch (SQLException e) {
            System.out.println("Update Restaurant Error: " + e.getMessage());
        }
    }

    @Override
    public void deleteRestaurant(int id) {
        String sql = "DELETE FROM restaurants WHERE restaurant_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            int rows = stmt.executeUpdate();
            if (rows > 0) System.out.println(" Restaurant deleted (id=" + id + ").");
            else System.out.println(" No restaurant found with id=" + id);

        } catch (SQLException e) {
            System.out.println("Delete Restaurant Error: " + e.getMessage());
        }
    }

    public List<Restaurant> getAllRestaurants() {
        String sql = "SELECT * FROM restaurants";
        List<Restaurant> list = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                list.add(mapToRestaurant(rs, conn));
            }

        } catch (SQLException e) {
            System.out.println("Get All Restaurants Error: " + e.getMessage());
        }

        return list;
    }
    
    private Restaurant mapToRestaurant(ResultSet rs, Connection conn) throws SQLException {
        Restaurant restaurant = new Restaurant();
        int id = rs.getInt("restaurant_id");
        restaurant.setRestaurantId(String.valueOf(id));
        restaurant.setName(rs.getString("name"));
        restaurant.setAddress(rs.getString("address"));
        restaurant.setPhoneNumber(rs.getString("phone_number"));
        restaurant.setEmail(rs.getString("email"));
        restaurant.setOpeningHours(rs.getString("opening_hours"));
        restaurant.setRating(rs.getDouble("rating"));
        restaurant.setImagePath(rs.getString("image_path"));
        restaurant.setMenu(loadMenu(conn, id));
        restaurant.setEmployees(loadEmployees(conn, id));
        restaurant.setReviews(loadReviews(conn, String.valueOf(id)));
        restaurant.setOrders(loadOrders(conn, String.valueOf(id)));
        return restaurant;
    }
    
    private Menu loadMenu(Connection conn, int restaurantId) throws SQLException {
        String sql = "SELECT menu_id FROM menu WHERE restaurant_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, restaurantId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Menu m = new Menu();
                    m.setMenuId(rs.getString("menu_id"));
                    return m;
                }
            }
        }
        return new Menu();
    }

    private List<Employee> loadEmployees(Connection conn, int restaurantId) throws SQLException {
        List<Employee> list = new ArrayList<>();
        String sql = "SELECT * FROM employees WHERE restaurant_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, restaurantId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Employee e = new Employee();
                    // FIXED: Changed from "employee_id" to "id"
                    e.setId(rs.getString("id"));
                    e.setName(rs.getString("name"));
                    e.setRole(rs.getString("role"));
                    e.setPhoneNumber(rs.getString("phone_number"));
                    list.add(e);
                }
            }
        }
        return list;
    }

    private List<Review> loadReviews(Connection conn, String restaurantId) throws SQLException {
        List<Review> list = new ArrayList<>();
        String sql = "SELECT * FROM reviews WHERE restaurant_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, restaurantId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Review review = new Review();
                    review.setReviewId(rs.getString("review_id"));
                    review.setRating(rs.getInt("rating"));
                    review.setComment(rs.getString("comment"));
                    list.add(review);
                }
            }
        }
        return list;
    }

    private List<Order> loadOrders(Connection conn, String restaurantId) throws SQLException {
        List<Order> list = new ArrayList<>();
        String sql = "SELECT * FROM orders WHERE restaurant_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, restaurantId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Order o = new Order();
                    o.setOrderId(rs.getString("order_id"));
                    o.setTotalAmount(rs.getDouble("total_amount"));
                    o.setStatus(javaproject1.DAL.Enums.OrderStatus.valueOf(rs.getString("status").toUpperCase()));
                    o.setOrderDate(rs.getDate("order_date"));
                    list.add(o);
                }
            }
        }
        return list;
    }
}