package javaproject1.BLL.Service.abstraction;

import javaproject1.DAL.Entity.CartItem;
import java.util.List;

public interface ICartItemService {

    void addCartItem(CartItem cartItem);
    CartItem getCartItemById(int id);
    void updateCartItem(CartItem cartItem);
    void deleteCartItem(int id);
    List<CartItem> getAllCartItems();

    double calculateSubtotal(CartItem cartItem);
}
