package javaproject1.DAL.Entity;
public class CartItem {
    private int cartItemID;
    private MenuItem menuItem;
    private int quantity;
    private double subPrice;
    public CartItem() {}
    public CartItem(MenuItem menuItem, int quantity) {
        this.menuItem = menuItem;
        this.quantity = Math.max(1, quantity);
        calculateSubPrice();
    }
    public CartItem(int cartItemID, MenuItem menuItem, int quantity) {
        this.cartItemID = cartItemID;
        this.menuItem = menuItem;
        this.quantity = Math.max(1, quantity);
        calculateSubPrice();
    }
    // Auto-calculate subtotal
    private void calculateSubPrice() {
        if (menuItem != null) {
            this.subPrice = menuItem.getPrice() * quantity;
        }
    }
    public int getCartItemID() { return cartItemID; }
    public void setCartItemID(int cartItemID) { this.cartItemID = cartItemID; }
    public MenuItem getMenuItem() { return menuItem; }
    public void setMenuItem(MenuItem menuItem) {
        this.menuItem = menuItem;
        calculateSubPrice(); // Update subtotal if menu item changes
    }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) {
        this.quantity = Math.max(1, quantity);
        calculateSubPrice();
    }
    public double getSubPrice() { return subPrice; }
    public void setSubPrice(double double1) {this.subPrice = double1;}
    @Override
    public String toString() {
        return "CartItem{" +
                "cartItemID=" + cartItemID +
                ", menuItem=" + (menuItem != null ? menuItem.getName() : "null") +
                ", quantity=" + quantity +
                ", subPrice=" + subPrice +
                '}';
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CartItem)) return false;
        CartItem that = (CartItem) o;
        return cartItemID == that.cartItemID;
    }
}
