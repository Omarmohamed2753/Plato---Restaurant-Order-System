package javaproject1.BLL.Service.implementation;

import javaproject1.DAL.Entity.Person;
import javaproject1.DAL.Repo.Implementation.PersonRepoImpl;
import javaproject1.DAL.Repo.abstraction.IPersonRepo;
import javaproject1.BLL.Service.abstraction.PersonServiceAbs;

import java.util.List;

public class PersonServiceImpl implements PersonServiceAbs {

    private final IPersonRepo personRepo;

    public PersonServiceImpl() {
        this(new PersonRepoImpl());
    }

    public PersonServiceImpl(IPersonRepo personRepo) {
        this.personRepo = personRepo;
    }

    @Override
    public void addPerson(Person person) {
        personRepo.addPerson(person);
    }

    @Override
    public Person getPersonById(int id) {
        return personRepo.getPersonById(id);
    }

    @Override
    public void updatePerson(Person person) {
        personRepo.updatePerson(person);
    }

    @Override
    public void deletePerson(int id) {
        personRepo.deletePerson(id);
    }

    @Override
    public List<Person> getAllPersons() {
        return personRepo.getAllPersons();
    }
}
