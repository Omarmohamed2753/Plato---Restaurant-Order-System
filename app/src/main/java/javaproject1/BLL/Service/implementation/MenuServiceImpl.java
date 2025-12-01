package javaproject1.BLL.Service.implementation;
import javaproject1.BLL.Service.abstraction.MenuServiceAbs;
import javaproject1.DAL.Entity.Menu;
import javaproject1.DAL.Entity.MenuItem;
import javaproject1.DAL.Entity.Restaurant;
import javaproject1.DAL.Repo.Implementation.MenuRepoImpl;
import javaproject1.DAL.Repo.Implementation.RestaurantRepoImpl;
import javaproject1.DAL.Repo.abstraction.IMenuRepo;
import javaproject1.DAL.Repo.abstraction.IRestaurantRepo;
public class MenuServiceImpl implements MenuServiceAbs {

    private final IRestaurantRepo restaurantRepo;
    private final IMenuRepo menuRepo;

    public MenuServiceImpl() {
        this(new RestaurantRepoImpl(), new MenuRepoImpl());
    }

    public MenuServiceImpl(IRestaurantRepo restaurantRepo, IMenuRepo menuRepo) {
        this.restaurantRepo = restaurantRepo;
        this.menuRepo = menuRepo;
    }

    @Override
    public void addMenu(Menu menu, int restaurantId) {
        Restaurant restaurant = restaurantRepo.getRestaurantById(restaurantId);
        if (restaurant != null) {
            menuRepo.addMenu(menu);
            restaurant.setMenu(menu);
            restaurantRepo.updateRestaurant(restaurant);
            System.out.println("Menu added to restaurant: " + restaurant.getName());
        } else {
            System.out.println("Restaurant not found with id: " + restaurantId);
        }
    }

    @Override
    public Menu getMenuByRestaurant(int restaurantId) {
        Restaurant restaurant = restaurantRepo.getRestaurantById(restaurantId);
        if (restaurant != null) {
            return restaurant.getMenu();
        }
        System.out.println("Restaurant not found with id: " + restaurantId);
        return null;
    }

    @Override
    public void updateMenu(int restaurantId, Menu newMenu) {
        Restaurant restaurant = restaurantRepo.getRestaurantById(restaurantId);
        if (restaurant != null) {
            menuRepo.updateMenu(newMenu);     
            restaurant.setMenu(newMenu);
            restaurantRepo.updateRestaurant(restaurant);
            System.out.println("Menu updated for restaurant: " + restaurant.getName());
        } else {
            System.out.println("Restaurant not found with id: " + restaurantId);
        }
    }

    @Override
    public void addItem(int restaurantId, MenuItem item) {
        Restaurant restaurant = restaurantRepo.getRestaurantById(restaurantId);
        if (restaurant != null) {
            Menu menu = restaurant.getMenu();
            if (menu == null) {
                menu = new Menu();
                menuRepo.addMenu(menu);         
                restaurant.setMenu(menu);
            }
            if (menu.getItems() == null) {
                menu.setItems(new java.util.ArrayList<>());
            }
            if (!menu.getItems().contains(item)) {
                menu.getItems().add(item);
            }
            menuRepo.updateMenu(menu);           
            restaurantRepo.updateRestaurant(restaurant);
            System.out.println("Item " + item.getName() + " added to " + restaurant.getName() + "'s menu.");
        } else {
            System.out.println("Restaurant not found with id: " + restaurantId);
        }
    }

    @Override
    public void removeItem(int restaurantId, MenuItem item) {
        Restaurant restaurant = restaurantRepo.getRestaurantById(restaurantId);
        if (restaurant != null) {
            Menu menu = restaurant.getMenu();
            if (menu != null && menu.getItems() != null && menu.getItems().contains(item)) {
                menu.getItems().remove(item);
                menuRepo.updateMenu(menu);      
                restaurantRepo.updateRestaurant(restaurant);
                System.out.println("Item " + item.getName() + " removed from " + restaurant.getName() + "'s menu.");
            } else {
                System.out.println("Item not found in menu or menu is empty.");
            }
        } else {
            System.out.println("Restaurant not found with id: " + restaurantId);
        }
    }

    @Override
    public void displayMenu(int restaurantId) {
        Restaurant restaurant = restaurantRepo.getRestaurantById(restaurantId);
        if (restaurant != null) {
            Menu menu = restaurant.getMenu();
            if (menu != null && menu.getItems() != null && !menu.getItems().isEmpty()) {
                System.out.println("Menu for " + restaurant.getName() + ":");
                for (MenuItem item : menu.getItems()) {
                    System.out.println(item);
                }
            } else {
                System.out.println("No menu items available for this restaurant.");
            }
        } else {
            System.out.println("Restaurant not found with id: " + restaurantId);
        }
    }
}
