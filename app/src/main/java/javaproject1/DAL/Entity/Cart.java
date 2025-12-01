package javaproject1.DAL.Entity;
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
    @Override
    public String toString() {
        return "Cart{" +
                "items=" + items +
                '}';
    }
}
