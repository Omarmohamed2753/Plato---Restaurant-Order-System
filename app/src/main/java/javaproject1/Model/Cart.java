package javaproject1.Model;

import java.util.ArrayList;
import java.util.List;

public class Cart {
    private int cartId;
    private List<CartItem> items;

    public Cart() {
        this.items = new ArrayList<>();
    }
    public int getCartId() {
        return cartId;
    }
    public void setCartId(int cartId) {
        this.cartId = cartId;
    }
    public List<CartItem> getItems() {
        return items;
    }

    public void setItems(List<CartItem> items) {
        this.items = items;
    }

    public void addItem(CartItem newItem) {
        for (CartItem item : items) {
            if (item.getMenuItem().equals(newItem.getMenuItem())) {
                item.setQuantity(item.getQuantity() + newItem.getQuantity());
                item.calculateSubtotal();
                return;
            }
        }
        newItem.calculateSubtotal();
        this.items.add(newItem);
    }

    public void removeItem(CartItem item) {
        this.items.remove(item);
    }

    public double checkout() {
        double total = calculateTotal();
        clearCart();
        return total;
    }

    public void clearCart() {
        this.items.clear();
    }

    public double calculateTotal() {
        double total = 0.0;
        for (CartItem item : items) {
            total += item.calculateSubtotal();
        }
        return total;
    }

    @Override
    public String toString() {
        return "Cart{" +
                "items=" + items +
                '}';
    }
}
