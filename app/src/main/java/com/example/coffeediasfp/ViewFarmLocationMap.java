package com.example.coffeediasfp;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.gson.Gson;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ViewFarmLocationMap extends AppCompatActivity implements OnMapReadyCallback {

    TextView farmName , farmSize;

    FarmFieldModal farmFieldModal;
    Gson gson;
    GoogleMap mMap;
    PolygonOptions polygonOptions;
    List<LatLng> fieldsMarkers;
    AppCompatButton editFarmDetails;
    ImageButton  gps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_farm_location_map);
        fieldsMarkers = new ArrayList<>();

        //show map
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        farmName = findViewById(R.id.editfarmName);
        farmSize = findViewById(R.id.editFarmSize);
        editFarmDetails = findViewById(R.id.editfarmDetails);


        farmFieldModal = getIntent().getParcelableExtra("farmModal");
        farmName.setText(farmFieldModal.getFarmName().toString());
        farmSize.setText(farmFieldModal.getFarmSize().toString());

        editFarmDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), EditFarmFieldandMapActivity.class);
                intent.putExtra("farmdetails", farmFieldModal);
                startActivity(intent);
                finish();
            }
        });





    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        String coordinatesJSON = farmFieldModal.getFarmLocation().toString();
        Log.d(TAG, "onMapReady: " + coordinatesJSON);
        fieldsMarkers = convertJsontoLatLngList(coordinatesJSON);
        Log.d(TAG, "onMapReady: "+fieldsMarkers.toString());
        drawPolygon(fieldsMarkers);



    }
    private void drawPolygon(List <LatLng> receivedMarkers) {
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
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18f));
    }
    public  static List<LatLng> convertJsontoLatLngList(String JSONString){
        List<LatLng> latLngList = new ArrayList<>();
        try {
            Gson gson1 = new Gson();
            Type ListType = new TypeToken<List<LatLng>>(){}.getType();
            List<LatLng> tempLatLngList= gson1.fromJson(JSONString, ListType);

            if(tempLatLngList != null){
                latLngList.addAll(tempLatLngList);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return  latLngList;
    }
}
