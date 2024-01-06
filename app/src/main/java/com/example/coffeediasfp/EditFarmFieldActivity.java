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

import java.util.HashMap;
import java.util.Map;


public class EditFarmFieldActivity extends AppCompatActivity {
    private AppCompatButton updateFarmBtn, deleteFarmBtn;

    private TextInputEditText farmNameEdit, farmSizeEdit;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private String farmID;
    private FarmFieldModal farmFieldModal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_farm_field);
        firebaseDatabase = FirebaseDatabase.getInstance();


        updateFarmBtn = findViewById(R.id.btneditField);
        deleteFarmBtn = findViewById(R.id.btnDelField);
        farmNameEdit = findViewById(R.id.TIEditFfarmname);
        farmSizeEdit = findViewById(R.id.TIFEditfarmsize);

//  get data from the previous activity
        farmFieldModal = getIntent().getParcelableExtra("farm");
         if(farmFieldModal != null){
             farmNameEdit.setText(farmFieldModal.getFarmName());
             farmSizeEdit.setText(farmFieldModal.getFarmSize());
             farmID = farmFieldModal.getFarmID();
         }


        databaseReference = firebaseDatabase.getReference("Farms").child(farmID);

//         the update button evnt listener

        updateFarmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                add loading thinngy

//                get the data for the edit text

                String farmName = farmNameEdit.getText().toString();
                String farmSize = farmSizeEdit.getText().toString();

//                using a hashmap to pass the data -same as the modal
                Map<String,Object> map = new HashMap<>();
//                put data with key and value pair
                map.put("farmName",farmName);
                map.put("farmSize", farmSize);
                map.put("farmID", farmID);



                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        class loading progessbar

//                        updatign the farm datails
                        databaseReference.updateChildren(map);
                        Toast.makeText(EditFarmFieldActivity.this, "Farm details updated ..", Toast.LENGTH_SHORT).show();

//                       should be  redirected to the recycler view
                        Intent intent =new Intent(getApplicationContext(), FarmFieldsList.class);
                        startActivity(intent);
                        finish();

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(EditFarmFieldActivity.this, "Failed to update Farm details...", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });

        deleteFarmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteFarm();
                Toast.makeText(EditFarmFieldActivity.this, "Delete complete...", Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void deleteFarm(){
        databaseReference.removeValue();
        Toast.makeText(this, farmFieldModal.getFarmName() + "details deleted", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}