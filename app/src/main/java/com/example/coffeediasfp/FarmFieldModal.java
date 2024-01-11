package com.example.coffeediasfp;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

public class FarmFieldModal implements Parcelable {

   private String farmName ;
   private String farmSize;
   private String farmID;


    public FarmFieldModal(){

    }


    public FarmFieldModal(String farmName, String farmSize, String farmID) {
        this.farmName = farmName;
        this.farmSize = farmSize;
        this.farmID = farmID;
    }

    protected FarmFieldModal(Parcel in){
        farmName = in.readString();
        farmSize = in.readString();
        farmID = in.readString();
    }
    public static final Creator<FarmFieldModal> CREATOR = new Creator<FarmFieldModal>() {
        @Override
        public FarmFieldModal createFromParcel(Parcel in) {
            return new FarmFieldModal(in);
        }

        @Override
        public FarmFieldModal[] newArray(int size) {
            return new FarmFieldModal[size];
        }
    };

    public String getFarmID() {
        return farmID;
    }

    public void setFarmID(String farmID) {
        this.farmID = farmID;
    }

    public String getFarmName() {
        return farmName;
    }
    public void setFarmName(String farmName) {
        this.farmName = farmName;
    }

    public String getFarmSize() {
        return farmSize;
    }

    public void setFarmSize(String farmSize) {
        this.farmSize = farmSize;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(farmName);
        parcel.writeString(farmSize);
        parcel.writeString(farmID);
    }
}
