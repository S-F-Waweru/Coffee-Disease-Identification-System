package com.example.coffeediasfp;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.ktx.Firebase;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EditFarmFieldandMapActivity extends AppCompatActivity implements OnMapReadyCallback {
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;

    private static final int LOCATION_GRANTED_REQUEST_CODE = 8116;
    static boolean mLocationPermissionGranted = false;
    GoogleMap mMap;
    AppCompatButton setFarmArea;
    ImageButton backBtn;
    List<LatLng> farmMarkers;
    private FarmFieldModal recievedFarmFiedModal;
    Gson gson;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_farm_fieldand_map);

        setFarmArea = findViewById(R.id.id_set_area);
        backBtn = findViewById(R.id.back_btn);
        farmMarkers = new ArrayList<>();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Farms");

        getLocationPermission();

        //get the farm details
        recievedFarmFiedModal = getIntent().getParcelableExtra("farmdetails");
        Log.d(TAG, "onClick: Fieled Modal" + recievedFarmFiedModal.getFarmName().toString());

        //show map
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        backBtn.setOnClickListener(view -> {
            onBackPressed();
        });
    }

    private void getLocationPermission() {
        String[] permissions = {
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
        };

        if (ContextCompat.checkSelfPermission(getApplicationContext(), FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(getApplicationContext(), COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionGranted = true;
            } else {
                ActivityCompat.requestPermissions(this, permissions, LOCATION_GRANTED_REQUEST_CODE);
            }
        } else {
            ActivityCompat.requestPermissions(this, permissions, LOCATION_GRANTED_REQUEST_CODE);
        }
    }


    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        mMap.setMyLocationEnabled(true);
        mMap.setOnMapClickListener(latLng -> {
            mMap.addMarker(new MarkerOptions().position(latLng));
            farmMarkers.add(latLng);
        });


        setFarmArea.setOnClickListener(view -> {
            if (farmMarkers.size() >= 4) {
//                chage LatLng to Json String

                gson = new Gson();

                String coordinatesJSon = gson.toJson(farmMarkers);

                Map<String,Object> updatedFarmMap = new HashMap<>();
//                put data with key and value pair
                updatedFarmMap.put("farmName",recievedFarmFiedModal.getFarmName());
                updatedFarmMap.put("farmSize", recievedFarmFiedModal.getFarmSize());
                updatedFarmMap.put("farmID", recievedFarmFiedModal.getFarmID());
                updatedFarmMap.put("farmLocation", coordinatesJSon);


//               save the Points and go back to view farm modal
                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        databaseReference.child(recievedFarmFiedModal.getFarmID()).updateChildren(updatedFarmMap);
                        Toast.makeText(EditFarmFieldandMapActivity.this, "Update successful", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(), FarmFieldsList.class));
                        finish();
                    }


                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(EditFarmFieldandMapActivity.this, "Error Updating" + error, Toast.LENGTH_SHORT).show();
                    }
                });

            } else {
                Toast.makeText(this, "Markers should be More or equal to 4", Toast.LENGTH_SHORT).show();
            }

        });
    }

    }