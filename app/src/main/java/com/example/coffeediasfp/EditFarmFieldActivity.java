package com.example.coffeediasfp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

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


public class EditFarmFieldActivity extends AppCompatActivity {
    private AppCompatButton updateFarmBtn, deleteFarmBtn;

    private TextInputEditText farmNameEdit, farmSizeEdit;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference ,farmRef;
    private String farmID;
    private FarmFieldModal farmFieldModal;

    String userId, email;

    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser user ;

    public void onStart(){
        super.onStart();

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
//             Log.d("FArmSize", "onCreate:" +  farmFieldModal.getFarmSize());
             farmID = farmFieldModal.getFarmID();
         }


         farmRef = databaseReference.child(farmID);


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



                farmRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        farmRef.child(farmID).updateChildren(map);
//                        databaseReference.updateChildren(map);
                        Toast.makeText(EditFarmFieldActivity.this, "Farm details updated ..", Toast.LENGTH_SHORT).show();

//                       should be  redirected to the recycler view
                        Intent intent =new Intent(getApplicationContext(), FarmFieldsList.class);
                        startActivity(intent);
                        finish();

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(EditFarmFieldActivity.this, "Farm details  failed to be updated ..", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });

        deleteFarmBtn.setOnClickListener(view -> {
            deleteFarm();
            Toast.makeText(EditFarmFieldActivity.this, "Delete complete...", Toast.LENGTH_SHORT).show();
        });


    }

    private void deleteFarm(){
        farmRef.removeValue();
        Toast.makeText(this, farmFieldModal.getFarmName() + "details deleted", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}