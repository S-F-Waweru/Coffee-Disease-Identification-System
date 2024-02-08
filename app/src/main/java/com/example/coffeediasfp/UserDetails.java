package com.example.coffeediasfp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class UserDetails extends AppCompatActivity {

    FirebaseAuth mAuth;
    private String userId;
   private String email;
   CheckBox farmerCbx, agrovetOwnerCbx;
   AppCompatButton saveFarmerProfile;
   TextInputEditText fullname, phNumber;


    @Override
    protected void onStart() {
        super.onStart();
        //Firebase Auth
        FirebaseUser user = mAuth.getCurrentUser();
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
        //initialize
        fullname = findViewById(R.id.userfullname);
        phNumber = findViewById(R.id.userphonenumber);
        farmerCbx = findViewById(R.id.isFarmerchecbox);
        agrovetOwnerCbx = findViewById(R.id.isAgroOwnerchecbox);
        saveFarmerProfile = findViewById(R.id.saveFarmerProfile);

        String fullName = fullname.getText().toString();
        String phoneNumber = phNumber.getText().toString();

        if (fullName.isEmpty() || phoneNumber.isEmpty()){
            Toast.makeText(this, "All credentoals are needed", Toast.LENGTH_SHORT).show();
            return;
        }

        User user = new User(userId, email, fullName, phoneNumber);
        farmerCbx.setOnCheckedChangeListener((compoundButton, isChecked) ->{
            if(!isChecked){ user.setFarmer(false);}
          } );
        agrovetOwnerCbx.setOnCheckedChangeListener((compoundButton, isChecked) ->{
            if (isChecked){user.setAgrovetOwner(true);}
        });

        saveFarmerProfile.setOnClickListener(view -> {
            //initialize fire store and add a user in the fire store

        });







    }
}