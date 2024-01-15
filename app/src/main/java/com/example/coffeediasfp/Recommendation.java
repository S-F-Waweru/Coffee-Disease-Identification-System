package com.example.coffeediasfp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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

public class Recommendation extends AppCompatActivity implements RecommendationRVAdapter.RecommendationClickInterface {

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReferenceDisease;
    FloatingActionButton addRecommendation;
    String diseasesID;

    ProgressBar progressBar;
    RecyclerView recommendationsRV;

    DatabaseReference databaseReferenceRecommedations;

    private String recommendationID;

    private Spinner diseaseSPinner;
    private ArrayList<RecommendationModal>recommendationModalArrayList;
    private RecommendationRVAdapter recommendationRVAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommendation);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReferenceRecommedations = firebaseDatabase.getReference("Recommendations");
        addRecommendation = findViewById(R.id.addReccomendationFAB);
        recommendationsRV = findViewById(R.id.recommendationsRV);
        recommendationModalArrayList = new ArrayList<>();
        recommendationRVAdapter = new RecommendationRVAdapter(recommendationModalArrayList, this, this);
        progressBar = findViewById(R.id.PBrecommendationLoading);

        recommendationsRV.setLayoutManager(new LinearLayoutManager(this));
        recommendationsRV.setAdapter(recommendationRVAdapter);

//        Log.e("Finding variable !", diseasesID);


//load Disease
        loadDiseases();
//        load reccimendations based on Disease named
//        getAllReccomendations();

//        getAllrecommendationForDisease(diseasesID);


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
//                Log.d("DiseaseMap", diseaseMap.toString());

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
                diseasesID = new ArrayList<>(diseaseMap.keySet()).get(position);

                if(diseasesID != null){
                    getAllrecommendationForDisease(diseasesID);
                }else {
                    Toast.makeText(Recommendation.this, "Failed.. to retrieve...null", Toast.LENGTH_SHORT).show();
                }
               
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }




    private void getAllrecommendationForDisease(String diseasesID){
        recommendationModalArrayList.clear();
        databaseReferenceRecommedations.orderByChild("diseaseID").equalTo(diseasesID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    RecommendationModal recommendationModal = dataSnapshot.getValue(RecommendationModal.class);
                    recommendationModalArrayList.add(recommendationModal);
                }
//                notify the adapter that the data has changed
                progressBar.setVisibility(View.GONE);
                recommendationRVAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Recommendation.this, "Failed ... " + error, Toast.LENGTH_SHORT).show();
            }
        });

    }


    @Override
    public void onRecommendationClick(int position) {
        Toast.makeText(this, "cliked", Toast.LENGTH_SHORT).show();
    }
}