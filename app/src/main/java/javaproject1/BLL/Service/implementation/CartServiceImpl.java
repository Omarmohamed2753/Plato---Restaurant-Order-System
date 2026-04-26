package javaproject1.BLL.Service.implementation;

import javaproject1.BLL.Service.abstraction.ICartService;
import javaproject1.DAL.Entity.Cart;
import javaproject1.DAL.Entity.CartItem;
import javaproject1.DAL.Repo.Implementation.CartItemRepoImpl;
import javaproject1.DAL.Repo.Implementation.CartRepoImpl;
import javaproject1.DAL.Repo.abstraction.ICartRepo;
import javaproject1.plato.JPAUtil;

import javax.persistence.EntityManager;
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

        newItem.calculateSubtotal();

        boolean found = false;
        for (CartItem item : cart.getItems()) {
            if (item.getMenuItem().getItemId().equals(newItem.getMenuItem().getItemId())) {
                int newQuantity = item.getQuantity() + newItem.getQuantity();
                item.setQuantity(newQuantity);
                item.calculateSubtotal();
                cartItemRepo.updateCartItem(item);
                found = true;
                System.out.println("Updated existing cart item: " + item.getMenuItem().getName()
                        + " (new quantity: " + newQuantity + ")");
                break;
            }
        }

        if (!found) {
            cartItemRepo.addCartItemWithCartId(newItem, cart.getCartId());
            cart.getItems().add(newItem);
            System.out.println("Added new item to cart: " + newItem.getMenuItem().getName());
        }

        cartRepo.updateCart(cart);
    }

    @Override
    public void removeItem(Cart cart, CartItem item) {
        if (cart == null || item == null) {
            System.out.println("Cart or item is null!");
            return;
        }

        if (item.getCartItemID() > 0) {
            cartItemRepo.deleteCartItem(item.getCartItemID());
        }

        cart.getItems().remove(item);
        System.out.println("Item removed from cart ID: " + cart.getCartId());
    }

    @Override
    public double calculateTotal(Cart cart) {
        if (cart == null || cart.getItems() == null) return 0.0;

        double total = 0.0;
        for (CartItem item : cart.getItems()) {
            total += item.calculateSubtotal();
        }
        return total;
    }

    @Override
    public double checkout(Cart cart) {
        if (cart == null) return 0.0;
        return calculateTotal(cart);
    }

    /**
     * Deletes all cart_item rows that belong to this cart via JPA,
     * then clears the in-memory list.
     * No raw JDBC — EntityManager handles everything.
     */
    @Override
    public void clearCart(Cart cart) {
        if (cart == null) {
            System.out.println("Cart is null!");
            return;
        }

        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();

            int deleted = em
                    .createQuery("DELETE FROM CartItem ci WHERE ci.cartId.cartId = :cartId")
                    .setParameter("cartId", cart.getCartId())
                    .executeUpdate();

            em.getTransaction().commit();
            System.out.println("Deleted " + deleted
                    + " cart items via JPA for cart " + cart.getCartId());
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            System.err.println("Error clearing cart: " + e.getMessage());
            e.printStackTrace();
        } finally {
            em.close();
        }

        cart.getItems().clear();
        System.out.println("Cart cleared. ID: " + cart.getCartId());
    }
}