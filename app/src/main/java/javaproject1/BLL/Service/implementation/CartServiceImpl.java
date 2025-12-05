package javaproject1.BLL.Service.implementation;

import javaproject1.BLL.Service.abstraction.ICartService;
import javaproject1.DAL.Entity.Cart;
import javaproject1.DAL.Entity.CartItem;
import javaproject1.DAL.Repo.Implementation.CartItemRepoImpl;
import javaproject1.DAL.Repo.Implementation.CartRepoImpl;
import javaproject1.DAL.Repo.abstraction.ICartRepo;

import java.util.List;

public class CartServiceImpl implements ICartService {

    private final ICartRepo cartRepo;
    private final CartItemRepoImpl cartItemRepo;

    public CartServiceImpl() {
        this(new CartRepoImpl());
    }

    public CartServiceImpl(ICartRepo cartRepo) {
        this.cartRepo = cartRepo;
        this.cartItemRepo = new CartItemRepoImpl();
    }

    @Override
    public void addCart(Cart cart) {
        cartRepo.addCart(cart);
        System.out.println("Cart added with ID: " + cart.getCartId());
    }

    @Override
    public Cart getCartById(int id) {
        Cart cart = cartRepo.getCartById(id);
        if (cart == null) {
            System.out.println("Cart not found with ID: " + id);
        }
        return cart;
    }

    @Override
    public void updateCart(Cart cart) {
        cartRepo.updateCart(cart);
        System.out.println("Cart updated with ID: " + cart.getCartId());
    }

    @Override
    public void deleteCart(int id) {
        cartRepo.deleteCart(id);
        System.out.println("Cart deleted with ID: " + id);
    }

    @Override
    public List<Cart> getAllCarts() {
        return cartRepo.getAllCarts();
    }

    @Override
    public void addItem(Cart cart, CartItem newItem) {
        if (cart == null || newItem == null) {
            System.out.println("Cart or item is null!");
            return;
        }

        // Calculate subtotal
        newItem.calculateSubtotal();

        // Check if item already exists in cart (in memory)
        boolean found = false;
        for (CartItem item : cart.getItems()) {
            if (item.getMenuItem().getItemId().equals(newItem.getMenuItem().getItemId())) {
                // Update existing item
                int newQuantity = item.getQuantity() + newItem.getQuantity();
                item.setQuantity(newQuantity);
                item.calculateSubtotal();
                
                // Update in database
                cartItemRepo.updateCartItem(item);
                found = true;
                System.out.println("Updated existing cart item: " + item.getMenuItem().getName() + 
                                 " (new quantity: " + newQuantity + ")");
                break;
            }
        }

        if (!found) {
            // Add new item to database
            cartItemRepo.addCartItemWithCartId(newItem, cart.getCartId());
            
            // Add to in-memory list
            cart.getItems().add(newItem);
            System.out.println("Added new item to cart: " + newItem.getMenuItem().getName());
        }

        // Update cart timestamp if needed
        cartRepo.updateCart(cart);
    }

    @Override
    public void removeItem(Cart cart, CartItem item) {
        if (cart == null || item == null) {
            System.out.println("Cart or item is null!");
            return;
        }

        // Remove from database
        if (item.getCartItemID() > 0) {
            cartItemRepo.deleteCartItem(item.getCartItemID());
        }

        // Remove from in-memory list
        cart.getItems().remove(item);
        
        System.out.println("Item removed from cart ID: " + cart.getCartId());
    }

    @Override
    public double calculateTotal(Cart cart) {
        if (cart == null || cart.getItems() == null) {
            return 0.0;
        }
        
        double total = 0.0;
        for (CartItem item : cart.getItems()) {
            total += item.calculateSubtotal();
        }
        return total;
    }

    @Override
    public double checkout(Cart cart) {
        if (cart == null) return 0.0;
        
        double total = calculateTotal(cart);
        // Note: Don't clear cart here - it will be cleared after order is placed
        return total;
    }

    @Override
    public void clearCart(Cart cart) {
        if (cart == null) {
            System.out.println("Cart is null!");
            return;
        }

        // Delete all cart items from database
        try (java.sql.Connection conn = javaproject1.DAL.DataBase.DBConnection.getConnection()) {
            String sql = "DELETE FROM cart_item WHERE cart_id = ?";
            try (java.sql.PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, cart.getCartId());
                int deleted = stmt.executeUpdate();
                System.out.println("Deleted " + deleted + " cart items from database");
            }
        } catch (Exception e) {
            System.err.println("Error clearing cart from database: " + e.getMessage());
            e.printStackTrace();
        }

        // Clear in-memory list
        cart.getItems().clear();
        
        System.out.println("Cart cleared. ID: " + cart.getCartId());
    }
}