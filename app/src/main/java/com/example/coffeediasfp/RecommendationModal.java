package com.example.coffeediasfp;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class RecommendationModal {
    String RecommendationID;
    String diseaseID;
    String RecommendationText;

    private Date timestamp;  // New field for timestamp
    private String date;     // New field for date

    public RecommendationModal(){

    }

    public RecommendationModal(String recommendationID, String diseaseID, String recommendationText) {
        this.RecommendationID = recommendationID;
        this.diseaseID = diseaseID;
        this.RecommendationText = recommendationText;
    }

    public String getRecommendationID() {
        return RecommendationID;
    }

    public void setRecommendationID(String recommendationID) {
        RecommendationID = recommendationID;
    }

    public String getDiseaseID() {
        return diseaseID;
    }

    public void setDiseaseID(String diseaseID) {
        this.diseaseID = diseaseID;
    }

    public String getRecommendationText() {
        return RecommendationText;
    }

    public void setRecommendationText(String recommendationText) {
        RecommendationText = recommendationText;
    }
    public void setCurrentTimestampAndDate() {
        this.timestamp = new Date();
        // Format the date as needed (e.g., "yyyy-MM-dd HH:mm:ss")

        this.date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(timestamp);
    }
}
