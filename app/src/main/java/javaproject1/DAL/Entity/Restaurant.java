package javaproject1.DAL.Entity;
import java.util.ArrayList;
import java.util.List;
public class Restaurant {
    private int restaurantId;
    private String name;
    private String address;
    private String phoneNumber;
    private String email;
    private String openingHours;
    private double rating;
    private Menu menu;
    private List<Employee> employees;
    private String imagePath;
    private List<Review> reviews;
    private List<Order> orders;
    public Restaurant() {}
    public Restaurant(int restaurantId, String name, String address, String phoneNumber, String email, String openingHours, double rating, String imagePath) {
        this.restaurantId = restaurantId;
        this.name = name;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.openingHours = openingHours;
        this.rating = rating;
        this.menu = new Menu();
        this.employees = new ArrayList<>();
        this.imagePath = imagePath;
        this.reviews = new ArrayList<>();
        this.orders = new ArrayList<>();
    }
    public Menu getMenu() { return menu; }
    public int getRestaurantId() { return restaurantId; }
    public void setRestaurantId(int restaurantId) { this.restaurantId = restaurantId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getOpeningHours() { return openingHours; }
    public void setOpeningHours(String openingHours) { this.openingHours = openingHours; }
    public double getRating() { return rating; }
    public void setRating(double rating) { this.rating = rating; }
    public List<Employee> getEmployees() { return employees; }
    public void setEmployees(List<Employee> employees) { this.employees = employees; }
    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }
    public List<Review> getReviews() {return reviews;}
    public List<Order> getOrders() {return orders;}
    public void setMenu(Menu menu) {this.menu = menu;}
    public void setReviews(List<Review> reviews) {this.reviews = reviews;}
    public void setOrders(List<Order> orders) {this.orders = orders;}
    @Override
    public String toString() {
        return "Restaurant{" +
                "restaurantId='" + restaurantId + '\'' +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", email='" + email + '\'' +
                ", openingHours='" + openingHours + '\'' +
                ", rating=" + rating +
                ", menu=" + menu +
                ", employees=" + employees +
                ", imagePath='" + imagePath + '\'' +
                '}';
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Restaurant that = (Restaurant) o;
        return restaurantId == that.restaurantId;
    }
}
