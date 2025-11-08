    package javaproject1.DAL.Entity;
    import java.util.ArrayList;
    import java.util.List;
    public class Menu {
        private int menuId;
        private List<MenuItem> items;
        public void setMenuId(int menuId) {this.menuId = menuId;}
        public int getMenuId() {return menuId;}
        public Menu() {this.items = new ArrayList<>();}
        public List<MenuItem> getItems() {return items;}
        public void setItems(List<MenuItem> items) {this.items = items;}
        public void addItem(MenuItem item) {
        if (item != null) {
            items.add(item);
        }
    }

    public void removeItem(MenuItem item) {
        if (item != null) {
            items.remove(item);
        }
    }

    public void displayMenu() {
        if (items.isEmpty()) {
            System.out.println("Menu is empty");
            return;
        }
        for (MenuItem item : items) {
            System.out.println(item.getName() + " - " + item.getPrice());
        }
    }

        @Override
        public String toString() {
            return "Menu{" +
                    "items=" + items +
                    '}';
        }
        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            Menu other = (Menu) obj;
            return menuId == other.menuId && items.equals(other.items);
        }
    }
