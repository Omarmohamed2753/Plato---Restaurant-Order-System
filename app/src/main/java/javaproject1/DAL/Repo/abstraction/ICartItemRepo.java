package javaproject1.DAL.Repo.abstraction;

import java.util.List;
import javaproject1.DAL.Entity.CartItem;

public interface ICartItemRepo {
    
    public void addCartItem(CartItem cartItem);
    public CartItem getCartItemById(int id);
    public void updateCartItem(CartItem cartItem);
    public void deleteCartItem(int id);
    List<CartItem> getAllCartItems();
    
}