package javaproject1.DAL.Repo.Implementation;

import javaproject1.DAL.Repo.abstraction.IPersonRepo;
import javaproject1.DAL.Entity.Person;
import javaproject1.DAL.DataBase.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PersonRepoImpl implements IPersonRepo {

    @Override
    public void addPerson(Person person) {
        String sql = "INSERT INTO persons (name, age, phone_number) VALUES (?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, person.getName());
            stmt.setInt(2, person.getAge());
            stmt.setString(3, person.getPhoneNumber());
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Person getPersonById(int id) {
        String sql = "SELECT * FROM persons WHERE id = ?";
        Person person = null;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                person = mapToPerson(rs);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return person;
    }

    @Override
    public void updatePerson(Person person) {
        String sql = "UPDATE persons SET name = ?, age = ?, phone_number = ? WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, person.getName());
            stmt.setInt(2, person.getAge());
            stmt.setString(3, person.getPhoneNumber());
            stmt.setString(4, person.getId());
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deletePerson(int id) {
        String sql = "DELETE FROM persons WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Person> getAllPersons() {
        String sql = "SELECT * FROM persons";
        List<Person> persons = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                persons.add(mapToPerson(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return persons;
    }

    private Person mapToPerson(ResultSet rs) throws SQLException {
        Person p = new Person() {};
        p.setId(rs.getString("id"));
        p.setName(rs.getString("name"));
        p.setAge(rs.getInt("age"));
        p.setPhoneNumber(rs.getString("phone_number"));
        return p;
    }
}
