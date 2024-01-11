package com.example.coffeediasfp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class DiseaseList extends AppCompatActivity implements DiseasesRVAdapter.DiseaseClickInterface{


    private RecyclerView diseaseRV;
    private ProgressBar loadingPB;

    private FloatingActionButton addDiseaseFAB;

    private FirebaseDatabase firebaseDatabase;
    private RelativeLayout bottomsheetRL;
    private DatabaseReference databaseReference;

    private ArrayList<DiseaseModal> diseaseModalArrayList;

    private DiseasesRVAdapter diseasesRVAdapter;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_disease_list);


        diseaseRV = findViewById(R.id.diseaseRV);
        loadingPB = findViewById(R.id.PBDiseaseLoading);
        addDiseaseFAB = findViewById(R.id.addDiseaseFAB);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Diseases");

        diseaseModalArrayList = new ArrayList<>();
        diseasesRVAdapter = new DiseasesRVAdapter(diseaseModalArrayList, this, this);

        diseaseRV.setLayoutManager(new LinearLayoutManager(this));
        diseaseRV.setAdapter(diseasesRVAdapter);

//        getting all diseases

        getalldiseases();

        addDiseaseFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DiseaseList.this, Disease.class);
                startActivity(intent);
            }
        });


    }







private void getalldiseases(){
        diseaseModalArrayList.clear();

        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                loadingPB.setVisibility(View.GONE);
                diseaseModalArrayList.add(snapshot.getValue(DiseaseModal.class));
                diseasesRVAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                loadingPB.setVisibility(View.GONE);
                diseasesRVAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot)  {
                loadingPB.setVisibility(View.GONE);
                diseasesRVAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                loadingPB.setVisibility(View.GONE);
                diseasesRVAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


}

private void displayBottomSheet(DiseaseModal diseaseModal){

        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View layout = LayoutInflater.from(this).inflate(R.layout.disease_bottom_sheet_dialog, bottomsheetRL);

    bottomSheetDialog.setContentView(layout);
    bottomSheetDialog.setCancelable(false);
    bottomSheetDialog.setCanceledOnTouchOutside(true);
    bottomSheetDialog.show();

    TextView diseaseNameTV = layout.findViewById(R.id.idTVFDiseaseName);
    TextView diseaseDescTV = layout.findViewById(R.id.idTVDiseaseDescription);
    Button diseaseBtn = layout.findViewById(R.id.idBtnEdit);

     diseaseNameTV.setText(diseaseModal.getDiseaseName());
     diseaseDescTV.setText(diseaseModal.getDiseaseDescription());

     diseaseBtn.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
             Intent intent = new Intent(DiseaseList.this, EditDiseaseActivity.class);
             intent.putExtra("disease", diseaseModal);
             startActivity(intent);
         }
     });

}



    @Override
    public void onDiseaseClick(int position) {
//        display somethigng
        displayBottomSheet(diseaseModalArrayList.get(position));

    }
}