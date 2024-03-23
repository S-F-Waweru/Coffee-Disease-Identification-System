package com.example.coffeediasfp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UserDetails extends AppCompatActivity {

    FirebaseAuth mAuth;
    private String userId;
   private String email;
   CheckBox farmerCbx, agrovetOwnerCbx;
   AppCompatButton saveFarmerProfile;
   TextInputEditText fullname, phNumber;
   Boolean isFarmer = true, isAgrovetOwnwer = false;



    @Override
    protected void onStart() {
        super.onStart();

        mAuth =FirebaseAuth.getInstance();
        //Firebase Auth
        FirebaseUser user = mAuth.getCurrentUser();

        String TAG = "fade move";
        Log.d(TAG, "onStart: " +user.toString());
        if(user != null){
            userId = user.getUid();
             email = user.getEmail();
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
        setContentView(R.layout.activity_user_details);
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference =firebaseDatabase.getReference("Users");
        //initialize
        fullname = findViewById(R.id.userfullname);
        phNumber = findViewById(R.id.userphonenumber);
        farmerCbx = findViewById(R.id.isFarmerchecbox);
        agrovetOwnerCbx = findViewById(R.id.isAgroOwnerchecbox);
        saveFarmerProfile = findViewById(R.id.saveFarmerProfile);


        saveFarmerProfile.setOnClickListener(view -> {
            String fullName = fullname.getText().toString();
            String phoneNumber = phNumber.getText().toString();
            if (fullName.isEmpty() || phoneNumber.isEmpty()){
                Toast.makeText(this, "Please fill all credentials", Toast.LENGTH_SHORT).show();
                return;
            }
            farmerCbx.setOnCheckedChangeListener((compoundButton, isChecked) ->{
                if(!isChecked){ isFarmer = true;}
            } );
            agrovetOwnerCbx.setOnCheckedChangeListener((compoundButton, isChecked) ->{
                if (isChecked){isAgrovetOwnwer = true;}
            });

            if(!farmerCbx.isChecked() && !agrovetOwnerCbx.isChecked()){
                Toast.makeText(this, "Both or either Should be checked ", Toast.LENGTH_SHORT).show();
                return;
            }

            User user = new User(userId, email, fullName, phoneNumber, isFarmer, isAgrovetOwnwer, false);

            // add To FireBase
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    databaseReference.child(userId).setValue(user);
                    Toast.makeText(UserDetails.this, "User added", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    finish();
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(UserDetails.this, "Failed to Add details", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                    finish();
                }
            });

        });






    }
}