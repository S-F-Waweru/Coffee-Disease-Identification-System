package com.example.coffeediasfp;

public class RecommendationModal {
    String RecommendationID;
    String diseaseID;
    String RecommendationText;

    public RecommendationModal(){

    }

    public RecommendationModal(String recommendationID, String diseaseID, String recommendationText) {
        RecommendationID = recommendationID;
        this.diseaseID = diseaseID;
        RecommendationText = recommendationText;
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
}
