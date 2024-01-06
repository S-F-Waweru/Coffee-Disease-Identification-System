package com.example.coffeediasfp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
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

public class FarmFieldsList extends AppCompatActivity  implements FarmFieldRVAdapter.FarmFieldClickInterface {
    private RecyclerView fieldsRV;
    private ProgressBar loadingPB;
    private FloatingActionButton FABtn;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private ArrayList<FarmFieldModal>  farmFieldModalArrayList;
    private RelativeLayout bottomSheetRL;
    private FarmFieldRVAdapter farmFieldRVAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_farm_fields_list);

        fieldsRV = findViewById(R.id.farmfieldRV);
        loadingPB = findViewById(R.id.PBLoading);
        FABtn = findViewById(R.id.FABtn);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Farms" );
        farmFieldModalArrayList = new ArrayList<>();
//        bottomSheetRL = (RelativeLayout) findViewById(R.id.idRLBottomSheet);
        farmFieldRVAdapter = new FarmFieldRVAdapter(farmFieldModalArrayList, this, this);

//        Recycler View
        fieldsRV.setLayoutManager(new LinearLayoutManager(this));
        fieldsRV.setAdapter(farmFieldRVAdapter);

        getAllFields();

        FABtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent  = new Intent(FarmFieldsList.this, FarmForm.class);
                startActivity(intent);
            }
        });



    }

//    a method for getting all Fields

    private void getAllFields(){

        farmFieldModalArrayList.clear();
//        read all data from the database
        databaseReference.addChildEventListener(new ChildEventListener() {

//            this methis will be called when new child has been added
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                loadingPB.setVisibility(View.GONE);
                farmFieldModalArrayList.add(snapshot.getValue(FarmFieldModal.class));
//                notify adapter that data has changed
                farmFieldRVAdapter.notifyDataSetChanged();

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    loadingPB.setVisibility(View.GONE);
                    farmFieldRVAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                    loadingPB.setVisibility(View.GONE);
                    farmFieldRVAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                loadingPB.setVisibility(View.GONE);
                farmFieldRVAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
//    when clicked shoudl show a bottom sheet
    @Override
    public void onFarmFieldClick(int position) {
        displayBottomSheet(farmFieldModalArrayList.get(position));
    }

    private void displayBottomSheet(FarmFieldModal farmFieldModal){
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View layout = LayoutInflater.from(this).inflate(R.layout.farm_field_bottom_sheet_dialog, bottomSheetRL);
        bottomSheetDialog.setContentView(layout);
        bottomSheetDialog.setCancelable(false);
        bottomSheetDialog.setCanceledOnTouchOutside(true);
        bottomSheetDialog.show();


        TextView farmNameTV = layout.findViewById(R.id.idTVFarmName);
        TextView farmSizeTV = layout.findViewById(R.id.idTVFarmSize);
        Button editBtm = layout.findViewById(R.id.idBtnEdit);
//        Button viewDetails = layout.findViewById(R.id.idBtnView);


        farmNameTV.setText(farmFieldModal.getFarmName());
        farmSizeTV.setText(farmFieldModal.getFarmSize());


        editBtm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FarmFieldsList.this, EditFarmFieldActivity.class);
//                pass the data the name shoudl be the same in the EditFarmFieldActiviy in the get intent method
                intent.putExtra("farm", farmFieldModal);
                startActivity(intent);
            }
        });

    }

}