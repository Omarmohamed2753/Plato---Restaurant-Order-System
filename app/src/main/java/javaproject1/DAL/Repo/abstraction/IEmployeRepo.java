package javaproject1.DAL.Repo.abstraction;

import javaproject1.DAL.Entity.Employee;

public interface IEmployeRepo {
    
    void addEmploye(Employee emp);
    Employee getEmployeById(int id);
    void updateEmploye(Employee emp);
    void deleteEmploye(int id);

}
