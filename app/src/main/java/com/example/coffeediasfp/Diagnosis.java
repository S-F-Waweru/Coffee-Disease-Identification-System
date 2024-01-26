package com.example.coffeediasfp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
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
import java.util.UUID;

public class Diagnosis extends AppCompatActivity {

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReferenceFarm;
    DatabaseReference databaseReferenceRecommendations;
    DatabaseReference databaseReferenceDiseases;

     Spinner farmSpinner;
     Button saveDiagnosis;

//     Farm id
    String farmID;
    String diseaseID;

    private  ArrayList<RecommendationModal>recommendationModalArrayList;
    private  ArrayList<DiseaseModal> diseaseModalArrayList;

    String latitude, longitude, diseaseName;
    ProgressBar progressBar;
    float  diseaseConfidence;

    TextView diseaseNameTV, confidenceTV, recommendationTV;

    StringBuilder recommendations;

    DiseaseModal diseaseModal;

    DatabaseReference databaseReferenceDiagnosis;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diagnosis);
        firebaseDatabase = FirebaseDatabase.getInstance();
        farmSpinner = findViewById(R.id.idFarmSpinner);
        databaseReferenceRecommendations = firebaseDatabase.getReference("Recommendations");
        databaseReferenceDiagnosis = firebaseDatabase.getReference("Diagnosis");
        diseaseNameTV = findViewById(R.id.diseaseName);
        confidenceTV =findViewById(R.id.confidencesTV);
        recommendationTV = findViewById(R.id.recommendationText);
        saveDiagnosis = findViewById(R.id.idAddDiagnosis);
        progressBar = findViewById(R.id.idDiagnosisProgressBar);



        recommendationModalArrayList = new ArrayList<>();
        diseaseModalArrayList = new ArrayList<>();

        latitude = getIntent().getStringExtra("latitude");
        longitude = getIntent().getStringExtra("longitude");
        diseaseName = getIntent().getStringExtra("diseaseName");
        diseaseConfidence = getIntent().getFloatExtra("confidence", 100) * 100;



//        loadFarms , generate diagnosis modal;
        loadFarms();

        getDiseaseWithName(diseaseName);

//        getRecomendationforDisease(diseaseID);



        diseaseNameTV.setText(diseaseName);
        confidenceTV.setText(diseaseConfidence + "% ");



        //    ----------------------------------------------Save Diagnisis For the farm -------------------------------------------------------------

        saveDiagnosis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Strings for the data to be out in modal
                String diagnosisID = UUID.randomUUID().toString();
                String farmidText = farmID;
                String diseaseIDTExt = diseaseID;
                String recommendationText = String.valueOf(recommendations);
                String percentageText = diseaseConfidence + "% ";

                DiagnosisModal diagnosisModal = new DiagnosisModal(diagnosisID, farmidText, diseaseIDTExt, recommendationText, longitude, latitude,percentageText);
//                longitude, latitude
//
//                add the Strings to the modal
                databaseReferenceDiagnosis.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                    databaseReferenceDiagnosis.child(diagnosisID).setValue(diagnosisModal);
                    //redirect to the DIagnisis list
                        Toast.makeText(Diagnosis.this, "Record added successfully" + diseaseID, Toast.LENGTH_SHORT).show();


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(Diagnosis.this, "failed ....."  + error, Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });
    }


    private void getRecomendationforDisease(String diseaseID){
        
        if(diseaseID != null) {
            recommendationModalArrayList.clear();
            recommendations = new StringBuilder();
            databaseReferenceRecommendations.orderByChild("diseaseID").equalTo(diseaseID).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        RecommendationModal recommendationModal = dataSnapshot.getValue(RecommendationModal.class);
                        recommendations.append(String.format("%s.  \n", recommendationModal.getRecommendationText()));
                        recommendationTV.setText(recommendations.toString());
                        Log.d("recommendation", String.valueOf(recommendations));
                        progressBar.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(getApplicationContext(), "Failed ... " + error, Toast.LENGTH_SHORT).show();

                }
            });
        }else{
            Toast.makeText(this, "DiseaseId is Null", Toast.LENGTH_SHORT).show();
        }
    }

//    getdiseasename and load it disea sename Tv
    private void  getDiseaseWithName(String diseaseName){

        databaseReferenceDiseases = firebaseDatabase.getReference("Diseases");

            databaseReferenceDiseases.orderByChild("diseaseName").equalTo(diseaseName).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                   diseaseModal = snapshot.getValue(DiseaseModal.class);
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        diseaseModal = dataSnapshot.getValue(DiseaseModal.class);
                        if (diseaseModal != null) {
                            diseaseID = diseaseModal.getDiseaseID();
                            Log.d("DiseaseID", diseaseID);
                            Toast.makeText(Diagnosis.this, "the DiseaseModal is not null" + diseaseID, Toast.LENGTH_SHORT).show();
                            getRecomendationforDisease(diseaseID);
                        } else {
                            Toast.makeText(Diagnosis.this, "the DiseaseModal is null", Toast.LENGTH_SHORT).show();
                        }
                    }
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


