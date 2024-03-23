package com.example.coffeediasfp;


import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements OnUserFetchListener{
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    Toolbar toolbar;
    FirebaseAuth mAuth;


    String userID, Email;

    FirebaseUser user;


    @Override
    public void onStart() {
        super.onStart();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        if(user != null){
            //Extract the UserID, and Email
            String id = user.getUid();
            String userEmail = user.getEmail();
            onUserFetch(id, userEmail);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        toolBar setting
        toolbar = findViewById(R.id.toolbar);


        Log.d(TAG, "onCreate: " + userID);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.tool_profile) {
                    // go to the Profile activity
                    Toast.makeText(MainActivity.this, "PROFILE CLICKED", Toast.LENGTH_SHORT).show();
                    //get user data
                    Intent intent = new Intent(getApplicationContext(), Userprofile.class);
                    startActivity(intent);
                    finish();
                } else if (item.getItemId() == R.id.tool_logout) {
                    mAuth.signOut();
                    Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                    finish();
                    Toast.makeText(MainActivity.this, "Logout Clicked", Toast.LENGTH_SHORT).show();
                }
                return false;
            }
        });

        loadHomeFragment();

// get the nav bar view

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavView);

//        attach listener
        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        if (item.getItemId() == R.id.botNavHome) {
// add the fragment thingy
                            Toast.makeText(MainActivity.this, "Home nav clicked", Toast.LENGTH_SHORT).show();
                            loadHomeFragment();
                        }
                        return false;
                    }
                }
        );


    }

    // Load the home fragement when the activity is created
    private void loadHomeFragment() {
        Fragment homeFragment = new HomeFragment();
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, homeFragment);
        fragmentTransaction.commit();
    }

    @Override
    public void onUserFetch(String userId, String email) {
        //assign the email and id to the global variables
        userID = userId;
        Email = email;

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
                                  if (diseaseNotificationCount == null ){
                                      diseaseNotificationCount.setValue(0);
                                  }
                            }
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
                Toast.makeText(MainActivity.this, "An error occured" + error, Toast.LENGTH_SHORT).show();
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
                Toast.makeText(MainActivity.this, "An error occured" + error, Toast.LENGTH_SHORT).show();
            }
        });

    }

}




