// package javaproject1.DAL.Repo.Implementation;

// import javaproject1.DAL.Entity.Person;
// import javaproject1.DAL.Repo.abstraction.IPersonRepo;
// import javaproject1.plato.JPAUtil;

// import javax.persistence.EntityManager;
// import java.util.ArrayList;
// import java.util.List;

// public class PersonRepoImpl implements IPersonRepo {

//     @Override
//     public void addPerson(Person person) {
//         EntityManager em = JPAUtil.getEntityManager();
//         em.getTransaction().begin();

//         javaproject1.plato. p = new Person();
//         p.setName(person.getName());
//         p.setAge(person.getAge());
//         p.setPhoneNumber(person.getPhoneNumber());

//         em.persist(p);
//         em.getTransaction().commit();

//         person.setId(String.valueOf(p.getId()));
//         em.close();
//     }

//     @Override
//     public Person getPersonById(int id) {
//         EntityManager em = JPAUtil.getEntityManager();

//         Person p =
//                 em.find(Person.class, id);

//         em.close();
//         return p == null ? null : mapToDomain(p);
//     }

//     @Override
//     public void updatePerson(Person person) {
//         EntityManager em = JPAUtil.getEntityManager();
//         em.getTransaction().begin();

//         Person p = em.find(
//                 Person.class,
//                 Integer.parseInt(person.getId())
//         );

//         if (p != null) {
//             p.setName(person.getName());
//             p.setAge(person.getAge());
//             p.setPhoneNumber(person.getPhoneNumber());
//         }

//         em.getTransaction().commit();
//         em.close();
//     }

//     @Override
//     public void deletePerson(int id) {
//         EntityManager em = JPAUtil.getEntityManager();
//         em.getTransaction().begin();

//         Person p =
//                 em.find(Person.class, id);

//         if (p != null) em.remove(p);

//         em.getTransaction().commit();
//         em.close();
//     }

//     @Override
//     public List<Person> getAllPersons() {
//         EntityManager em = JPAUtil.getEntityManager();

//         List<Person> list =
//                 em.createQuery("SELECT p FROM Persons p",
//                         Person.class)
//                         .getResultList();

//         em.close();

//         List<Person> result = new ArrayList<>();
//         for (var p : list) result.add(mapToDomain(p));

//         return result;
//     }

//     // ================= Mapping =================

//     private Person mapToDomain(Person p) {
//         Person person = new Person() {};

//         person.setId(String.valueOf(p.getId()));
//         person.setName(p.getName() != null ? p.getName() : "");
//         person.setAge(p.getAge());
//         person.setPhoneNumber(p.getPhoneNumber() != null ? p.getPhoneNumber() : "");

//         return person;
//     }
// }