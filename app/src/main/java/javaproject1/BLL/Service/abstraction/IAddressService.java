package javaproject1.BLL.Service.abstraction;

import javaproject1.DAL.Entity.Address;
import java.util.List;

public interface IAddressService {

    void addAddress(Address address);

    Address getAddressById(int id);

    void updateAddress(Address address);

    void deleteAddress(int id);

    List<Address> getAllAddresses();

    void displayAddresses();
}
