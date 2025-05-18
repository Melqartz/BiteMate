package com.example.food_app.models;

public class Address {
    private String city;
    private String building;
    private String street;
    private String floor;

    private String coordinates;

    public Address() {
        // Default constructor required for Firebase database
    }

    public Address(String city, String building, String street, String floor,String coordinates) {
        this.city = city;
        this.building = building;
        this.street = street;
        this.floor = floor;
        this.coordinates=coordinates;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getBuilding() {
        return building;
    }

    public void setBuilding(String building) {
        this.building = building;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getFloor() {
        return floor;
    }

    public void setFloor(String floor) {
        this.floor = floor;
    }

    public String getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(String coordinates) {
        this.coordinates = coordinates;
    }

    @Override
    public String toString() {
        return "City: " + city + ", Building: " + building + ", Street: " + street + ", Floor: " + floor;
    }
}
