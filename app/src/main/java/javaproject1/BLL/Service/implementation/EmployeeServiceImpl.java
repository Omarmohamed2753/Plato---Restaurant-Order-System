package javaproject1.BLL.Service.implementation;

import javaproject1.BLL.Service.abstraction.IEmployeeService;
import javaproject1.DAL.Entity.Employee;
import javaproject1.DAL.Repo.Implementation.EmployeeRepoImpl;

import java.util.List;

public class EmployeeServiceImpl implements IEmployeeService {

    private final EmployeeRepoImpl employeeRepo;

    public EmployeeServiceImpl() {
        this.employeeRepo = new EmployeeRepoImpl();
    }

    @Override
    public void addEmployee(Employee emp) {
        employeeRepo.addEmployee(emp);
        System.out.println("Employee added: " + emp.getName());
    }

    @Override
    public Employee getEmployeeById(int id) {
        Employee emp = employeeRepo.getEmployeeById(id);
        if (emp == null) {
            System.out.println("Employee not found with ID: " + id);
        }
        return emp;
    }

    @Override
    public void updateEmployee(Employee emp) {
        employeeRepo.updateEmployee(emp);
        System.out.println("Employee updated: " + emp.getName());
    }

    @Override
    public void deleteEmployee(int id) {
        employeeRepo.deleteEmployee(id);
        System.out.println("Employee deleted with ID: " + id);
    }

    @Override
    public List<Employee> getAllEmployees() {
        return employeeRepo.getAllEmployees();
    }

    @Override
    public void getDetails(Employee emp) {
        if (emp == null) {
            System.out.println("Employee is null!");
            return;
        }

        System.out.println("Employee Details:");
        System.out.println("Name: " + emp.getName());
        System.out.println("Age: " + emp.getAge());
        System.out.println("Role: " + emp.getRole());
        System.out.println("Phone Number: " + emp.getPhoneNumber());
        System.out.println("Experience Years: " + emp.getExperiencesYear());

        if (emp.getRestaurant() != null) {
            System.out.println("Restaurant: " + emp.getRestaurant().getName());
        } else {
            System.out.println("Restaurant: Not assigned");
        }
    }
}
