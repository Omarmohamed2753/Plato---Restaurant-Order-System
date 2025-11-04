package javaproject1.Model;

/**
 * Represents a restaurant staff member.
 * Inherits from User.
 */
public class Employee extends Person {

    
    private int experiencesYear;
    private String imagePath;
    private String role; // e.g., "Manager", "Chef", "DeliveryPerson"
    
    // Association relationship
    private Restaurant restaurant; 
    
    public Employee(int id, String name, int age, String role, String phoneNumber, String imagePath, int experiencesYear) {
        super(id, name, age, phoneNumber);
        this.experiencesYear = experiencesYear;
        this.imagePath = imagePath;
        this.role = role;
    }
    
    /**
     * A generic method representing employee duties.
     */
    public void performDuty() {
        System.out.println(name + " (" + role + ") is performing their duties.");
        if ("DeliveryPerson".equals(role)) {
            System.out.println("Looking for orders to deliver...");
        } else if ("Chef".equals(role)) {
            System.out.println("Preparing food...");
        }
    }
    
    // --- Getters and Setters ---

    public int getExperiencesYear() {
        return experiencesYear;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setExperiencesYear(int experiencesYear) {
        this.experiencesYear = experiencesYear;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getRole() {
        return role;
    }
    
    public void setRole(String role) {
        this.role = role;
    }
    
    public Restaurant getRestaurant() {
        return restaurant;
    }

    public void setRestaurant(Restaurant restaurant) {
        this.restaurant = restaurant;
    }

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