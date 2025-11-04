package javaproject1.Model;

import java.util.Objects;

/**
 * Represents a single item on the menu.
 * This is a data object, so overriding equals() and hashCode() is important (Lecture 2).
 */
public class MenuItem {
    
    private String itemId;
    private String name;
    private String description;
    private double price;
    private String category;

    public MenuItem(String itemId, String name, String description, double price, String category) {
        this.itemId = itemId;
        this.name = name;
        this.description = description;
        this.price = price;
        this.category = category;
    }

    // --- Getters and Setters ---

    public String getItemId() {
        return itemId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    // --- Overriding Object methods (Lecture 2) ---

    @Override
    public String toString() {
        return "MenuItem{" +
                "name='" + name + '\'' +
                ", price=" + price +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MenuItem menuItem = (MenuItem) o;
        return Objects.equals(itemId, menuItem.itemId) &&
                Objects.equals(name, menuItem.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(itemId, name);
    }
}
