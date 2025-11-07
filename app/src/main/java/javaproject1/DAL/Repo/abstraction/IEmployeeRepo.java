package javaproject1.DAL.Repo.abstraction;

import java.util.List;

import javaproject1.DAL.Entity.Employee;

public interface IEmployeeRepo {
    
    void addEmployee(Employee emp);
    Employee getEmployeeById(int id);
    void updateEmployee(Employee emp);
    void deleteEmployee(int id);
    List<Employee> getAllEmployees();
}
