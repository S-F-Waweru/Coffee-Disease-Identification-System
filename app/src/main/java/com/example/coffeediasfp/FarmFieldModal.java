package com.example.coffeediasfp;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;

import java.util.ArrayList;
import java.util.List;

public class FarmFieldModal implements Parcelable {

   private String farmName ;
   private String farmSize;
   private String farmID;

    private String farmLocation;

    public FarmFieldModal(){

    }

    public FarmFieldModal(String farmName, String farmSize, String farmID, String farmLocation) {
        this.farmName = farmName;
        this.farmSize = farmSize;
        this.farmID = farmID;
        this.farmLocation = farmLocation;
    }

    public FarmFieldModal(String farmName, String farmSize, String farmID) {
        this.farmName = farmName;
        this.farmSize = farmSize;
        this.farmID = farmID;
        this.farmLocation = null;
    }



    protected FarmFieldModal(Parcel in) {
        farmName = in.readString();
        farmSize = in.readString();
        farmID = in.readString();
        farmLocation = in.readString();
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

    public String getFarmLocation() {
        return farmLocation;
    }

    public void setFarmLocation(String farmLocation) {
        this.farmLocation = farmLocation;
    }



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


    public Boolean isLocationSet(){
        if(getFarmLocation() != null ){
            return true;
        }
        return  false;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeString(farmName);
        parcel.writeString(farmSize);
        parcel.writeString(farmID);
        parcel.writeString(farmLocation);
    }
}
