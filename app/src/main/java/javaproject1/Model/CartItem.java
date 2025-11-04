package javaproject1.Model;

import java.util.Objects;

/**
 * Represents an item inside a cart, linking a MenuItem to a quantity.
 */
public class CartItem {
    
    private MenuItem menuItem;
    private int quantity;

    public CartItem(MenuItem menuItem, int quantity) {
        this.menuItem = menuItem;
        this.quantity = quantity;
    }

    public double calculateSubtotal() {
        return menuItem.getPrice() * quantity;
    }

    // --- Getters and Setters ---

    public MenuItem getMenuItem() {
        return menuItem;
    }

    public void setMenuItem(MenuItem menuItem) {
        this.menuItem = menuItem;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return "CartItem{" +
                "item=" + menuItem.getName() +
                ", quantity=" + quantity +
                ", subtotal=" + calculateSubtotal() +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CartItem cartItem = (CartItem) o;
        return quantity == cartItem.quantity &&
                Objects.equals(menuItem, cartItem.menuItem);
    }

    @Override
    public int hashCode() {
        return Objects.hash(menuItem, quantity);
    }
}