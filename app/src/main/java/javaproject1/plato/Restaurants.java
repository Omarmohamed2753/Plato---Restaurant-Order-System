/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javaproject1.plato;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Set;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author Omar
 */
@Entity
@Table(name = "restaurants")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Restaurants.findAll", query = "SELECT r FROM Restaurants r")
    , @NamedQuery(name = "Restaurants.findByRestaurantId", query = "SELECT r FROM Restaurants r WHERE r.restaurantId = :restaurantId")
    , @NamedQuery(name = "Restaurants.findByName", query = "SELECT r FROM Restaurants r WHERE r.name = :name")
    , @NamedQuery(name = "Restaurants.findByAddress", query = "SELECT r FROM Restaurants r WHERE r.address = :address")
    , @NamedQuery(name = "Restaurants.findByPhoneNumber", query = "SELECT r FROM Restaurants r WHERE r.phoneNumber = :phoneNumber")
    , @NamedQuery(name = "Restaurants.findByEmail", query = "SELECT r FROM Restaurants r WHERE r.email = :email")
    , @NamedQuery(name = "Restaurants.findByOpeningHours", query = "SELECT r FROM Restaurants r WHERE r.openingHours = :openingHours")
    , @NamedQuery(name = "Restaurants.findByRating", query = "SELECT r FROM Restaurants r WHERE r.rating = :rating")
    , @NamedQuery(name = "Restaurants.findByImagePath", query = "SELECT r FROM Restaurants r WHERE r.imagePath = :imagePath")})
public class Restaurants implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "restaurant_id")
    private Integer restaurantId;
    @Basic(optional = false)
    @Column(name = "name")
    private String name;
    @Column(name = "address")
    private String address;
    @Column(name = "phone_number")
    private String phoneNumber;
    @Column(name = "email")
    private String email;
    @Column(name = "opening_hours")
    private String openingHours;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "rating")
    private BigDecimal rating;
    @Column(name = "image_path")
    private String imagePath;
    @OneToMany(mappedBy = "restaurantId")
    private Set<Admin> adminSet;
    @OneToMany(mappedBy = "restaurantId")
    private Set<Menu> menuSet;
    @OneToMany(mappedBy = "restaurantId")
    private Set<Reviews> reviewsSet;
    @OneToMany(mappedBy = "restaurantId")
    private Set<Orders> ordersSet;
    @OneToMany(mappedBy = "restaurantId")
    private Set<Employees> employeesSet;

    public Restaurants() {
    }

    public Restaurants(Integer restaurantId) {
        this.restaurantId = restaurantId;
    }

    public Restaurants(Integer restaurantId, String name) {
        this.restaurantId = restaurantId;
        this.name = name;
    }

    public Integer getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(Integer restaurantId) {
        this.restaurantId = restaurantId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getOpeningHours() {
        return openingHours;
    }

    public void setOpeningHours(String openingHours) {
        this.openingHours = openingHours;
    }

    public BigDecimal getRating() {
        return rating;
    }

    public void setRating(BigDecimal rating) {
        this.rating = rating;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    @XmlTransient
    public Set<Admin> getAdminSet() {
        return adminSet;
    }

    public void setAdminSet(Set<Admin> adminSet) {
        this.adminSet = adminSet;
    }

    @XmlTransient
    public Set<Menu> getMenuSet() {
        return menuSet;
    }

    public void setMenuSet(Set<Menu> menuSet) {
        this.menuSet = menuSet;
    }

    @XmlTransient
    public Set<Reviews> getReviewsSet() {
        return reviewsSet;
    }

    public void setReviewsSet(Set<Reviews> reviewsSet) {
        this.reviewsSet = reviewsSet;
    }

    @XmlTransient
    public Set<Orders> getOrdersSet() {
        return ordersSet;
    }

    public void setOrdersSet(Set<Orders> ordersSet) {
        this.ordersSet = ordersSet;
    }

    @XmlTransient
    public Set<Employees> getEmployeesSet() {
        return employeesSet;
    }

    public void setEmployeesSet(Set<Employees> employeesSet) {
        this.employeesSet = employeesSet;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (restaurantId != null ? restaurantId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Restaurants)) {
            return false;
        }
        Restaurants other = (Restaurants) object;
        if ((this.restaurantId == null && other.restaurantId != null) || (this.restaurantId != null && !this.restaurantId.equals(other.restaurantId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "plato.Restaurants[ restaurantId=" + restaurantId + " ]";
    }
    
}
