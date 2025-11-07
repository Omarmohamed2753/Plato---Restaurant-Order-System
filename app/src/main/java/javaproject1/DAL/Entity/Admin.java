package javaproject1.DAL.Entity;
public class Admin extends Person {
    private String email;
    private String password;
    private Restaurant restaurant; // Admin manages one restaurant
    public Admin(int id, String name, int age, String phoneNumber, String email, String password, Restaurant restaurant) {
        super(id, name, age, phoneNumber);
        this.email = email;
        this.password = password;
        this.restaurant = restaurant;
    }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public Restaurant getRestaurant() { return restaurant; }
    public void setRestaurant(Restaurant restaurant) { this.restaurant = restaurant; }
    @Override
    public String toString() {
        return "Admin{" +
                "name='" + name + '\'' +
                ", restaurant=" + (restaurant != null ? restaurant.getName() : "None") +
                '}';
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Admin)) return false;
        if (!super.equals(o)) return false;
        Admin admin = (Admin) o;
        return email.equals(admin.email) &&
                password.equals(admin.password) &&
                ((restaurant == null && admin.restaurant == null) ||
                 (restaurant != null && restaurant.equals(admin.restaurant)));
    }
}
