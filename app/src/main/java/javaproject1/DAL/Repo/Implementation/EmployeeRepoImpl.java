package javaproject1.DAL.Repo.Implementation;

import javaproject1.DAL.Entity.Employee;
import javaproject1.DAL.Repo.abstraction.IEmployeeRepo;
import javaproject1.plato.JPAUtil;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;

public class EmployeeRepoImpl implements IEmployeeRepo {

    @Override
    public void addEmployee(Employee employee) {
        EntityManager em = JPAUtil.getEntityManager();
        em.getTransaction().begin();

        javaproject1.plato.Employees e = new javaproject1.plato.Employees();
        e.setName(employee.getName());
        e.setAge(employee.getAge());
        e.setRole(employee.getRole());
        e.setPhoneNumber(employee.getPhoneNumber());
        e.setImagePath(employee.getImagePath() != null ? employee.getImagePath() : "");
        e.setExperiencesYear(employee.getExperiencesYear());

        if (employee.getRestaurant() != null && employee.getRestaurant().getRestaurantId() != null) {
            javaproject1.plato.Restaurants r = em.find(
                    javaproject1.plato.Restaurants.class,
                    Integer.parseInt(employee.getRestaurant().getRestaurantId()));
            e.setRestaurantId(r);
        }

        em.persist(e);
        em.getTransaction().commit();
        employee.setId(String.valueOf(e.getId()));
        em.close();
        System.out.println("Employee added with ID: " + employee.getId());
    }

    @Override
    public Employee getEmployeeById(int id) {
        EntityManager em = JPAUtil.getEntityManager();
        javaproject1.plato.Employees e = em.find(javaproject1.plato.Employees.class, id);
        em.close();
        return e == null ? null : mapToDomain(e);
    }

    @Override
    public void updateEmployee(Employee employee) {
        EntityManager em = JPAUtil.getEntityManager();
        em.getTransaction().begin();

        javaproject1.plato.Employees e = em.find(
                javaproject1.plato.Employees.class, Integer.parseInt(employee.getId()));
        if (e != null) {
            e.setName(employee.getName());
            e.setAge(employee.getAge());
            e.setRole(employee.getRole());
            e.setPhoneNumber(employee.getPhoneNumber());
            e.setImagePath(employee.getImagePath() != null ? employee.getImagePath() : "");
            e.setExperiencesYear(employee.getExperiencesYear());
            em.merge(e);
        }

        em.getTransaction().commit();
        em.close();
    }

    @Override
    public void deleteEmployee(int id) {
        EntityManager em = JPAUtil.getEntityManager();
        em.getTransaction().begin();
        javaproject1.plato.Employees e = em.find(javaproject1.plato.Employees.class, id);
        if (e != null) em.remove(e);
        em.getTransaction().commit();
        em.close();
        System.out.println("Employee deleted with ID: " + id);
    }

    @Override
    public List<Employee> getAllEmployees() {
        EntityManager em = JPAUtil.getEntityManager();
        List<javaproject1.plato.Employees> jpaList = em
                .createQuery("SELECT e FROM Employees e", javaproject1.plato.Employees.class)
                .getResultList();
        em.close();

        List<Employee> result = new ArrayList<>();
        for (javaproject1.plato.Employees e : jpaList) result.add(mapToDomain(e));
        return result;
    }

    private Employee mapToDomain(javaproject1.plato.Employees e) {
        Employee domain = new Employee();
        domain.setId(String.valueOf(e.getId()));
        domain.setName(e.getName());
        domain.setAge(e.getAge());
        domain.setRole(e.getRole());
        domain.setPhoneNumber(e.getPhoneNumber());
        domain.setImagePath(e.getImagePath());
        domain.setExperiencesYear(e.getExperiencesYear() != null ? e.getExperiencesYear() : 0);
        return domain;
    }
}