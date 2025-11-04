package javaproject1.Model;

/**
 * Represents a restaurant staff member.
 * Inherits from User (Lecture 4).
 */
public class Employee extends Person {

    private int employeeId;
    private String name;
    private String role; // e.g., "Manager", "Chef", "DeliveryPerson"
    
    // Association relationship (Lecture 4)
    private Restaurant restaurant; 

    public Employee(int employeeId, String name,int age, String role, String phoneNumber) {
        super(employeeId, name, age, phoneNumber);
        this.employeeId = employeeId;
        this.name = name;
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

    public int getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(int employeeId) {
        this.employeeId = employeeId;
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
                "employeeId='" + employeeId + '\'' +
                ", name='" + name + '\'' +
                ", role='" + role + '\'' +
                '}';
    }
}