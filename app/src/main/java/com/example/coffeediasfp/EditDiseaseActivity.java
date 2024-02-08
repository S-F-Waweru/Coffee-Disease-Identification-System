package com.example.coffeediasfp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.media.tv.TableRequest;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class EditDiseaseActivity extends AppCompatActivity {

    private AppCompatButton updateBtn, deleteBtn;
    private TextInputEditText diseaseName, diseaseDesc;

    private FirebaseDatabase firebaseDatabase;

    private DatabaseReference databaseReference;
    private String diseaseID;

    private DiseaseModal diseaseModal;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_disease);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Diseases");
        diseaseName  = findViewById(R.id.TIDiseasename);
        diseaseDesc = findViewById(R.id.TIDiseaseDesc);

        updateBtn = findViewById(R.id.btnEditDisease);
        deleteBtn = findViewById(R.id.btnDelete);

        diseaseModal = getIntent().getParcelableExtra("disease");

        if(diseaseModal != null){
            diseaseName.setText(diseaseModal.getDiseaseName());
            diseaseDesc.setText(diseaseModal.getDiseaseDescription());
            diseaseID = diseaseModal.getDiseaseID();
        }

        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                loading pregress bar

                String diseaseNameText = diseaseName.getText().toString();
                String diseaseDescText = diseaseDesc.getText().toString();

                Map<String, Object> map = new HashMap<>();
                map.put("diseaseName", diseaseNameText);
                map.put("diseaseDescription", diseaseDescText);
                map.put("diseaseID", diseaseID);

                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        databaseReference.child(diseaseID).updateChildren(map);
                        Toast.makeText(EditDiseaseActivity.this, "Disease details updated ..", Toast.LENGTH_SHORT).show();

                        Intent intent =new Intent(getApplicationContext(), DiseaseList.class);
                        startActivity(intent);
                        finish();

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        deleteBtn.setOnClickListener(view1 -> {
                            deleteDisease();
                        });
                    }
                });




            }
        });

    }

    private void deleteDisease(){
        databaseReference.removeValue();
        Toast.makeText(this, diseaseModal.getDiseaseName() + " details deleted", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, DiseaseList.class);
        startActivity(intent);
        finish();
    }
}