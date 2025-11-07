package javaproject1.DAL.Repo.abstraction;
import java.util.List;

import javaproject1.DAL.Entity.Person;

public interface IPersonRepo {

    public void addPerson( Person person);
    public  Person getPersonById(int id);
    public void updatePerson(Person person);
    public void deletePerson(int id);
    List<Person> getAllPersons();
}