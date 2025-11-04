package javaproject1.Model;



import java.util.ArrayList;
import java.util.List;

/**
 * Represents a customer's shopping cart.
 * Holds a list of CartItems (Lecture 3 - Collections).
 */
public class Cart {

    private int cartId;
    private double totalPrice;
    private List<CartItem> items;
    
    public Cart() {
        this.items = new ArrayList<>();
    }
    
    /**
     * Adds an item to the cart. If it already exists, increments the quantity.
     */
    public void addItem(CartItem newItem) {
        // Check if item already in cart
        for (CartItem item : items) {
            if (item.getMenuItem().equals(newItem.getMenuItem())) {
                item.setQuantity(item.getQuantity() + newItem.getQuantity());
                return; // Exit after updating
            }
        }
        // If not found, add as new item
        this.items.add(newItem);
    }
    
    public void removeItem(CartItem item) {
        this.items.remove(item);
    }
    
    public double calculateTotal() {
        double total = 0.0;
        for (CartItem item : items) {
            total += item.calculateSubtotal();
        }
        return total;
    }
    
    public void clearCart() {
        this.items.clear();
    }
    
    // --- Getters and Setters ---
    
    public int getCartId() {
        return cartId;
    }
    
    public double getTotalPrice() {
        return totalPrice;
    }


    public List<CartItem> getItems() {
        return items;
    }

    @Override
    public String toString() {
        return "Cart{" +
                "items=" + items +
                ", total=" + calculateTotal() +
                '}';
    }
}
