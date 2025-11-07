package javaproject1.DAL.Entity;
import java.util.Objects;
public class Address {
    private int id;
    private String street;
    private String city;
    private int buildingNumber;
    public Address() {}
    public Address(String street, String city, int buildingNumber) {
        this.street = street;
        this.city = city;
        this.buildingNumber = buildingNumber;
    }
    public Address(int id, String street, String city, int buildingNumber) {
        this.id = id;
        this.street = street;
        this.city = city;
        this.buildingNumber = buildingNumber;
    }
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getStreet() { return street; }
    public void setStreet(String street) { this.street = street; }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public int getBuildingNumber() { return buildingNumber; }
    public void setBuildingNumber(int buildingNumber) { this.buildingNumber = buildingNumber; }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Address)) return false;
        Address address = (Address) o;
        return id == address.id &&
                buildingNumber == address.buildingNumber &&
                Objects.equals(street, address.street) &&
                Objects.equals(city, address.city);
    }
    @Override
    public String toString() {
        return "Address{" +
                "id=" + id +
                ", street='" + street + '\'' +
                ", city='" + city + '\'' +
                ", buildingNumber=" + buildingNumber +
                '}';
    }
}
