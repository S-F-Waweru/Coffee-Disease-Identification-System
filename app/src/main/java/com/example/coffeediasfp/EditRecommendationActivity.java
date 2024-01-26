package com.example.coffeediasfp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.os.Bundle;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class EditRecommendationActivity extends AppCompatActivity implements RecommendationRVAdapter.RecommendationClickInterface{
    AppCompatButton upadteBtn, deleteBtn;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReferenceRecommendations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_recommendation);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReferenceRecommendations = firebaseDatabase.getReference("Recommendations");

    }

    @Override
    public void onRecommendationClick(int position) {
        
    }
}