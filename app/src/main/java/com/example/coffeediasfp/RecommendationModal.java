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

    private Date timestamp;  // New field for timestamp
    private String date;     // New field for date

    public RecommendationModal(){

    }
    private RecommendationModal(Parcel in) {
        RecommendationID = in.readString();
        diseaseID = in.readString();
        RecommendationText = in.readString();
        date = in.readString();
        timestamp = new Date(in.readLong());
    }

    public RecommendationModal(String recommendationID, String diseaseID, String recommendationText) {
        this.RecommendationID = recommendationID;
        this.diseaseID = diseaseID;
        this.RecommendationText = recommendationText;

        setCurrentTimestampAndDate();
    }

    public  static final Creator<RecommendationModal> CREATOR = new Creator<RecommendationModal>() {
        @Override
        public RecommendationModal createFromParcel(Parcel parcel) {
            return new RecommendationModal();
        }

        @Override
        public RecommendationModal[] newArray(int i) {
            return new RecommendationModal[i];
        }
    };

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeString(RecommendationID);
        parcel.writeString(diseaseID);
        parcel.writeString(RecommendationText);
        parcel.writeString(date);
        parcel.writeLong(timestamp.getTime());
    }
}
