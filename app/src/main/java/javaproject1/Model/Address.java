package javaproject1.Model;


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

    /**
     * Combines address components into a single string.
     * @return Full formatted address.
     */
    public String getFullAddress() {
        return buildingNumber + " " + street + ", " + city ;
    }

    // --- Getters and Setters ---

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public int getBuildingNumber() {
        return buildingNumber;
    }

    public void setBuildingNumber(int buildingNumber) {
        this.buildingNumber = buildingNumber;
    }

    // --- Overriding Object methods ---

    @Override
    public String toString() {
        return "Address{" +
                "fullAddress='" + getFullAddress() + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Address address = (Address) o;
        return Objects.equals(street, address.street) &&
                Objects.equals(city, address.city) &&
                Objects.equals(buildingNumber, address.buildingNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(street, city, buildingNumber);
    }
}