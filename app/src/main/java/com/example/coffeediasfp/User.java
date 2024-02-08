package com.example.coffeediasfp;

public class User {
    private String userId;
    private  String email;
    private  String fullName;
    private String phoneNumber;
    // what kind of user farmer, Officer, Agrovet Owner;
//    private String status;
    private Boolean isFarmer = true;
    private Boolean IsAgrovetOwner = false;
    private  Boolean IsAdmin = false;

    public Boolean getFarmer() {
        return isFarmer;
    }

    public void setFarmer(Boolean farmer) {
        isFarmer = farmer;
    }

    public Boolean getAgrovetOwner() {
        return IsAgrovetOwner;
    }

    public void setAgrovetOwner(Boolean agrovetOwner) {
        IsAgrovetOwner = agrovetOwner;
    }

    public Boolean getAdmin() {
        return IsAdmin;
    }

    public void setAdmin(Boolean admin) {
        IsAdmin = admin;
    }

    public User(){
    }

    public User(String userId, String email, String fullName, String phoneNumber) {
        this.userId = userId;
        this.email = email;
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

}
