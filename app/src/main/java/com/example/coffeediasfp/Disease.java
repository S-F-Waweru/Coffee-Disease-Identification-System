package com.example.coffeediasfp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.UUID;

public class Disease extends AppCompatActivity {

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    TextInputEditText diseaseNameTIET, diseasedDescTIET;
    AppCompatButton addDiseaseBtn;

    String diseaseID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_disease);

//        initialize
        diseaseNameTIET = findViewById(R.id.TIDiseasename);
        diseasedDescTIET = findViewById(R.id.TIDiseaseDesc);

        addDiseaseBtn = findViewById(R.id.btnAddDisease);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Diseases");

//        addbutton Event listener

        addDiseaseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String diseaseNameText = diseaseNameTIET.getText().toString();
                String diseaseDescText = diseasedDescTIET.getText().toString();
                diseaseID = UUID.randomUUID().toString();

                DiseaseModal diseaseModal = new DiseaseModal(diseaseNameText, diseaseDescText, diseaseID);

                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        databaseReference.child(diseaseID).setValue(diseaseModal);
                        Toast.makeText(Disease.this, "Disease details added ...", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(), DiseaseList.class));
                        finish();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(Disease.this, "Error id " + error.toString(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}