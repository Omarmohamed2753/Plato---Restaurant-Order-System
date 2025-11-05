package javaproject1.DAL.Repo.Implementation;

import javaproject1.DAL.Repo.abstraction.IPersonRepo;
import javaproject1.DAL.Entity.Person;

import java.sql.*;

public class PersonRepo implements IPersonRepo {

    private Connection connection;

    public PersonRepo(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void addPerson(Person person) {
        String sql = "INSERT INTO Person(name, age) VALUES (?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, person.getName());
            stmt.setInt(2, person.getAge());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Person getPersonById(int id) {
        String sql = "SELECT * FROM Person WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Person p = new Person(){};
                p.setId(rs.getInt("id"));
                p.setName(rs.getString("name"));
                p.setAge(rs.getInt("age"));
                return p;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void updatePerson(Person person) {
        String sql = "UPDATE Person SET name = ?, age = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, person.getName());
            stmt.setInt(2, person.getAge());
            stmt.setInt(3, person.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deletePerson(int id) {
        String sql = "DELETE FROM Person WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
