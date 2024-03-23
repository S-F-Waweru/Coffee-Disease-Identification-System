package com.example.coffeediasfp;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class DiagnosisRVAdapter extends RecyclerView.Adapter<DiagnosisRVAdapter.ViewHolder> {

    private ArrayList<DiagnosisModal>diagnosisModalArrayList;
    private Context context;
    String farmName;
    String diseaseName;

    int lastPos = -1;

    public DiagnosisRVAdapter(ArrayList<DiagnosisModal> diagnosisModalArrayList, Context context) {
        this.diagnosisModalArrayList = diagnosisModalArrayList;
        this.context = context;

//        prefetch initial data for items
//        prefetchInitialData();
    }

    private void prefetchInitialData() {
        for(int i = 0 ;i < diagnosisModalArrayList.size(); i++ ){
            DiagnosisModal diseaseModal = diagnosisModalArrayList.get(i);
            fetchDiagnosisData(diseaseModal.getDiseaseID() , diseaseModal.getFarmID() , null);
        }
    }



    @NonNull
    @Override
    public DiagnosisRVAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.diagnosis_item_rv_item, parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DiagnosisRVAdapter.ViewHolder holder, int position) {

        position = holder.getAdapterPosition();
        DiagnosisModal diagnosisModal = diagnosisModalArrayList.get(position);


            fetchDiagnosisData(diagnosisModal.getDiseaseID(), diagnosisModal.getFarmID(), new DataLoadCallBack() {
                @Override
                public void onDataLoaded(String farmName, String diseaseName) {
                        // Fetch data if not cached
                        fetchDiagnosisData(diagnosisModal.getDiseaseID(), diagnosisModal.getFarmID(), new DataLoadCallBack() {
                            @Override
                            public void onDataLoaded(String farmName, String diseaseName) {
                                // Update holder with fetched data
                                setHolderData(holder, diagnosisModal, farmName, diseaseName);
                            }
                        });
                    }

            });

    }

    @Override
    public int getItemCount() {
        return diagnosisModalArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView famrNameTV, diseaseNameTv, percentage;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            famrNameTV = itemView.findViewById(R.id.idFarmNameRV);
            diseaseNameTv = itemView.findViewById(R.id.idDiseaseNameRV);
            percentage = itemView.findViewById(R.id.idPercentageRV);
        }
    }




    /////////////////////==========================================Combined method

    private void fetchDiagnosisData(String diseaseID, String farmID, DataLoadCallBack callback) {
        if (diseaseID == null || farmID == null) {
            Toast.makeText(context, "Missing ID(s)", Toast.LENGTH_SHORT).show();
            return;
        }
        //get the current user

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReferenceDisease = firebaseDatabase.getReference("Diseases");
//      DatabaseReference databaseReferenceFarm = firebaseDatabase.getReference("Farms");
        DatabaseReference databaseReferenceFarm = firebaseDatabase.getReference("AllFarms").child(user.getUid()).child("Farms");
        databaseReferenceDisease.child(diseaseID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot diseaseSnapshot) {
                DiseaseModal diseaseModal = diseaseSnapshot.getValue(DiseaseModal.class);
                final String diseaseName = (diseaseModal != null) ? diseaseModal.getDiseaseName() : null;

                databaseReferenceFarm.child(farmID).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot farmSnapshot) {
                        FarmFieldModal farmFieldModal = farmSnapshot.getValue(FarmFieldModal.class);
                        String farmName = (farmFieldModal != null) ? farmFieldModal.getFarmName() : null;

                        callback.onDataLoaded(farmName, diseaseName);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        handleError(callback, error);
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                handleError(callback, error);
            }
        });
    }

    private void handleError(DataLoadCallBack callback, DatabaseError error) {
        Toast.makeText(context, "Failed to load data: " + error, Toast.LENGTH_SHORT).show();
        // Optionally provide more specific error handling for different scenarios
        callback.onDataLoaded(null, null); // Indicate error to callback
    }


    // animation
    private void setAnimation(View itemView, int position){
        if(position > lastPos){
            Animation animation = AnimationUtils.loadAnimation(context, android.R.anim.slide_in_left);
            itemView.setAnimation(animation);
            lastPos = position;
        }
    }


    private void setHolderData(ViewHolder holder, DiagnosisModal diagnosisModal, String farmName, String diseaseName) {
        // Set holder data using fetched values
        holder.famrNameTV.setText(farmName);
        holder.diseaseNameTv.setText(diseaseName);
        holder.percentage.setText("Confidence: " + diagnosisModal.getPercentage());
        setAnimation(holder.itemView, holder.getAdapterPosition());
    }






    public interface DataLoadCallBack{
        void onDataLoaded(String farmName, String DiseaseName);
    }
}


