package javaproject1.DAL.Repo.Implementation;

import javaproject1.DAL.DataBase.DBConnection;
import javaproject1.DAL.Entity.Address;
import javaproject1.DAL.Repo.abstraction.IAddressRepo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AddressRepoImpl implements IAddressRepo {

    @Override
    public void addAddress(Address address) {
        String sql = "INSERT INTO address (street, city, building_number) VALUES (?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, address.getStreet());
            stmt.setString(2, address.getCity());
            stmt.setInt(3, address.getBuildingNumber());
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    address.setId(rs.getString(1));
                }
            }

        } catch (SQLException e) {
            System.err.println("Error adding address: " + e.getMessage());
        }
    }

    @Override
    public Address getAddressById(int id) {
        String sql = "SELECT * FROM address WHERE id = ?";
        Address address = null;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    address = extractAddressFromResultSet(rs);
                }
            }

        } catch (SQLException e) {
            System.err.println("Error fetching address by ID: " + e.getMessage());
        }
        return address;
    }

    @Override
    public void updateAddress(Address address) {
        String sql = "UPDATE address SET street = ?, city = ?, building_number = ? WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, address.getStreet());
            stmt.setString(2, address.getCity());
            stmt.setInt(3, address.getBuildingNumber());
            stmt.setString(4, address.getId());

            stmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error updating address: " + e.getMessage());
        }
    }

    @Override
    public void deleteAddress(int id) {
        String sql = "DELETE FROM address WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error deleting address: " + e.getMessage());
        }
    }

    @Override
    public List<Address> getAllAddresses() {
        List<Address> addresses = new ArrayList<>();
        String sql = "SELECT * FROM address";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                addresses.add(extractAddressFromResultSet(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error fetching addresses: " + e.getMessage());
        }
        return addresses;
    }

    // Helper method to convert ResultSet to Address
    private Address extractAddressFromResultSet(ResultSet rs) throws SQLException {
        Address address = new Address();
        address.setId(rs.getString("id"));
        address.setStreet(rs.getString("street"));
        address.setCity(rs.getString("city"));
        address.setBuildingNumber(rs.getInt("building_number"));
        return address;
    }
}
