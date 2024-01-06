package com.example.coffeediasfp;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class DiseaseModal implements Parcelable {
   private String diseaseName;
   private String diseaseDescription;
   private String diseaseID;

    public DiseaseModal(String diseaseName, String diseaseDescription, String diseaseID) {
        this.diseaseName = diseaseName;
        this.diseaseDescription = diseaseDescription;
        this.diseaseID = diseaseID;
    }

    protected DiseaseModal(Parcel in) {
        diseaseName = in.readString();
        diseaseDescription = in.readString();
        diseaseID = in.readString();
    }

    public static final Creator<DiseaseModal> CREATOR = new Creator<DiseaseModal>() {
        @Override
        public DiseaseModal createFromParcel(Parcel in) {
            return new DiseaseModal(in);
        }

        @Override
        public DiseaseModal[] newArray(int size) {
            return new DiseaseModal[size];
        }
    };

    public String getDiseaseID() {
        return diseaseID;
    }

    public void setDiseaseID(String diseaseID) {
        this.diseaseID = diseaseID;
    }

    public String getDiseaseName() {
        return diseaseName;
    }

    public void setDiseaseName(String diseaseName) {
        this.diseaseName = diseaseName;
    }

    public String getDiseaseDescription() {
        return diseaseDescription;
    }

    public void setDiseaseDescription(String diseaseDescription) {
        this.diseaseDescription = diseaseDescription;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeString(diseaseName);
        parcel.writeString(diseaseDescription);
        parcel.writeString(diseaseID);
    }
}
