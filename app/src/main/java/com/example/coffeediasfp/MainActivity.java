package com.example.coffeediasfp;


import android.content.Intent;
import android.os.Bundle;
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
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    Toolbar toolbar;
    FirebaseAuth mAuth;


    @Override
    public void onStart(){
        super.onStart();
//
//        FirebaseUser currentUser = mAuth.getCurrentUser();
//        if(currentUser != null){
////            open the main activity
//            Intent intent  = new Intent(getApplicationContext(), MainActivity.class);
//            startActivity(intent);
//            finish();
//
//        }

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();

        loadHomeFragment();

// get the nav bar view

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavView);

//        attach listener
        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        if (item.getItemId()== R.id.botNavHome){
// add the fragment thingy
                            Toast.makeText(MainActivity.this, "Home nav clicked", Toast.LENGTH_SHORT).show();
                            loadHomeFragment();
                        } else if (item.getItemId()== R.id.botNavLab) {
                            Toast.makeText(MainActivity.this, "Diagnose Tab", Toast.LENGTH_SHORT).show();
                            loadDiagnoseFragment();
                        } else if (item.getItemId() == R.id.botNavDiseases) {
                             Toast.makeText(MainActivity.this, "Diseases Tab", Toast.LENGTH_SHORT).show();
                             loadDiseasesFragment();
                        }
                        return false;
                    }
                 }
        );



// Toolbar for the logout button

    }
    // Load the home fragement when the activity is created
    private void loadHomeFragment(){
        Fragment homeFragment = new HomeFragment();
        FragmentManager fm =getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, homeFragment);
        fragmentTransaction.commit();
    }


//    Diseases fragment
    private void loadDiagnoseFragment(){
        Fragment diagnoseFragment = new DiagnoseFragment();
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction  fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, diagnoseFragment);
        fragmentTransaction.commit();
    }

    private void loadDiseasesFragment(){
        Fragment diseasesFragment = new DiseasesFragment();
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, diseasesFragment);
        fragmentTransaction.commit();
    }

}