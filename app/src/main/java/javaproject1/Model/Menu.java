package javaproject1.Model;

import java.util.ArrayList;
import java.util.List;

public class Menu {
    private List<MenuItem> items;

    public Menu() {
        this.items = new ArrayList<>();
    }

    public List<MenuItem> getItems() {
        return items;
    }

    public void setItems(List<MenuItem> items) {
        this.items = items;
    }

    public void addItem(MenuItem item) {
        if (!items.contains(item)) {
            items.add(item);
        }
    }
    public void removeItem(MenuItem item) {
        items.remove(item);
    }
    public void displayMenu() {
        for (MenuItem item : items) {
            System.out.println(item);
        }
    }

    @Override
    public String toString() {
        return "Menu{" +
                "items=" + items +
                '}';
    }
}
