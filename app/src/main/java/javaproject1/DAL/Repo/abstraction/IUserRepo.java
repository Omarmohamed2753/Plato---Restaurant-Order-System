package javaproject1.DAL.Repo.abstraction;
import java.util.List;

import javaproject1.DAL.Entity.User;

public interface IUserRepo {

    public void addUser( User user);
    public  User getUserById(int id);
    public void updateUser(User user);
    public void deleteUser(int id);
    List<User> getAllUsers();
    
}