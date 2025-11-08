package javaproject1.BLL.Service.abstraction;

import javaproject1.DAL.Entity.Employee;
import java.util.List;

public interface IEmployeeService {

    void addEmployee(Employee emp);
    Employee getEmployeeById(int id);
    void updateEmployee(Employee emp);
    void deleteEmployee(int id);
    List<Employee> getAllEmployees();

    void getDetails(Employee emp); 
}
