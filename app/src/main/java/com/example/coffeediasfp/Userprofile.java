package com.example.coffeediasfp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Userprofile extends AppCompatActivity {
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    TextInputEditText fullname, phoneNumber;
    private String userId, email;
    AppCompatButton updateProfile, deleteProfile;
    CheckBox farmerCbx, agrovetOwnerCbx;

    ImageButton back_btn;
    User dbUserData;

    boolean isFarmer, isAgrovetOwner;

    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference userDb;
    FirebaseUser user;




    @Override
    protected void onStart() {
        super.onStart();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        //Firebase Auth
        if(user != null){
            userId = user.getUid();
            email = user.getEmail();
            userDb=firebaseDatabase.getReference("Users").child(userId);
            //get data from the database and set data in the UI
            getUserData();
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
        setContentView(R.layout.activity_userprofile);

        // define widgets
        fullname = findViewById(R.id.userfullname);
        phoneNumber = findViewById(R.id.userphonenumber);

        updateProfile = findViewById(R.id.updateAccount);
        deleteProfile = findViewById(R.id.deleteAccount);
        farmerCbx = findViewById(R.id.isFarmerchecbox);
        agrovetOwnerCbx = findViewById(R.id.isAgroOwnerchecbox);
        back_btn = findViewById(R.id.back_btn);


        //when the user updates, save the updated info
        updateProfile.setOnClickListener(view -> {
            // get the data from the interface
            String fName = Objects.requireNonNull(fullname.getText()).toString();
            String Pnumber = Objects.requireNonNull(phoneNumber.getText()).toString();
            // get the Boolen for Farmer and Agrovet


            isFarmer = farmerCbx.isChecked();

            isAgrovetOwner = agrovetOwnerCbx.isChecked();

//            User updatedUser = new User(userId, email, fName, Pnumber, isFarmer, isAgrovetOwner, false);
            Map<String, Object> update = new HashMap<>();
            update.put("userId",userId);
            update.put("email",email);
            update.put("fullName",fName);
            update.put("phoneNumber",Pnumber);
            update.put("isFarmer",isFarmer);
            update.put("IsAgrovetOwner",isAgrovetOwner);
            update.put("IsAdmin",false);


            // update the profile
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    // update the data
                    databaseReference.child(userId).updateChildren(update);
                    Toast.makeText(Userprofile.this, "Your profile updated successfully ", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    finish();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(Userprofile.this, "update failed..." + error, Toast.LENGTH_SHORT).show();
                }
            });
        });
        deleteProfile.setOnClickListener(view -> deleteProfile());

        // go back to the previous page
        back_btn.setOnClickListener(view -> onBackPressed());

    }

    public  void getUserData(){
        //get user data
        userDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                dbUserData = snapshot.getValue(User.class);
                if(dbUserData != null) {
                    // set the widget
                    fullname.setText(dbUserData.getFullName());
                    phoneNumber.setText(dbUserData.getPhoneNumber());
                   farmerCbx.setChecked(dbUserData.getFarmer());
                    agrovetOwnerCbx.setChecked(dbUserData.getAgrovetOwner());
                    Toast.makeText(Userprofile.this, "Data set!!!", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(Userprofile.this, "User data is null", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "Failed to get the data" + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public  void deleteProfile(){
        displayConfirmationDialog();
    }
    public void displayConfirmationDialog(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getApplicationContext());
        alertDialogBuilder.setTitle("Are you sure you want to delete Profile?");
        alertDialogBuilder.setTitle("Clicking yes will delete your account and all its data, process is irreversible")
                .setCancelable(true)
                .setPositiveButton("Yes", (dialogInterface, i) -> {
                    userDb.removeValue();
                    Toast.makeText(Userprofile.this, "Profile deleted", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
                    finish();
                })
                .setNegativeButton("No", (dialogInterface, i) -> dialogInterface.cancel());

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}