package com.example.coffeediasfp;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class ChooseLocationMapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;

    String longitude, latitude;


    private GoogleMap mMap;

    ImageButton backBtn;
    AppCompatButton setLocationBtn;
    AppCompatButton removeMarkerBtn;


    private static final float DEFAULT_ZOOM = 15f;
    private static final int LOCATION_GRANTED_REQUEST_CODE = 1234;
    private boolean mLocationPermissionGranted = false;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    Marker marker;
    String  diseaseName;
    float  maxConfidence;
    List<Marker> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_location_map);
        backBtn = findViewById(R.id.back_btn);
        setLocationBtn = findViewById(R.id.id_choosen_Location);
        removeMarkerBtn = findViewById(R.id.id_remove_marker);
        diseaseName = getIntent().getStringExtra("diseaseName");
        Log.d(TAG, "onCreate: Diseasename" + diseaseName);
        maxConfidence = getIntent().getFloatExtra("confidence", 100) * 100;
        Log.d(TAG, "onCreate: confidence" + maxConfidence);
        getLocationPermission();

        //show map
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
//        get device location
        getDeviceLocation();

//        get farm modal

        // add a marker showing your location


//    ----------------------------------------    BUTTONS!!! --------------------------------------------------------
        setLocationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent resultIntent = new Intent();
                resultIntent.putExtra("latitude", latitude);
                resultIntent.putExtra("longitude", longitude);
                setResult(Activity.RESULT_OK, resultIntent);
                finish();
//                if (!longitude.isEmpty() && !latitude.isEmpty() && !diseaseName.isEmpty()){
//                    Intent  intent = new Intent(getApplicationContext(), Diagnosis.class);
////                create a bundle with allthe data and send it
//                    intent.putExtra("longitude",longitude );
//                    intent.putExtra("latitude", latitude);
//                    intent.putExtra("diseaseName",diseaseName );
//                    intent.putExtra("confidence", maxConfidence);
//                    startActivity(intent);
//                }else {
//                    Toast.makeText(ChooseLocationMapActivity.this, "you must select locations", Toast.LENGTH_SHORT).show();
//                }
            }
        });
        backBtn.setOnClickListener(view -> onBackPressed());

    }
//-------------------------------------Location Methods------------------------------------------

    private void getDeviceLocation() {
        Log.d("TAG", "getDeviceLocation: getting devices current Location");
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        getLocationPermission();
        try {
            if (mLocationPermissionGranted) {
                Task<Location> location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(ChooseLocationMapActivity.this, "Found Location!!", Toast.LENGTH_SHORT).show();
                            Location currentLocation = task.getResult();
                            moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), DEFAULT_ZOOM, "My Location");

                        } else {
                            Toast.makeText(ChooseLocationMapActivity.this, "unable to ge current location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        } catch (SecurityException e) {
            Toast.makeText(this, "Security Exception" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }

    private void getLocationPermission() {
        String[] permissions = {
                Manifest.permission.ACCESS_FINE_LOCATION,
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

    private void moveCamera(LatLng latLng, float zoom, String title) {
        Log.d(TAG, "moveCamera: Moving Camera to :lat :" + latLng.latitude + ", lng : " + latLng.longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
    }

    private  void setLatLng(LatLng latLng){
        longitude = latLng.longitude + "";
        latitude = latLng.latitude +"" ;

        Log.d(TAG, "setLatLng: Longitude and latitude set" + latitude +"  " + longitude);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        getDeviceLocation();
        mMap.setMyLocationEnabled(true);
      //  googleMap.addMarker(new MarkerOptions().position(new LatLng(0,0)).title("Marker"));

        mMap.setOnMapClickListener(latLng -> {
            if(list.isEmpty()){
               marker = mMap.addMarker(new MarkerOptions().position(latLng));

                removeMarkerBtn.setVisibility(View.VISIBLE);
                marker.isDraggable();
               setLatLng(latLng);
                list.add(marker);
            }else {
                Toast.makeText(ChooseLocationMapActivity.this, "Only one marker can be placed", Toast.LENGTH_SHORT).show();
            }
        });
        removeMarkerBtn.setOnClickListener(view -> {
            list.clear();
            marker.remove();
            longitude = null;
            latitude = null;
            removeMarkerBtn.setVisibility(View.GONE);
        });
    }


}