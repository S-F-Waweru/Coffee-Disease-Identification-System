package com.example.coffeediasfp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Diagnosis extends AppCompatActivity {

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReferenceFarm;
    DatabaseReference databaseReferenceRecommendations;
    DatabaseReference databaseReferenceDiseases;

     Spinner farmSpinner;

//     Farm id
    String farmID;
    String diseaseID;

    private  ArrayList<RecommendationModal>recommendationModalArrayList;
    private  ArrayList<DiseaseModal> diseaseModalArrayList;

    String latitude, longitude, diseaseName;
    float  diseaseConfidence;

    TextView diseaseNameTV, confidenceTV, recommendationTV;

    StringBuilder recommendations;

    DiseaseModal diseaseModal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diagnosis);
        firebaseDatabase = FirebaseDatabase.getInstance();
        farmSpinner = findViewById(R.id.idFarmSpinner);
        databaseReferenceRecommendations = firebaseDatabase.getReference("Recommendations");
        diseaseNameTV = findViewById(R.id.diseaseName);
        confidenceTV =findViewById(R.id.confidencesTV);
        recommendationTV = findViewById(R.id.recommendationText);



        recommendationModalArrayList = new ArrayList<>();
        diseaseModalArrayList = new ArrayList<>();

        latitude = getIntent().getStringExtra("latitude");
        longitude = getIntent().getStringExtra("longitude");
        diseaseName = getIntent().getStringExtra("diseaseName");
        diseaseConfidence = getIntent().getFloatExtra("confidence", 100) * 100;


//        loadFarms , generate diagnosis modal;
        loadFarms();
        getDiseaseWithName(diseaseName);
        getRecomendationforDisease(diseaseID);


        diseaseNameTV.setText(diseaseName);
        confidenceTV.setText(diseaseConfidence + "% ");


    }
//    -------------------------------Recycler view------------------------------------------
//get all reccomendations adnid  for all diseases

    private void getRecomendationforDisease(String diseaseID){
        recommendationModalArrayList.clear();

        databaseReferenceRecommendations.orderByChild("diseaseID").equalTo(diseaseID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                        RecommendationModal recommendationModal = dataSnapshot.getValue(RecommendationModal.class);
                       recommendations.append(String.format("%s.  \n", recommendationModal.getRecommendationText()));
                       recommendationTV.setText(recommendations);
                    }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "Failed ... " + error, Toast.LENGTH_SHORT).show();

            }
        });
    }

//    getdiseasename and load it disea sename Tv
    private void  getDiseaseWithName(String diseaseName){

        databaseReferenceDiseases = firebaseDatabase.getReference("Diseases");

            databaseReferenceDiseases.orderByChild("diseaseName").equalTo(diseaseName).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                   diseaseModal = snapshot.getValue(DiseaseModal.class);
                   diseaseID = diseaseModal.getDiseaseID();
//                   pupulat disease TV
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(Diagnosis.this, "Failed to Retrieve" + error, Toast.LENGTH_SHORT).show();
                }

              });

    }

//   ------------------------------ Loading and populating the Farm Spinner

    private void loadFarms() {
        databaseReferenceFarm = firebaseDatabase.getReference("Farms");
//            getting all Farms  ***
        databaseReferenceFarm.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<FarmFieldModal> farmList = new ArrayList<>();
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    FarmFieldModal farm = snapshot1.getValue(FarmFieldModal.class);
                    farmList.add(farm);
                }
//                    ppopulate Spinner

                populateSpinner(farmList);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Diagnosis.this, "Failed to retrieve the Farms..." + error, Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void populateSpinner(List<FarmFieldModal> farmlist){
        Map<String, String> farmMap = new HashMap<>();

        for(FarmFieldModal farmField : farmlist){
            farmMap.put(farmField.getFarmID(), farmField.getFarmName());
        }
//        adapter for the spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                new ArrayList<>(farmMap.values())
        );
         adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
         farmSpinner.setAdapter(adapter);

         farmSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
             @Override
             public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                 farmID = new ArrayList<>(farmMap.keySet()).get(i);
             }

             @Override
             public void onNothingSelected(AdapterView<?> adapterView) {

             }
         });

    }
}


