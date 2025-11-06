package javaproject1.DAL.Repo.abstraction;

import java.util.List;
import javaproject1.DAL.Entity.Address;

public interface IAddressRepo {

    void addAddress(Address address);
    Address getAddressById(int id);
    void updateAddress(Address address);
    void deleteAddress(int id);
    List<Address> getAllAddresses();
}
