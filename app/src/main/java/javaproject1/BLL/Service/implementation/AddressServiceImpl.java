package javaproject1.BLL.Service.implementation;

import javaproject1.BLL.Service.abstraction.IAddressService;
import javaproject1.DAL.Entity.Address;
import javaproject1.DAL.Repo.Implementation.AddressRepoImpl;

import java.util.List;

public class AddressServiceImpl implements IAddressService {

    private final AddressRepoImpl addressRepo;

    public AddressServiceImpl() {
        this.addressRepo = new AddressRepoImpl();
    }

    @Override
    public void addAddress(Address address) {
        if (address == null) {
            System.out.println("Cannot add null address!");
            return;
        }
        addressRepo.addAddress(address);
        System.out.println("Address added with ID: " + address.getId());
    }

    @Override
    public Address getAddressById(int id) {
        Address address = addressRepo.getAddressById(id);
        if (address == null) {
            System.out.println("Address not found with ID: " + id);
        }
        return address;
    }

    @Override
    public void updateAddress(Address address) {
        if (address == null) {
            System.out.println("Cannot update null address!");
            return;
        }
        addressRepo.updateAddress(address);
        System.out.println("Address updated with ID: " + address.getId());
    }

    @Override
    public void deleteAddress(int id) {
        addressRepo.deleteAddress(id);
        System.out.println("Address deleted with ID: " + id);
    }

    @Override
    public List<Address> getAllAddresses() {
        List<Address> addresses = addressRepo.getAllAddresses();
        if (addresses.isEmpty()) {
            System.out.println("No addresses found.");
        }
        return addresses;
    }

    @Override
    public void displayAddresses() {
        List<Address> addresses = getAllAddresses();
        for (Address a : addresses) {
            System.out.println("ID: " + a.getId() + " | Street: " + a.getStreet() +
                               " | City: " + a.getCity() + " | Building: " + a.getBuildingNumber());
        }
    }
}
