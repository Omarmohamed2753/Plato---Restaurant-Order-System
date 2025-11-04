package javaproject1.Model;

import java.util.Objects;

/**
 * Represents an item inside a cart, linking a MenuItem to a quantity.
 */
public class CartItem {
    
    private MenuItem menuItem;
    private int cartItemID;
    private double subPrice;
    
    private int quantity;
    
    public CartItem(MenuItem menuItem, int quantity) {
        this.menuItem = menuItem;
        this.quantity = quantity;
    }
    
    // --- Getters and Setters ---
    
    public void setCartItemID(int cartItemID) {
        this.cartItemID = cartItemID;
    }

    public void setSubPrice(double subPrice) {
        this.subPrice = subPrice;
    }

    public int getCartItemID() {
        return cartItemID;
    }

    public double getSubPrice() {
        return subPrice;
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
                ", quantity=" + quantity +
                ", subtotal=" +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CartItem cartItem = (CartItem) o;
        return quantity == cartItem.quantity;
    }

    @Override
    public int hashCode() {
        return Objects.hash(quantity);
    }

    public MenuItem getMenuItem() {
        return menuItem;
    }

    public void setMenuItem(MenuItem menuItem) {
        this.menuItem = menuItem;
    }
}