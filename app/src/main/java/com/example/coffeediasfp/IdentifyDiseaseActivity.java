package com.example.coffeediasfp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.provider.MediaStore;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import android.Manifest;


import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;


import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;

public class IdentifyDiseaseActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int PICK_IMAGE_REQUEST = 2;

    private ImageView imageView;
    Button btnCapture;
    Button btnChoose;

    Button btnPredict;


//    Location Stuff
    FusedLocationProviderClient mFusedLocationClient;
    TextView latitudeTextView , longitudeTextView;
    int PERMISSION_ID = 44;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_identify_disease);

//        Initialize  UI elements
        imageView = findViewById(R.id.imageView);
        btnCapture = findViewById(R.id.btnCapture);
        btnChoose = findViewById(R.id.btnChoose);
        btnPredict = findViewById(R.id.btnPredict);
        longitudeTextView = findViewById(R.id.lonTextView);
        latitudeTextView = findViewById(R.id.latTextView);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

//        capture the photo
        ActivityResultLauncher<Intent> captureLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),result->{
                    if(result.getResultCode() == Activity.RESULT_OK ){
                        // process the captured Photo
                        Bundle extras = result.getData().getExtras();
                        if (extras != null){
                            Bitmap imageBitmap = (Bitmap) extras.get("data");
                            imageView.setImageBitmap(imageBitmap);
                        }
                    }

                });

//        Choose am image from the gallery

        ActivityResultLauncher <Intent> chooseLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result-> {
                    if(result.getResultCode() == Activity.RESULT_OK && result.getData() != null){
//                        process the chosen image from gallery
                        Uri selectedImageUri = result.getData().getData();
                        try {
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri);
                            imageView.setImageBitmap(bitmap);
                        }catch(IOException e){
                            e.printStackTrace();
                        }
                    }
                }
        );

        btnCapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
// check permission
//                if (ContextCompat.checkSelfPermission((getApplicationContext()), CAMERA) == = PackageManager.PERMISSION_GRANTED) {
//                    Intent takePictureIntent = new Intent((MediaStore.ACTION_IMAGE_CAPTURE));
//                    captureLauncher.launch(takePictureIntent);
//
//                })
//                Launch tha camera to capture a photo
                Intent takePictureIntent = new Intent((MediaStore.ACTION_IMAGE_CAPTURE));
                captureLauncher.launch(takePictureIntent);
//
            }
        });

        btnChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Launch the gallery to chose an image
                Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                chooseLauncher.launch(galleryIntent);
            }
        });

     btnPredict.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
//              get location and display location-
              getLastLocation();
//              get prediction
//              save location
//              save prediction


          }
      });
    }




//  Location methods and stuffs

    @SuppressLint("missingPermission")
    public void getLastLocation(){
//        check if premissions are given

        if(checkPermissions()){
//            check if location is enabled
            if(isLocationEnabled()){
                //get lastloaction
                mFusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>(){
                    @Override
                    public void onComplete(@NonNull Task<Location> task){
                        Location location =task.getResult();
                        if(location == null ){
                            requestNewLocationData();
                        }else{
                            latitudeTextView.setText(location.getLatitude() + "");
                            longitudeTextView.setText(location.getLongitude() + "");

                        }
                    }
                });

            }else{
//                have a toast message for the user and oprn location in settings
                Toast.makeText(this, "Please turn on " + "your location ...", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        }else{
//           request premission
            requestPermissions();
        }

    }

        @SuppressLint("missingPermission")
        public void requestNewLocationData(){
//            initializing location Request object with appropriatemethods
            LocationRequest mLocationRequest = new LocationRequest()
                    .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                    .setInterval(5000) // 5 seconds
                    .setFastestInterval(0) // Set to 0 to request updates as fast as possible
                    .setNumUpdates(1); // Request a single location update

            // setting LocationRequest
            // on FusedLocationClient
            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
            mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());

        }
    private LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();
            latitudeTextView.setText("Latitude: " + mLastLocation.getLatitude() + "");
            longitudeTextView.setText("Longitude: " + mLastLocation.getLongitude() + "");
        }
    };

    // method to check for permissions
    private boolean checkPermissions() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;

        // If we want background location
        // on Android 10.0 and higher,
        // use:
        // ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    // method to request for permissions
    private void requestPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_ID);
    }

    // method to check
    // if location is enabled
    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    // If everything is alright then
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_ID) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (checkPermissions()) {
            getLastLocation();
        }
    }
}
