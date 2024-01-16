package com.example.coffeediasfp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diagnosis_list);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReferenceDiagnosis = firebaseDatabase.getReference("Diagnosis");
        farmSpinner = findViewById(R.id.idFarmSpinner);
        loadingProgress = findViewById(R.id.loadingProgress);
        diagnosisModalArrayList  = new ArrayList<>();
        diagnosisRV = findViewById(R.id.diagnosisRV);

        diagnosisRVAdapter = new DiagnosisRVAdapter(diagnosisModalArrayList, this);
        diagnosisRV.setLayoutManager(new LinearLayoutManager(this));





//       loads the farm to spin
        loadFarms();
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
        databaseReferenceFarm = firebaseDatabase.getReference("Farms");
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