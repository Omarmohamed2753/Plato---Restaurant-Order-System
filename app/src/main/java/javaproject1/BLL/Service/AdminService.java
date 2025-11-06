package javaproject1.Service;

import javaproject1.DAL.Repo.Implementation.UserRepo;
import javaproject1.DAL.Repo.Implementation.MenuRepo;
import javaproject1.DAL.Repo.Implementation.OrderRepo;
import javaproject1.DAL.Entity.User;
import javaproject1.DAL.Entity.MenuItem;
import javaproject1.DAL.Entity.Order;

import java.util.List;

public class AdminService {
    private final UserRepo userRepo = new UserRepo(null);
    private final MenuRepo menuRepo = new MenuRepo();
    private final OrderRepo orderRepo = new OrderRepo(null, userRepo);

}