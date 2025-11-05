package javaproject1.Model;

public class Employee extends Person {
    private int experiencesYear;
    private String imagePath;
    private String role;
    private Restaurant restaurant; // back-reference

    public Employee(int id, String name, int age, String role, String phoneNumber, String imagePath, int experiencesYear) {
        super(id, name, age, phoneNumber);
        this.experiencesYear = experiencesYear;
        this.imagePath = imagePath;
        this.role = role;
    }
    public void getdetails() {
        System.out.println("Employee Details:");
        System.out.println("Name: " + getName());
        System.out.println("Age: " + getAge());
        System.out.println("Role: " + role);
        System.out.println("Phone Number: " + getPhoneNumber());
        System.out.println("Experience Years: " + experiencesYear);
        if (restaurant != null) {
            System.out.println("Restaurant: " + restaurant.getName());
        } else {
            System.out.println("Restaurant: Not assigned");
        }
    }
    // getters/setters
    public int getExperiencesYear() { return experiencesYear; }
    public String getImagePath() { return imagePath; }
    public void setExperiencesYear(int experiencesYear) { this.experiencesYear = experiencesYear; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public Restaurant getRestaurant() { return restaurant; }
    public void setRestaurant(Restaurant restaurant) { this.restaurant = restaurant; }

    @Override
    public String toString() {
        return "Employee{" +
                "id=" + getId() +
                ", name='" + getName() + '\'' +
                ", role='" + role + '\'' +
                ", experiencesYear=" + experiencesYear +
                '}';
    }
}
