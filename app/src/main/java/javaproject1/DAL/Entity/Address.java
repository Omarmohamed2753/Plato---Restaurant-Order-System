package javaproject1.DAL.Entity;

import java.util.Objects;

public class Address {
    private String street;
    private String city;
    private int buildingNumber;

    public Address(String street, String city, int buildingNumber) {
        this.street = street;
        this.city = city;
        this.buildingNumber = buildingNumber;
    }

    // getters/setters
    public String getStreet() { return street; }
    public void setStreet(String street) { this.street = street; }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public int getBuildingNumber() { return buildingNumber; }
    public void setBuildingNumber(int buildingNumber) { this.buildingNumber = buildingNumber; }

    @Override
    public String toString() {
        return "Address{" + street + ", " + city + ", " + buildingNumber + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Address address = (Address) o;
        return buildingNumber == address.buildingNumber &&
                Objects.equals(street, address.street) &&
                Objects.equals(city, address.city);
    }
}
