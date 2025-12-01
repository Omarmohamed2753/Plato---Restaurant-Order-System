package javaproject1.DAL.Entity;
import java.util.ArrayList;
import java.util.List;

public class Menu {
    private String menuId;
    private List<MenuItem> items;
    public String getMenuId() {
        return menuId;
    }
    public void setMenuId(String menuId) {
        this.menuId = menuId;
    }
    public Menu() {
        this.items = new ArrayList<>();
    }

    public List<MenuItem> getItems() {
        return items;
    }

    public void setItems(List<MenuItem> items) {
        this.items = items;
    }
    @Override
    public String toString() {
        return "Menu{" +
                "items=" + items +
                '}';
    }
}
