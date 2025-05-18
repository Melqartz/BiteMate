package com.example.food_app;

public class User {
    private String name;
    private String uid;

    private String phoneNumber;

    private String email;

    private int points;

    public User(String name, String uid, String phoneNumber,int points) {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
        this.name = name;
        this.uid = uid;
        this.phoneNumber=phoneNumber;
        this.points=points;
    }
    /*

    public User(String name, String uid,String email , String phone) {
        this.name = name;
        this.uid = uid;
        this.email=email;
        this.phoneNumber=phone;
    }
    */


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
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

}
