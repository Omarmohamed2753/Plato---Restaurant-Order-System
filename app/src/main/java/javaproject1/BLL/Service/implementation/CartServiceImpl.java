package javaproject1.BLL.Service.implementation;

import javaproject1.BLL.Service.abstraction.ICartService;
import javaproject1.DAL.Entity.Cart;
import javaproject1.DAL.Entity.CartItem;
import javaproject1.DAL.Repo.Implementation.CartRepoImpl;
import javaproject1.DAL.Repo.abstraction.ICartRepo;

import java.util.List;

public class CartServiceImpl implements ICartService {

    private final ICartRepo cartRepo;

    public CartServiceImpl() {
        this(new CartRepoImpl());
    }

    public CartServiceImpl(ICartRepo cartRepo) {
        this.cartRepo = cartRepo;
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
        if (cart == null || newItem == null) return;

        boolean found = false;
        for (CartItem item : cart.getItems()) {
            if (item.getMenuItem().equals(newItem.getMenuItem())) {
                item.setQuantity(item.getQuantity() + newItem.getQuantity());
                item.calculateSubtotal();
                found = true;
                break;
            }
        }
        if (!found) {
            newItem.calculateSubtotal();
            cart.getItems().add(newItem);
        }

        cartRepo.updateCart(cart);
        System.out.println("Item added to cart ID: " + cart.getCartId());
    }

    @Override
    public void removeItem(Cart cart, CartItem item) {
        if (cart == null || item == null) return;
        cart.getItems().remove(item);
        cartRepo.updateCart(cart);
        System.out.println("Item removed from cart ID: " + cart.getCartId());
    }

    @Override
    public double calculateTotal(Cart cart) {
        if (cart == null) return 0.0;
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
        clearCart(cart);
        return total;
    }

    @Override
    public void clearCart(Cart cart) {
        if (cart == null) return;
        cart.getItems().clear();
        cartRepo.updateCart(cart);
        System.out.println("Cart cleared. ID: " + cart.getCartId());
    }
}
