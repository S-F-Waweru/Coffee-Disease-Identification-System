package com.example.coffeediasfp;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class RecommendationModal implements Parcelable {
    String RecommendationID;
    String diseaseID;
    String RecommendationText;


    public  RecommendationModal(){

    }
    public RecommendationModal(String recommendationID, String diseaseID, String recommendationText) {
        RecommendationID = recommendationID;
        this.diseaseID = diseaseID;
        RecommendationText = recommendationText;
    }

    protected RecommendationModal(Parcel in) {
        RecommendationID = in.readString();
        diseaseID = in.readString();
        RecommendationText = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(RecommendationID);
        dest.writeString(diseaseID);
        dest.writeString(RecommendationText);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<RecommendationModal> CREATOR = new Creator<RecommendationModal>() {
        @Override
        public RecommendationModal createFromParcel(Parcel in) {
            return new RecommendationModal(in);
        }

        @Override
        public RecommendationModal[] newArray(int size) {
            return new RecommendationModal[size];
        }
    };

    public String getRecommendationID(){
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
