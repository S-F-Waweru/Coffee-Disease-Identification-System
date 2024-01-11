package com.example.coffeediasfp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class AddRecommendation extends AppCompatActivity {

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReferenceDisease;
   private DatabaseReference databaseReferenceRecommendation;

   private TextInputEditText recommendationTET;
   private AppCompatButton addRecommendation;


    private Spinner diseaseSPinner;
    private String recommendationID;
    String diseasesID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_recomendation);


        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReferenceRecommendation = firebaseDatabase.getReference("Recommendations");
        recommendationTET = findViewById(R.id.idTVDiseseRecommendation);
        addRecommendation = findViewById(R.id.btnAddRecommendation);
        loadDiseases();

        addRecommendation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String recommendationText = recommendationTET.getText().toString();
                recommendationID = UUID.randomUUID().toString();

                RecommendationModal recommendationModal = new RecommendationModal(recommendationID, diseasesID, recommendationText);
                recommendationModal.setCurrentTimestampAndDate();

                databaseReferenceRecommendation.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        databaseReferenceRecommendation.child(recommendationID).setValue(recommendationModal);
                        Toast.makeText(AddRecommendation.this, "Recommendation Added successfully", Toast.LENGTH_SHORT).show();

                        startActivity(new Intent(getApplicationContext(), Recommendation.class));
                        finish();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(AddRecommendation.this, "Failed ...." + error, Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });

//








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
                Toast.makeText(AddRecommendation.this, "Failed because" + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void populateSpinner(List<DiseaseModal> diseaseList) {
        Map<String, String> diseaseMap = new HashMap<>();

        for (DiseaseModal disease : diseaseList) {
            diseaseMap.put(disease.getDiseaseName(), disease.getDiseaseID());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                new ArrayList<>(diseaseMap.keySet())
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        diseaseSPinner = findViewById(R.id.idSpinnerDisease);

        diseaseSPinner.setAdapter(adapter);

        diseaseSPinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
               String selectedDiseaseName = adapter.getItem(position);
               diseasesID = diseaseMap.get(selectedDiseaseName);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    private String getKeyFromValue(Map<String, String> map, String value) {
        for (Map.Entry<String, String> entry : map.entrySet()) {
            if (entry.getValue().equals(value)) {
                return entry.getKey();
            }
        }
        return null; // Handle this case based on your requirements
    }
}