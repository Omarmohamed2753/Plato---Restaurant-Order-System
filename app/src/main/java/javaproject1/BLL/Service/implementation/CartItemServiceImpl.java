package javaproject1.BLL.Service.implementation;

import javaproject1.BLL.Service.abstraction.ICartItemService;
import javaproject1.DAL.Entity.CartItem;
import javaproject1.DAL.Repo.Implementation.CartItemRepoImpl;

import java.util.List;

public class CartItemServiceImpl implements ICartItemService {

    private final CartItemRepoImpl cartItemRepo;

    public CartItemServiceImpl() {
        this.cartItemRepo = new CartItemRepoImpl();
    }

    @Override
    public void addCartItem(CartItem cartItem) {
        cartItemRepo.addCartItem(cartItem);
        System.out.println("CartItem added: " + cartItem.getMenuItem().getName());
    }

    @Override
    public CartItem getCartItemById(int id) {
        CartItem item = cartItemRepo.getCartItemById(id);
        if (item == null) {
            System.out.println("CartItem not found with ID: " + id);
        }
        return item;
    }

    @Override
    public void updateCartItem(CartItem cartItem) {
        cartItemRepo.updateCartItem(cartItem);
        System.out.println("CartItem updated: " + cartItem.getMenuItem().getName());
    }

    @Override
    public void deleteCartItem(int id) {
        cartItemRepo.deleteCartItem(id);
        System.out.println("CartItem deleted with ID: " + id);
    }

    @Override
    public List<CartItem> getAllCartItems() {
        return cartItemRepo.getAllCartItems();
    }

    @Override
    public double calculateSubtotal(CartItem cartItem) {
        if (cartItem == null || cartItem.getMenuItem() == null) return 0.0;
        double subtotal = cartItem.getMenuItem().getPrice() * cartItem.getQuantity();
        cartItem.setSubPrice(subtotal);
        return subtotal;
    }
}
