package com.example.coffeediasfp;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.collection.LLRBNode;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ShowFarmsMapArea extends AppCompatActivity implements OnMapReadyCallback {

//    List<farmMArker> farmPolygon;

    GoogleMap mMap;
    PolygonOptions polygonOptions;

    FarmFieldModal recievedFarmFieldModal;
    FarmFieldModal farmFieldModal;
    String farmName, farmSize, farmID;
    ImageButton back_btn;

    AppCompatButton saveFarmDetails;

    EditText editFarmName, editFarmSize;
    ArrayList<LatLng> receivedMarkers;

    Gson gson;
    String coordinatesJSON;

    String userId, email;

    private FirebaseDatabase firebaseDatabase;

    private DatabaseReference databaseReference ,farmRef;

    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser user ;

    public void onStart(){
        super.onStart();
        user = mAuth.getCurrentUser();
        if(user != null){
            userId = user.getUid();
            email = user.getEmail();
            //        database stuff
            firebaseDatabase = FirebaseDatabase.getInstance();
            databaseReference = firebaseDatabase.getReference("AllFarms").child(userId).child("Farms");
            databaseReference.setValue(true);
        }else {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
            finish();
            Toast.makeText(this, "You are not logged in", Toast.LENGTH_SHORT).show();
        }
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_farms_map_area);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        editFarmName = findViewById(R.id.editfarmName);
        editFarmSize = findViewById(R.id.editFarmSize);
        back_btn = findViewById(R.id.back_btn);
        saveFarmDetails = findViewById(R.id.saveFarmDetails);
        receivedMarkers= new ArrayList<>();
        gson =new Gson();
        recievedFarmFieldModal = getIntent().getParcelableExtra("farmModal");
        Log.d(TAG, "onCreate: Farm Modal" + recievedFarmFieldModal.getFarmName());
        receivedMarkers= getIntent().getParcelableArrayListExtra("coordinates");
        Log.d(TAG, "onCreate: Farm Modal" + receivedMarkers.toString());

        coordinatesJSON = gson.toJson(receivedMarkers);
        Log.d(TAG, "onMapReady: Cordinates" + coordinatesJSON);


//        coordinates = Collections.singletonList(receivedMarkers.toString());
        Log.d(TAG, "onCreate: received farmModal" + recievedFarmFieldModal.getFarmName());
        Log.d(TAG, "onMapReady: Markers" + receivedMarkers.toString());
        farmName = recievedFarmFieldModal.getFarmName();
        farmSize = recievedFarmFieldModal.getFarmSize();
        farmID = recievedFarmFieldModal.getFarmID();

        editFarmName.setText(farmName);
        editFarmSize.setText(farmSize);


        back_btn.setOnClickListener(view -> {
            onBackPressed();
        });


    }

    private void drawPolygon(ArrayList<LatLng> receivedMarkers) {
        polygonOptions = new PolygonOptions();
        for (LatLng latLng : receivedMarkers) {
            polygonOptions.add(latLng);
        }
        moveCamera(receivedMarkers.get(0));
        //set properties

        polygonOptions.strokeColor(Color.argb(100, 0, 255, 0));
        polygonOptions.fillColor(Color.argb(100, 0, 0, 255));
        polygonOptions.clickable(true);
        mMap.addPolygon(polygonOptions);
    }

    private void moveCamera(LatLng latLng) {
        Log.d(TAG, "moveCamera: Moving Camera to :lat :" + latLng.latitude + ", lng : " + latLng.longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f));
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        Toast.makeText(this, "Map Ready", Toast.LENGTH_SHORT).show();
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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

        drawPolygon(receivedMarkers);


        farmFieldModal = new FarmFieldModal(editFarmName.getText().toString(), editFarmSize.getText().toString(),
                recievedFarmFieldModal.getFarmID(), coordinatesJSON);
        

        saveFarmDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FarmFieldModal finalFarmDetails = new FarmFieldModal(farmName, farmSize,farmID,coordinatesJSON);


                //
//                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Farms");

                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        databaseReference.child(farmID).setValue(finalFarmDetails);
                        Toast.makeText(ShowFarmsMapArea.this, "Farm details saved", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "onDataChange: Saved"+ finalFarmDetails.describeContents());
                        Intent intent = new Intent(getApplicationContext(), FarmFieldsList.class);
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
    }



}