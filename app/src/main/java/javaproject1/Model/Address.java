package javaproject1.Model;


import java.util.Objects;

/**
 * Represents a physical location.
 * Based on Lecture 1 (Classes, Attributes) and Lecture 2 (Object Class).
 */
public class Address {

    private String street;
    private String city;
    private String buildingNumber;
    private String zipCode;

    public Address(String street, String city, String buildingNumber, String zipCode) {
        this.street = street;
        this.city = city;
        this.buildingNumber = buildingNumber;
        this.zipCode = zipCode;
    }

    /**
     * Combines address components into a single string.
     * @return Full formatted address.
     */
    public String getFullAddress() {
        return buildingNumber + " " + street + ", " + city + ", " + zipCode;
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

    public String getBuildingNumber() {
        return buildingNumber;
    }

    public void setBuildingNumber(String buildingNumber) {
        this.buildingNumber = buildingNumber;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    // --- Overriding Object methods (Lecture 2) ---

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
                Objects.equals(buildingNumber, address.buildingNumber) &&
                Objects.equals(zipCode, address.zipCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(street, city, buildingNumber, zipCode);
    }
}