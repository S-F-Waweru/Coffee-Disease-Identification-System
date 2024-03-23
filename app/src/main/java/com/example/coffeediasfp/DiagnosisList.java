package com.example.coffeediasfp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DiagnosisList extends AppCompatActivity {

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReferenceDiagnosis;
    DatabaseReference databaseReferenceFarm;
     String farmID;

    Spinner farmSpinner;
    RecyclerView diagnosisRV;
    ProgressBar loadingProgress;
    ArrayList<DiagnosisModal> diagnosisModalArrayList;
    DiagnosisRVAdapter diagnosisRVAdapter;
    ImageButton diseaseMap;
    FloatingActionButton addDiagnosis;


    String userId, email;

    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser user ;

    //on start method
    public void onStart(){
        super.onStart();

        user = mAuth.getCurrentUser();
        if(user != null){
            userId = user.getUid();
            email = user.getEmail();
            //        database stuff
            firebaseDatabase = FirebaseDatabase.getInstance();
            databaseReferenceFarm = firebaseDatabase.getReference("AllFarms").child(userId).child("Farms");
            databaseReferenceFarm.setValue(true);
        }else {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
            finish();
            Toast.makeText(this, "You are not logged in", Toast.LENGTH_SHORT).show();
        }
    }




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diagnosis_list);

        firebaseDatabase = FirebaseDatabase.getInstance();

        databaseReferenceDiagnosis = firebaseDatabase.getReference("Diagnosis");
//        databaseReferenceFarm = firebaseDatabase.getReference("Farms");

        farmSpinner = findViewById(R.id.idFarmSpinner);
        loadingProgress = findViewById(R.id.loadingProgress);
        diseaseMap = findViewById(R.id.showDiseaseMap);
        diagnosisModalArrayList  = new ArrayList<>();
        diagnosisRV = findViewById(R.id.diagnosisRV);
        addDiagnosis = findViewById(R.id.addDiseaseFAB);

        diagnosisRVAdapter = new DiagnosisRVAdapter(diagnosisModalArrayList, this);
        diagnosisRV.setLayoutManager(new LinearLayoutManager(this));

        addDiagnosis.setOnClickListener(view -> {
            Intent i = new Intent(getApplicationContext(),IdentifyDiseaseActivity.class);
            startActivity(i);
            finish();
        });

        diseaseMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), DiseaseHeatmapActivity.class);
                startActivity(intent);
                finish();
            }
        });





//       loads the farm to spin
//        loadFarms();
//        diagnosisRV.setAdapter(diagnosisRVAdapter);

        //loadd recyclerViwe


    }



    private void getallDiagnosisForFarm(String farmID){
        if (diagnosisModalArrayList == null) {
            diagnosisModalArrayList = new ArrayList<>(); // Initialize the ArrayList if null
        } else {
            diagnosisModalArrayList.clear(); // Clear the ArrayList if it's not null
        }

        if(farmID != null){
            databaseReferenceDiagnosis.orderByChild("farmID").equalTo(farmID).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                        DiagnosisModal diagnosisModal = dataSnapshot.getValue(DiagnosisModal.class);
                        diagnosisModalArrayList.add(diagnosisModal);
                    }
//                    progressBar
                    loadingProgress.setVisibility(View.GONE);

//                    Adapter
                    diagnosisRV.setAdapter(diagnosisRVAdapter);

                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(DiagnosisList.this, "Failed .... " + error, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    // get all farms in the database
    private void loadFarms(){

        databaseReferenceFarm.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<FarmFieldModal>farmFieldList = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    FarmFieldModal farmFieldModal = dataSnapshot.getValue(FarmFieldModal.class);
                    farmFieldList.add(farmFieldModal);
                }
                populateFarmSpinner(farmFieldList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(DiagnosisList.this, "Faliled ....." + error, Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void populateFarmSpinner(List<FarmFieldModal> farmFieldList){
        Map<String, String> farmMap = new HashMap<>();
        for (FarmFieldModal farm : farmFieldList){
            farmMap.put(farm.getFarmID(), farm.getFarmName());
        }
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
                if (farmID != null) {
                    getallDiagnosisForFarm(farmID);

                }
                else {
                    Toast.makeText(DiagnosisList.this, "Farm ID is null", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
//                    shoe all diagnosis
            }
        });

    }
}