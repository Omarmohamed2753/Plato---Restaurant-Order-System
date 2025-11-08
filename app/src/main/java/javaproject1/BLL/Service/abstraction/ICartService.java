package javaproject1.BLL.Service.abstraction;

import javaproject1.DAL.Entity.Cart;
import javaproject1.DAL.Entity.CartItem;
import java.util.List;

public interface ICartService {

    void addCart(Cart cart);
    Cart getCartById(int id);
    void updateCart(Cart cart);
    void deleteCart(int id);
    List<Cart> getAllCarts();
    void addItem(Cart cart, CartItem newItem);
    void removeItem(Cart cart, CartItem item);
    double calculateTotal(Cart cart);
    double checkout(Cart cart);
    void clearCart(Cart cart);
}
