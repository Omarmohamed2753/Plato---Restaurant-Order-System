package javaproject1.DAL.Repo.abstraction;

import javaproject1.DAL.Entity.Cart;

public interface ICartRepo {
    
    public void addCart(Cart cart);
    public Cart getCartById(int id);
    public void updateCart(Cart cart);
    public void deleteCart(int id);
    
}