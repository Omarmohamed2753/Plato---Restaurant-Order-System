package javaproject1.DAL.Repo.abstraction;

import java.util.List;
import javaproject1.DAL.Entity.Admin;

public interface IAdminRepo {
    
    public void addAdmin(Admin admin);
    public Admin getAdminById(int id);
    public void updateAdmin(Admin admin);
    public void deleteAdmin(int id);
    List<Admin> getAllAdmins();
    
}