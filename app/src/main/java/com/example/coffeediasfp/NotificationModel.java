package com.example.coffeediasfp;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.ServerValue;

public class NotificationModel {

    private String notificationID;
    private String farmerID;
    private String diseaseID;
    private String title;
    private String message;
    private int last_count;
    private Object timestamp;

    public NotificationModel(){}

    public NotificationModel(String notificationID, String farmerID, String diseaseID, String title, String message, int last_count) {
        this.notificationID = notificationID;
        this.farmerID = farmerID;
        this.diseaseID = diseaseID;
        this.title = title;
        this.message = message;
        this.last_count = last_count;
        this.timestamp = ServerValue.TIMESTAMP;
    }

    public String getNotificationID() {
        return notificationID;
    }

    public void setNotificationID(String notificationID) {
        this.notificationID = notificationID;
    }

    public String getFarmerID() {
        return farmerID;
    }

    public void setFarmerID(String farmerID) {
        this.farmerID = farmerID;
    }

    public String getDiseaseID() {
        return diseaseID;
    }

    public void setDiseaseID(String diseaseID) {
        this.diseaseID = diseaseID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getLast_count() {
        return last_count;
    }

    public void setLast_count(int last_count) {
        this.last_count = last_count;
    }

    @Exclude
    public Object getTimestamp(){
        return timestamp;
    }

    @Exclude
    public void setTimestamp(Object timestamp) {
        this.timestamp = timestamp;
    }
}
