package com.example.coffeediasfp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.UUID;

public class FarmForm extends AppCompatActivity {
    AppCompatButton addField;

    private TextInputEditText farmName, farmSize;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private String farmID;
    ImageButton mMAp;

    String userId, email;

    FirebaseAuth mAuth;
    FirebaseUser user ;
    public void onStart(){
        super.onStart();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        if(user != null){
            userId = user.getUid();
            email = user.getEmail();
            //        database stuff
            firebaseDatabase = FirebaseDatabase.getInstance();
            databaseReference = firebaseDatabase.getReference("AllFarms").child(userId).child("Farms");
//            databaseReference.setValue(true);
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
        setContentView(R.layout.activity_farm_form);

        farmName = findViewById(R.id.TIFfarmname);
        farmSize = findViewById(R.id.TIFfarmsize);
        firebaseDatabase = FirebaseDatabase.getInstance();
         addField = findViewById(R.id.btnAddField);
         mMAp = findViewById(R.id.ic_gps);
         mMAp.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 String farmname = farmName.getText().toString();
                 String farmsize = farmSize.getText().toString();
                 farmID = UUID.randomUUID().toString();
                 FarmFieldModal farmFieldModal = new FarmFieldModal(farmname, farmsize, farmID);

                 Log.d("Sent Fieled", "onClick: Fieled Modal" + farmFieldModal.getFarmName().toString());
                 Intent intent = new Intent(getApplicationContext(), SetFarmAreaMapActivity.class);
                 intent.putExtra( "farmModal",farmFieldModal );
                 startActivity(intent);

             }
         });

         addField.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 String farmname = farmName.getText().toString();
                 String farmsize = farmSize.getText().toString();
                 farmID = UUID.randomUUID().toString();

//                 add the fields to the modal class;

                 FarmFieldModal farmFieldModal = new FarmFieldModal(farmname, farmsize, farmID);

                 databaseReference.addValueEventListener(new ValueEventListener() {
                     @Override
                     public void onDataChange(@NonNull DataSnapshot snapshot) {
                         databaseReference.child(farmID).setValue(farmFieldModal);
                         startActivity(new Intent(FarmForm.this, FarmFieldsList.class));
                         Toast.makeText(FarmForm.this, "Farm Added successfully", Toast.LENGTH_SHORT).show();
                         finish();
                     }

                     @Override
                     public void onCancelled(@NonNull DatabaseError error) {
//                         display error gotten when adding
                         Toast.makeText(FarmForm.this, "Error is " + error.toString(), Toast.LENGTH_SHORT).show();

                     }
                 });

             }
         });


    }
}