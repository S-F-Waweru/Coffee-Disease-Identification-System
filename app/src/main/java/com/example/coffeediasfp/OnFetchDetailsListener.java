package com.example.coffeediasfp;

import java.util.ArrayList;

public interface OnFetchDetailsListener
{
    // returns a String for the methods
    String onFarmFetch(String farmID);
    String onDiseaseNameFetch(String diseaseID);
}
