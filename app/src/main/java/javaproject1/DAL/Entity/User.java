package javaproject1.DAL.Entity;
import java.util.ArrayList;
import java.util.List;
public class User extends Person {
    private String email;
    private String password;
    private List<Address> addresses;
    private boolean isElite;
    private Subscription subscription;
    private Cart cart;
    private List<Order> orders;
    public User() {}
    public User(int id, String name, int age, String phoneNumber,String email, String password, Address initialAddress) {
        super(id, name, age, phoneNumber);
        this.email = email;
        this.password = password;
        this.addresses = new ArrayList<>();
        if (initialAddress != null) this.addresses.add(initialAddress);
        this.isElite = false;
        this.subscription = null;
        this.cart = new Cart();
        this.orders = new ArrayList<>();
    }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public List<Address> getAddresses() { return addresses; }
    public void setAddresses(List<Address> addresses) { this.addresses = addresses; }
    public boolean isElite() { return isElite; }
    public void setElite(boolean elite) { isElite = elite; }
    public Subscription getSubscription() { return subscription; }
    public void setSubscription(Subscription subscription) { this.subscription = subscription; }
    public Cart getCart() { return cart; }
    public void setCart(Cart cart) { this.cart = cart; }
    public List<Order> getOrders() { return orders; }
    public void setOrders(List<Order> orders) { this.orders = orders; }
    @Override
    public String toString() {
        return "User{" +
                "email='" + email + '\'' +
                ", name='" + name + '\'' +
                ", isElite=" + isElite +
                '}';
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        if (!super.equals(o)) return false;
        User user = (User) o;
        return isElite == user.isElite &&
                email.equals(user.email) &&
                password.equals(user.password) &&
                addresses.equals(user.addresses) &&
                ((subscription == null && user.subscription == null) ||
                 (subscription != null && subscription.equals(user.subscription))) &&
                cart.equals(user.cart) &&
                orders.equals(user.orders);
    }
}
