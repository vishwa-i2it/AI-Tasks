package com.plantstore.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Entity representing a user's address in the e-commerce system.
 * 
 * <p>This entity contains address information for shipping and billing purposes.
 * Users can have multiple addresses.</p>
 * 
 * @author Plant Store Team
 * @version 1.0.0
 * @since 2024-01-01
 */
@Entity
@Table(name = "addresses")
public class Address extends BaseEntity {

    @NotBlank(message = "Street address is required")
    @Size(max = 200, message = "Street address cannot exceed 200 characters")
    @Column(name = "street_address", nullable = false, length = 200)
    private String streetAddress;

    @Size(max = 100, message = "Apartment/suite cannot exceed 100 characters")
    @Column(name = "apartment_suite", length = 100)
    private String apartmentSuite;

    @NotBlank(message = "City is required")
    @Size(max = 100, message = "City cannot exceed 100 characters")
    @Column(name = "city", nullable = false, length = 100)
    private String city;

    @NotBlank(message = "State/province is required")
    @Size(max = 100, message = "State/province cannot exceed 100 characters")
    @Column(name = "state_province", nullable = false, length = 100)
    private String stateProvince;

    @NotBlank(message = "Postal code is required")
    @Pattern(regexp = "^[A-Za-z0-9\\s-]{3,10}$", message = "Postal code should be valid")
    @Column(name = "postal_code", nullable = false, length = 10)
    private String postalCode;

    @NotBlank(message = "Country is required")
    @Size(max = 100, message = "Country cannot exceed 100 characters")
    @Column(name = "country", nullable = false, length = 100)
    private String country;

    @Column(name = "is_default", nullable = false)
    private Boolean isDefault = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "address_type", nullable = false, length = 20)
    private AddressType addressType = AddressType.SHIPPING;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * Enum representing the type of address.
     */
    public enum AddressType {
        SHIPPING, BILLING, BOTH
    }

    /**
     * Default constructor.
     */
    public Address() {
    }

    /**
     * Constructor with basic address information.
     * 
     * @param streetAddress the street address
     * @param city the city
     * @param stateProvince the state or province
     * @param postalCode the postal code
     * @param country the country
     */
    public Address(String streetAddress, String city, String stateProvince, 
                   String postalCode, String country) {
        this.streetAddress = streetAddress;
        this.city = city;
        this.stateProvince = stateProvince;
        this.postalCode = postalCode;
        this.country = country;
    }

    /**
     * Gets the full address as a formatted string.
     * 
     * @return the formatted address
     */
    public String getFullAddress() {
        StringBuilder address = new StringBuilder(streetAddress);
        
        if (apartmentSuite != null && !apartmentSuite.trim().isEmpty()) {
            address.append(", ").append(apartmentSuite);
        }
        
        address.append(", ").append(city)
               .append(", ").append(stateProvince)
               .append(" ").append(postalCode)
               .append(", ").append(country);
        
        return address.toString();
    }

    /**
     * Checks if this address is a shipping address.
     * 
     * @return true if it's a shipping address, false otherwise
     */
    public boolean isShippingAddress() {
        return AddressType.SHIPPING.equals(addressType) || AddressType.BOTH.equals(addressType);
    }

    /**
     * Checks if this address is a billing address.
     * 
     * @return true if it's a billing address, false otherwise
     */
    public boolean isBillingAddress() {
        return AddressType.BILLING.equals(addressType) || AddressType.BOTH.equals(addressType);
    }

    // Getters and Setters

    public String getStreetAddress() {
        return streetAddress;
    }

    public void setStreetAddress(String streetAddress) {
        this.streetAddress = streetAddress;
    }

    public String getApartmentSuite() {
        return apartmentSuite;
    }

    public void setApartmentSuite(String apartmentSuite) {
        this.apartmentSuite = apartmentSuite;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getStateProvince() {
        return stateProvince;
    }

    public void setStateProvince(String stateProvince) {
        this.stateProvince = stateProvince;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Boolean getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(Boolean isDefault) {
        this.isDefault = isDefault;
    }

    public AddressType getAddressType() {
        return addressType;
    }

    public void setAddressType(AddressType addressType) {
        this.addressType = addressType;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "Address{" +
                "id=" + getId() +
                ", streetAddress='" + streetAddress + '\'' +
                ", city='" + city + '\'' +
                ", stateProvince='" + stateProvince + '\'' +
                ", postalCode='" + postalCode + '\'' +
                ", country='" + country + '\'' +
                ", addressType=" + addressType +
                ", isDefault=" + isDefault +
                '}';
    }
} 