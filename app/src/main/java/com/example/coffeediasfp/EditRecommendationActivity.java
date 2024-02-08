package com.example.coffeediasfp;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class EditRecommendationActivity extends AppCompatActivity {
    AppCompatButton upadateBtn, deleteBtn;
    TextInputEditText updateRecommendationText;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReferenceRecommendations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_recommendation);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReferenceRecommendations = firebaseDatabase.getReference("Recommendations");

        upadateBtn = findViewById(R.id.updateRecommendation);
        deleteBtn = findViewById(R.id.delete);
        updateRecommendationText = findViewById(R.id.idTVDiseseRecommendation);

        // get Reccomendation
        RecommendationModal recommendation = getIntent().getParcelableExtra("recommendation");
        Log.d(TAG, "onCreate: " + recommendation.getRecommendationID());

       if(recommendation != null) {
           Log.d(TAG, "onCreate: " + recommendation.getRecommendationText());
           String recommendatioTxt = recommendation.getRecommendationText().toString();

           updateRecommendationText.setText(recommendatioTxt);

       }

        upadateBtn.setOnClickListener(view -> {
            String updatedRec = updateRecommendationText.getText().toString();

            String recommendationid = recommendation.getRecommendationID();
            String diseaseID = recommendation.getDiseaseID();
            Map <String,Object> map = new HashMap<>();

            map.put("RecommendationID", recommendationid);
            map.put("diseaseID", diseaseID);
            map.put("RecommendationText", updatedRec);

            databaseReferenceRecommendations.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    databaseReferenceRecommendations.child(recommendationid).updateChildren(map);
                    Toast.makeText(getApplicationContext(), "Recomendation details updated ..", Toast.LENGTH_SHORT).show();
//                       should be  redirected to the recycler view
                    Intent intent =new Intent(getApplicationContext(), Recommendation.class);
                    startActivity(intent);
                    finish();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(getApplicationContext(), "Recommendation details  failed to  be updated ..", Toast.LENGTH_SHORT).show();
                }
            });



        });

        deleteBtn.setOnClickListener(view -> deleteRecommendation());
    }


    private void deleteRecommendation(){
        databaseReferenceRecommendations.removeValue();
        Toast.makeText(this, "Recommendation deleted", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, Recommendation.class);
        startActivity(intent);
        finish();
    }


}