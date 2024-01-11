package com.example.coffeediasfp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Recommendation extends AppCompatActivity {

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReferenceDisease;
    FloatingActionButton addRecommendation;
    String diseasesID;

    ProgressBar progressBar;
    RecyclerView recommendationsRV;

    DatabaseReference databaseReferenceRecommedations;

    private Spinner diseaseSPinner;
    private ArrayList<RecommendationModal>recommendationModalArrayList;
    private RecommendationRVAdapter recommendationRVAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommendation);
        firebaseDatabase = FirebaseDatabase.getInstance();
        addRecommendation = findViewById(R.id.addReccomendationFAB);
        recommendationsRV = findViewById(R.id.recommendationsRV);
        recommendationModalArrayList = new ArrayList<>();
        progressBar.findViewById(R.id.PBDiseaseLoading);


//load Disease
        loadDiseases();
//        load reccimendations based on Disease named
        getAllReccomendations();

        addRecommendation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), AddRecommendation.class);
                startActivity(i);
            }
        });

    }


    private void loadDiseases() {
        databaseReferenceDisease = firebaseDatabase.getReference("Diseases");
        databaseReferenceDisease.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                List<DiseaseModal> diseaseList = new ArrayList<>();
                for (DataSnapshot snapshot : datasnapshot.getChildren()) {
                    DiseaseModal disease = snapshot.getValue(DiseaseModal.class);
                    diseaseList.add(disease);
                }
//                method to populate spinner
                populateSpinner(diseaseList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Recommendation.this, "Failed because" + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void populateSpinner(List<DiseaseModal> diseaseList) {
        Map<String, String> diseaseMap = new HashMap<>();

        for (DiseaseModal disease : diseaseList) {
            diseaseMap.put(disease.getDiseaseID(), disease.getDiseaseName());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                new ArrayList<>(diseaseMap.values())
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        diseaseSPinner = findViewById(R.id.idSpinnerDisease);

        diseaseSPinner.setAdapter(adapter);

        diseaseSPinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                String diseaseDescription = new ArrayList<>(diseaseMap.keySet()).get(position);
                diseasesID = getKeyFromValue(diseaseMap, diseaseDescription);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    private void getAllReccomendations(){
        recommendationModalArrayList.clear();

        databaseReferenceRecommedations.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                progressBar.setVisibility(View.GONE);
                recommendationModalArrayList.add(snapshot.getValue(RecommendationModal.class));
                recommendationRVAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                progressBar.setVisibility(View.GONE);
                recommendationRVAdapter.notifyDataSetChanged();

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                progressBar.setVisibility(View.GONE);
                recommendationRVAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                progressBar.setVisibility(View.GONE);
                recommendationRVAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Recommendation.this, "Failed..." + error, Toast.LENGTH_SHORT).show();
            }
        });
    }





    // Helper method to get the key (Disease ID) from a value (Disease name) in a Map
    private String getKeyFromValue(Map<String, String> map, String value) {
        for (Map.Entry<String, String> entry : map.entrySet()) {
            if (entry.getValue().equals(value)) {
                return entry.getKey();
            }
        }
        return null; // Handle this case based on your requirements
    }



}