package javaproject1.DAL.Entity;
public class Employee extends Person {
    private int experiencesYear;
    private String imagePath;
    private String role;
    private Restaurant restaurant; // back-reference
    public Employee() {
    }
    public Employee(int id, String name, int age, String role, String phoneNumber, String imagePath, int experiencesYear) {
        super(id, name, age, phoneNumber);
        this.experiencesYear = experiencesYear;
        this.imagePath = imagePath;
        this.role = role;
    }
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
    @Override
    public boolean equals(Object o) {  
        if (this == o) return true;
        if (!(o instanceof Employee)) return false;
        if (!super.equals(o)) return false;
        Employee employee = (Employee) o;
        return experiencesYear == employee.experiencesYear &&
                imagePath.equals(employee.imagePath) &&
                role.equals(employee.role) &&
                ((restaurant == null && employee.restaurant == null) ||
                 (restaurant != null && restaurant.equals(employee.restaurant)));
    }
}
