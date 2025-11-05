package javaproject1.DAL.Entity;

import java.util.ArrayList;
import java.util.List;

public class Menu {
    private int menuId;
    private List<MenuItem> items;
    
    public void setMenuId(int menuId) {
        this.menuId = menuId;
    }

    public int getMenuId() {
        return menuId;
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
