package javaproject1.DAL.Entity;

import java.util.Objects;

public class MenuItem {
    private int itemId;
    private String name;
    private String description;
    private double price;
    private String category;
    private String imagePath;
    public MenuItem() {
    }
    public MenuItem(int itemId, String name, String description, double price, String category) {
        this.itemId = itemId;
        this.name = name;
        this.description = description;
        this.price = price;
        this.category = category;
    }
    public MenuItem(int itemId, String name, double price) {
    this.itemId = itemId;
    this.name = name;
    this.price = price;
}
public MenuItem(String name, String description, double price, String category) {
    this.name = name;
    this.description = description;
    this.price = price;
    this.category = category;
}


    // getters/setters
    public int getItemId() { return itemId; }
    public void setItemId(int itemId) { this.itemId = itemId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }

    @Override
    public String toString() {
        return "MenuItem{" +
                "itemId='" + itemId + '\'' +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", category='" + category + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MenuItem menuItem = (MenuItem) o;
        return Objects.equals(itemId, menuItem.itemId);
    }

}
