package javaproject1.BLL.Service.abstraction;

import javaproject1.DAL.Entity.Person;
import java.util.List;

public interface PersonServiceAbs {

    void addPerson(Person person);

    Person getPersonById(int id);

    void updatePerson(Person person);

    void deletePerson(int id);

    List<Person> getAllPersons();
}
