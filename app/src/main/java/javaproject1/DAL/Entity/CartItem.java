package javaproject1.DAL.Entity;

import java.util.Objects;
public class CartItem {
    private MenuItem menuItem;
    private int cartItemID;
    private double subPrice;
    private int quantity;

    public CartItem(MenuItem menuItem, int quantity) {
        this.menuItem = menuItem;
        this.quantity = Math.max(1, quantity);
        calculateSubtotal();
    }

    public CartItem() {}

    // Calculate subtotal and store it
    public double calculateSubtotal() {
        this.subPrice = this.menuItem.getPrice() * this.quantity;
        return this.subPrice;
    }

    // getters/setters
    public int getCartItemID() { return cartItemID; }
    public void setCartItemID(int cartItemID) { this.cartItemID = cartItemID; }
    public double getSubPrice() { return subPrice; }
    public void setSubPrice(double subPrice) { this.subPrice = subPrice; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; calculateSubtotal(); }
    public MenuItem getMenuItem() { return menuItem; }
    public void setMenuItem(MenuItem menuItem) { this.menuItem = menuItem; calculateSubtotal(); }

    public void updateQuantity() {
        this.quantity++;
        calculateSubtotal();
    }

    @Override
    public String toString() {
        return "CartItem{" +
                "menuItem=" + menuItem +
                ", cartItemID=" + cartItemID +
                ", subPrice=" + subPrice +
                ", quantity=" + quantity +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CartItem cartItem = (CartItem) o;
        return Objects.equals(menuItem, cartItem.menuItem);
    }
}
