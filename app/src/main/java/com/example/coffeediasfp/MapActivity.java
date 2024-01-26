//package com.example.coffeediasfp;
//
//import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;
//
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.core.app.ActivityCompat;
//import androidx.core.content.ContextCompat;
//
//import android.Manifest;
//
//import android.content.Context;
//import android.content.pm.PackageManager;
//import android.location.Address;
//import android.location.Geocoder;
//import android.location.Location;
//import android.os.Bundle;
//import android.util.Log;
//import android.view.KeyEvent;
//import android.view.View;
//import android.view.inputmethod.EditorInfo;
//import android.widget.EditText;
//import android.widget.ImageView;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.google.android.gms.location.FusedLocationProviderClient;
//import com.google.android.gms.location.LocationServices;
//import com.google.android.gms.maps.CameraUpdateFactory;
//import com.google.android.gms.maps.GoogleMap;
//import com.google.android.gms.maps.OnMapReadyCallback;
//import com.google.android.gms.maps.SupportMapFragment;
//import com.google.android.gms.maps.model.LatLng;
//import com.google.android.gms.maps.model.MarkerOptions;
//import com.google.android.gms.tasks.OnCompleteListener;
//import com.google.android.gms.tasks.Task;
//
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.List;
//
//public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {
//
//    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
//    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
//    private static final float DEFAULT_ZOOM = 15f;
//
//    private static final int LOCATION_GRANTED_REQUEST_CODE = 1234;
//
//
////widgets
//    EditText mSearchText;
//    ImageView mGPS;
//
//
////vars
//    private boolean mLocationPermissionGranted = false;
//    private GoogleMap mMap;
//    private FusedLocationProviderClient mFusedLocationProviderClient;
//
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_map);
//        mSearchText  = findViewById(R.id.input_search);
//        mGPS = findViewById(R.id.ic_gps);
//        getLocationPermission();
//        getDeviceLocation();
//    }
//    private void  init(){
////        set when the enter button is clicked
//        mSearchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//            @Override
//            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
//                if(actionId == EditorInfo.IME_ACTION_SEARCH
//                        || actionId == EditorInfo.IME_ACTION_DONE
//                        || keyEvent.getAction() == keyEvent.ACTION_DOWN
//                        || keyEvent.getAction() == keyEvent.KEYCODE_ENTER){
////                execute our method for searching
//                    geoLocate();
//                }
//                return false;
//            }
//        });
//
//        mGPS.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                getDeviceLocation();
//            }
//        });
//    }
//    private  void geoLocate(){
//        String searchString = mSearchText.getText().toString();
//        Geocoder geocoder = new Geocoder(MapActivity.this);
//        List<Address> list = new ArrayList<>();
//        try {
////            have only one address in the list
//            list = geocoder.getFromLocationName(searchString, 1);
//
//        }catch (IOException e){
//            Log.d(TAG, "geoLocate: IOException" + e.getMessage());
//        }
//        if (list.size() > 0 ){
//            Address address = list.get(0);
////move the camera to the default zoom
//            moveCamera(new LatLng(address.getLatitude(), address.getLongitude()), DEFAULT_ZOOM, address.getAddressLine(0));
//        }
//    }
//
//    private void getDeviceLocation() {
//        Log.d("TAG", "getDeviceLocation: getting devices current Location");
//        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
//
//
//        try {
//            if (mLocationPermissionGranted) {
//                Task<Location> location = mFusedLocationProviderClient.getLastLocation();
//                location.addOnCompleteListener(new OnCompleteListener<Location>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Location> task) {
//                        if (task.isSuccessful()) {
//                            Toast.makeText(MapActivity.this, "Found Location!!", Toast.LENGTH_SHORT).show();
//                            Location currentLocation = (Location) task.getResult();
//                            moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), DEFAULT_ZOOM, "My Location");
//
//                        } else {
//                            Toast.makeText(MapActivity.this, "unable to ge current location", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });
//
//            }
//        } catch (SecurityException e) {
//            Toast.makeText(this, "Security Exception" + e.getMessage(), Toast.LENGTH_SHORT).show();
//        }
//
//    }
//
//    private void moveCamera(LatLng latLng, float zoom, String title) {
//        Log.d(TAG, "moveCamera: Moving Camera to :lat :" + latLng.latitude + ", lng : " + latLng.longitude);
//        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
//
//        if(!title.equals("My Location")){
//            // drop a Marker
//            MarkerOptions options = new MarkerOptions().position(latLng).title(title);
//            mMap.addMarker(options);
//        }
//    }
//
//
//    private void initMap() {
//        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.idmap);
//        mapFragment.getMapAsync(MapActivity.this);
//    }
//
//    private void getLocationPermission() {
//        String[] permissions = {
//                Manifest.permission.ACCESS_FINE_LOCATION,
//                Manifest.permission.ACCESS_COARSE_LOCATION
//        };
//
//        if (ContextCompat.checkSelfPermission(getApplicationContext(), FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
//            if (ContextCompat.checkSelfPermission(getApplicationContext(), COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
//                mLocationPermissionGranted = true;
//            } else {
//                ActivityCompat.requestPermissions(this, permissions, LOCATION_GRANTED_REQUEST_CODE);
//            }
//        } else {
//            ActivityCompat.requestPermissions(this, permissions, LOCATION_GRANTED_REQUEST_CODE);
//        }
//    }
//
//
////ActivityCompat.OnRequestPermissionsResultCallback Callback for the result from requesting permissions.
//// This method is invoked for every call on requestPermissions(Activity, String[], int).
////Note: It is possible that the permissions request interaction with the user is interrupted. In this case you will receive empty permissions and results arrays which should be treated as a cancellation.
//
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        mLocationPermissionGranted = false;
//        switch (requestCode) {
//            case LOCATION_GRANTED_REQUEST_CODE:
//                for (int i = 0; i < grantResults.length; i++) {
//                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                        return;
//                    }
//                    mLocationPermissionGranted = true;
//                    //Initialize  the map
//                    initMap();
//                }
//        }
//    }
//
//    @Override
//    public void onMapReady(@NonNull GoogleMap googleMap) {
//        Toast.makeText(this, "Map is Ready", Toast.LENGTH_SHORT).show();
//        mMap = googleMap;
//
//        if (mLocationPermissionGranted) {
//            getDeviceLocation();
//
//            // map location on the map
//            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                return;
//            }
//            mMap.setMyLocationEnabled(true);
//            //disable the ui so that i can add a search bar
//            mMap.getUiSettings().setMyLocationButtonEnabled(false);
//
//            //cal the init method
//            init();
//        }
//    }
//}