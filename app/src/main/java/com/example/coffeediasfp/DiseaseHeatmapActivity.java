package com.example.coffeediasfp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DiseaseHeatmapActivity extends AppCompatActivity implements OnMapReadyCallback {

    GoogleMap mMap;
    String farmId;
    String diseaseID;
    Spinner diseaseSpinner, farmSpinner;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_disease_heatmap);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


//        gets the Diseases and loads them to Disease Spinner
        getDisease();
//        gets the Farms and loads them to Farms Spinner and generate farm Boundaries
        getFarms();
//        gets Diagnosis and generates heatMaps
        getDiagnosis();
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

    }

// get all infromation from the Diagnisis Node
    public  void getDiagnosis(){
        ArrayList<DiagnosisModal> diagnosisModalArrayList;
        diagnosisModalArrayList = new ArrayList<>();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Diagnosis");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snapshot1 : snapshot.getChildren()){
                    DiagnosisModal diagnosisModal = snapshot1.getValue(DiagnosisModal.class);
                    diagnosisModalArrayList.add(diagnosisModal);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(DiseaseHeatmapActivity.this, "Error occurred " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void getDisease(){
        ArrayList<DiseaseModal> diseaseModalArrayList = new ArrayList<>();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Diseases");
         databaseReference.addValueEventListener(new ValueEventListener() {
             @Override
             public void onDataChange(@NonNull DataSnapshot snapshot) {
                 for(DataSnapshot snapshot1 : snapshot.getChildren()){
                    DiseaseModal diseaseModal = snapshot1.getValue(DiseaseModal.class);
                    diseaseModalArrayList.add(diseaseModal);
                    populateDiseaseSpinner(diseaseModalArrayList);
                 }
             }
             @Override
             public void onCancelled(@NonNull DatabaseError error) {
                 Toast.makeText(DiseaseHeatmapActivity.this, "Failed to load Diseases" +error, Toast.LENGTH_SHORT).show();
             }
         });
    }
    public void populateDiseaseSpinner(ArrayList<DiseaseModal> diseaseModalArrayList){
        Map<String, String> diseaseMap = new HashMap<>();
        for(DiseaseModal disease : diseaseModalArrayList){
            diseaseMap.put( disease.getDiseaseName(), disease.getDiseaseID());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                new ArrayList<>(diseaseMap.keySet())
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        diseaseSpinner = findViewById(R.id.idSpinnerDisease);
        diseaseSpinner.setAdapter(adapter);
        diseaseSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selectDiseaseName = (String) adapterView.getItemAtPosition(i);
                diseaseID = diseaseMap.get(selectDiseaseName);
                // generate a heatMap for the diagnosis with this diseaseName

            };

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // generate heat maps of all diseasea

            }
        });

    }

    public  void getFarms(){
        ArrayList<FarmFieldModal> farmFieldModalArrayList = new ArrayList<>();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Farms");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot snapshot1: snapshot.getChildren()){
                    FarmFieldModal farmFieldModal =snapshot1.getValue(FarmFieldModal.class);
                    farmFieldModalArrayList.add(farmFieldModal);
                    //polpulate sinner and show famr boundaries that are selected
                    populateFarmSpinner(farmFieldModalArrayList, databaseReference);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(DiseaseHeatmapActivity.this, "Failed to load Farms" +error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void populateFarmSpinner(ArrayList<FarmFieldModal>farmFieldModalArrayList, DatabaseReference databaseReference){
        Map<String, String> farmMap = new HashMap<>();
        for(FarmFieldModal farm : farmFieldModalArrayList){
            farmMap.put(farm.getFarmName(), farm.getFarmID());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                new ArrayList<>(farmMap.keySet())
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        farmSpinner= findViewById(R.id.idFarmSpinner);
        farmSpinner.setAdapter(adapter);
        farmSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selectedFarmName = (String) adapterView.getItemAtPosition(i);
                farmId = farmMap.get(selectedFarmName);
                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        FarmFieldModal fieldModal = snapshot.child(farmId).getValue(FarmFieldModal.class);
                        String farmLocation = fieldModal.getFarmLocation();
                        // generate farm boundaries



                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(DiseaseHeatmapActivity.this, " Failed to get data" + error, Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }
}