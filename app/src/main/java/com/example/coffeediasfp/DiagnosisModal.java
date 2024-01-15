package com.example.coffeediasfp;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.ArrayList;

public class DiagnosisModal implements Parcelable {

    private String diagnosisID;
    private String farmID;
    private String diseaseID;
    private String recommendations;
    private String longitude;
    private String latitude;

    public DiagnosisModal (){}

    public DiagnosisModal(String diagnosisID, String farmID, String diseaseID, String recommendations, String longitude, String latitude) {
        this.diagnosisID = diagnosisID;
        this.farmID = farmID;
        this.diseaseID = diseaseID;
        this.recommendations = recommendations;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    protected DiagnosisModal(Parcel in) {
        diagnosisID = in.readString();
        farmID = in.readString();
        diseaseID = in.readString();
        recommendations = in.readString();
        longitude = in.readString();
        latitude = in.readString();
    }

    public static final Creator<DiagnosisModal> CREATOR = new Creator<DiagnosisModal>() {
        @Override
        public DiagnosisModal createFromParcel(Parcel in) {
            return new DiagnosisModal(in);
        }

        @Override
        public DiagnosisModal[] newArray(int size) {
            return new DiagnosisModal[size];
        }
    };

    public String getDiseaseID() {
        return diseaseID;
    }

    public void setDiseaseID(String diseaseID) {
        this.diseaseID = diseaseID;
    }

    public String getRecommendations() {
        return recommendations;
    }

    public void setRecommendations(String recommendations) {
        this.recommendations = recommendations;
    }

    public String getDiagnosisID() {
        return diagnosisID;
    }

    public void setDiagnosisID(String diagnosisID) {
        this.diagnosisID = diagnosisID;
    }

    public String getFarmID() {
        return farmID;
    }

    public void setFarmID(String farmID) {
        this.farmID = farmID;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeString(diagnosisID);
        parcel.writeString(farmID);
        parcel.writeString(diseaseID);
        parcel.writeString(recommendations);
        parcel.writeString(longitude);
        parcel.writeString(latitude);
    }
}
