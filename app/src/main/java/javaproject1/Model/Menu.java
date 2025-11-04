package javaproject1.Model;



import java.util.ArrayList;
import java.util.List;

/**
 * Represents a restaurant's menu.
 * Contains a list of MenuItems (Lecture 3 - Collections).
 */
public class Menu {
    
    // List of menu items
    private List<MenuItem> items;

    public Menu() {
        this.items = new ArrayList<>();
    }

    public void addItem(MenuItem item) {
        this.items.add(item);
    }

    public void removeItem(MenuItem item) {
        this.items.remove(item);
    }

    public MenuItem getMenuItem(String itemName) {
        for (MenuItem item : items) {
            if (item.getName().equalsIgnoreCase(itemName)) {
                return item;
            }
        }
        return null;
    }

    // --- Getters and Setters ---
    
    public List<MenuItem> getItems() {
        return items;
    }

    public void setItems(List<MenuItem> items) {
        this.items = items;
    }

    @Override
    public String toString() {
        return "Menu{" +
                "itemCount=" + items.size() +
                '}';
    }
}
