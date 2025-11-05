package javaproject1.DAL.Repo.abstraction;

import javaproject1.DAL.Entity.Menu;

public interface IMenuRepo {
    public void addMenu(Menu menu);
    public  Menu getMenuById(int id);
    public void updateMenu(Menu menu);
    public void deleteMenu(int id);
}
