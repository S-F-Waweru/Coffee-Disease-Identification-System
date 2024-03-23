package com.example.coffeediasfp;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Reports extends AppCompatActivity implements OnUserFetchListener{

    FirebaseUser user;
    FirebaseAuth mAuth;
    String userID, email;
    int Reportcount = 5;

    @Override
    protected void onStart() {
        super.onStart();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        userID= user.getUid();

        onUserFetch(userID, email);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reports);
    }

    @Override
    public void onUserFetch(String userId, String email) {
        //get the total count for the disease Reports;
        userID = userId;
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference userNotificationCount  = database.getReference("Notifications").child(userID);


        FarmDataListener listener = new FarmDataListener() {
            @Override
            public void onDataLoaded(ArrayList<FarmFieldModal> farmList) {
                // get list here
                for (FarmFieldModal farm : farmList){
                    String farmID = farm.getFarmID();
                    DatabaseReference farmNotifications = userNotificationCount.child(farmID);
                    FetchDiseasesListener diseasesListener = new FetchDiseasesListener() {
                        @Override
                        public void onFetchDiseases(ArrayList<DiseaseModal> diseaseList) {
                            Log.d(TAG, "onFetchDiseases: " + diseaseList.toString());
                            for (DiseaseModal disease :diseaseList) {
                                String diseaseId = disease.getDiseaseID();
                                DatabaseReference diseaseNotificationCount = farmNotifications.child(diseaseId).child("TotalReports");
                                diseaseNotificationCount.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        String count = snapshot.getValue(String.class);
                                        Reportcount = Integer.parseInt(Reportcount + count);
                                        Toast.makeText(Reports.this, count, Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        Toast.makeText(Reports.this, "Error", Toast.LENGTH_SHORT).show();

                                    }
                                });
                            }
                            Toast.makeText(Reports.this, "Total count is " + Reportcount, Toast.LENGTH_SHORT).show();
                        }
                    };
                    getAllDisease(diseasesListener);
                }
            }
        };
        getallFarms(userID, listener);
    }
    public void getallFarms(String userId, FarmDataListener listener){
        ArrayList<FarmFieldModal> farmList = new ArrayList<>();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference farms = database.getReference("AllFarms").child(userId).child("Farms");
        farms.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot snapshot1 : snapshot.getChildren()){
                    FarmFieldModal farm = snapshot1.getValue(FarmFieldModal.class);
                    farmList.add(farm);
                }
                listener.onDataLoaded(farmList);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "An error occured" + error, Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void getAllDisease( FetchDiseasesListener listener){
        ArrayList<DiseaseModal> diseaseList = new ArrayList<>();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference diseases = database.getReference("Diseases");

        diseases.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snapshot1 : snapshot.getChildren()){
                    DiseaseModal disease = snapshot1.getValue(DiseaseModal.class);
                    diseaseList.add(disease);
                }
                listener.onFetchDiseases(diseaseList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "An error occured" + error, Toast.LENGTH_SHORT).show();
            }
        });

    }
}