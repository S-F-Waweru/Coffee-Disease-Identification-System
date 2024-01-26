package com.example.coffeediasfp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
//import com.google.gson.Gson;

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
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.gson.Gson;

import org.checkerframework.common.reflection.qual.NewInstance;

import java.util.ArrayList;
import java.util.List;

public class SetFarmAreaMapActivity extends AppCompatActivity implements OnMapReadyCallback {


    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;

    private static final int LOCATION_GRANTED_REQUEST_CODE = 54321;
    static boolean mLocationPermissionGranted = false;
    GoogleMap mMap;
    AppCompatButton setFarmArea;
    ImageButton backBtn;
    List<LatLng> farmMarkers;
    private FarmFieldModal recievedFarmFiedModal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_farm_area_map);
        setFarmArea = findViewById(R.id.id_set_area);
        backBtn = findViewById(R.id.back_btn);
        farmMarkers = new ArrayList<>();

        getLocationPermission();

        //get the farm details
        recievedFarmFiedModal = getIntent().getParcelableExtra("farmModal");
        Log.d("recievedFarmFiedModal", "onClick: Fieled Modal" + recievedFarmFiedModal.getFarmName().toString());

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
                Manifest.permission.ACCESS_COARSE_LOCATION
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
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
                Intent intent = new Intent(getApplicationContext(), ShowFarmsMapArea.class);
                intent.putParcelableArrayListExtra("coordinates", (ArrayList<LatLng>) farmMarkers);
                intent.putExtra("farmModal", recievedFarmFiedModal);
                startActivity(intent);
            } else {
                Toast.makeText(this, "MArkers should be More or equal to 4", Toast.LENGTH_SHORT).show();
            }

        });


    }


}