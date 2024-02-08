package com.example.coffeediasfp;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.maps.android.heatmaps.Gradient;
import com.google.maps.android.heatmaps.HeatmapTileProvider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class DiseaseHeatmapActivity extends AppCompatActivity implements OnMapReadyCallback {

    GoogleMap mMap;
    String farmId;
    String diseaseID;
    Spinner diseaseSpinner, farmSpinner;
    ImageButton back_btn, view_all_btn;
    PolygonOptions polygonOptions;
    ArrayList<DiagnosisModal> diagnosisModalArrayList = new ArrayList<>();
    List<LatLng> diseaseLocations = new ArrayList<>();

    ArrayList<DiseaseModal> diseaseModalArrayList;
    TileOverlay overlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_disease_heatmap);
        back_btn = findViewById(R.id.back_btn);
        view_all_btn  = findViewById(R.id.ic_all_diseases);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        // back button
        back_btn.setOnClickListener(view -> onBackPressed());




//        gets Diagnosis and generates heatMaps
        getDiagnosis();

//        gets the Diseases and loads them to Disease Spinner
        getDisease();
//        gets the Farms and loads them to Farms Spinner and generate farm Boundaries
        getFarms();

        //view All dieaseses heatmap
        view_all_btn.setOnClickListener(view -> {
            Toast.makeText(this, "Showing a heatmap for all diseases", Toast.LENGTH_SHORT).show();
            DatabaseReference db = FirebaseDatabase.getInstance().getReference("Diagnosis");

            // get an array for the id
            ArrayList<String> diseaseList =new ArrayList<>();
            for(DiseaseModal disease : diseaseModalArrayList){
                String diseaseID = disease.getDiseaseID();
                diseaseList.add(diseaseID);
            }



            db.addValueEventListener(new ValueEventListener() {
                List<DiagnosisModal> diagnosisList = new ArrayList<>();
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot diagnosisModal : snapshot.getChildren()) {
                        DiagnosisModal diagnosisModal1 = diagnosisModal.getValue(DiagnosisModal.class);
                        diagnosisList.add(diagnosisModal1);
                    }
                    Log.d(TAG, "onDataChange: "+ diagnosisList.toString());


                    int numDiseases = diseaseModalArrayList.size();
                    // Generate contrasting colors based on the number of diseases
                    int[] colors = generateContrastingColors(numDiseases);


                    Toast.makeText(DiseaseHeatmapActivity.this, "Locations converted", Toast.LENGTH_SHORT).show();

//                    ArrayList<LatLng> disease0 = new ArrayList<>();
                    Log.d(TAG, "onDataChange: diseaseList" + diseaseList );

                    if (diseaseList != null) {
                        for (int i = 0; i < diseaseList.size()- 1; i++) {
                            List<String> diseaseX = new ArrayList<>();
                            List<LatLng> diseaseXcoodinates = new ArrayList<>();
                            for(DiagnosisModal diagnosisModal :diagnosisList){
                                if (diagnosisModal.getDiseaseID().equals(diseaseList.get(i))){
                                    String coordinates =  diagnosisModal.getLocation();
                                    diseaseX.add(coordinates);
                                }
                            }
                            diseaseXcoodinates = jsonToLatLng(diseaseX.toString());
                            addHeatMap(diseaseXcoodinates);
                        }
                    }

                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        });

    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

    }

// get all infromation from the Diagnisis Node
    public  void getDiagnosis(){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Diagnosis");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snapshot1 : snapshot.getChildren()){
                    DiagnosisModal diagnosisModal = snapshot1.getValue(DiagnosisModal.class);
                    diagnosisModalArrayList.add(diagnosisModal);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(DiseaseHeatmapActivity.this, "Error occurred " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void getDisease(){
        diseaseModalArrayList = new ArrayList<>();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Diseases");
         databaseReference.addValueEventListener(new ValueEventListener() {
             @Override
             public void onDataChange(@NonNull DataSnapshot snapshot) {
                 for(DataSnapshot snapshot1 : snapshot.getChildren()){
                    DiseaseModal diseaseModal = snapshot1.getValue(DiseaseModal.class);
                    diseaseModalArrayList.add(diseaseModal);
                 }
                 if (!diseaseModalArrayList.isEmpty() || diseaseModalArrayList != null ){
                     populateDiseaseSpinner(diseaseModalArrayList);
                 }

             }
             @Override
             public void onCancelled(@NonNull DatabaseError error) {
                 Toast.makeText(DiseaseHeatmapActivity.this, "Failed to load Diseases" +error, Toast.LENGTH_SHORT).show();
             }
         });
    }
    public void populateDiseaseSpinner(ArrayList<DiseaseModal> diseaseModalArrayList){
        Map<String, String> diseaseMap = new HashMap<>();
//        diseaseMap.put("Select Disease", "");
        for(DiseaseModal disease : diseaseModalArrayList){
            diseaseMap.put( disease.getDiseaseName(), disease.getDiseaseID());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                new ArrayList<>(diseaseMap.keySet())
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        diseaseSpinner = findViewById(R.id.DiseaseSpinner);
        diseaseSpinner.setAdapter(adapter);
        diseaseSpinner.setSelection(0);
        diseaseSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selectDiseaseName = (String) adapterView.getItemAtPosition(i);
                diseaseID = diseaseMap.get(selectDiseaseName);
                Log.d(TAG, "onItemSelected: "+ diseaseID.toString());
                // generate a heatMap for the diagnosis with this diseaseName

                List<String> locationString = new ArrayList<>();
                List<LatLng> locationLatLngs = new ArrayList<>();

//                get the Location for all diseases of the selected farm and disease
                for(DiagnosisModal diagnosisModal : diagnosisModalArrayList){
                    if(diagnosisModal.getDiseaseID().equals(diseaseID) &&
                            diagnosisModal.getFarmID().equals(farmId)){
                        locationString.add(diagnosisModal.getLocation());
                    }
                }

                locationLatLngs = jsonToLatLng(locationString.toString());
                Log.d(TAG, "onItemSelected: Location latlngs" + locationLatLngs);

                if (locationLatLngs == null || locationLatLngs.isEmpty()){
                    Toast.makeText(DiseaseHeatmapActivity.this, "No diseasese found", Toast.LENGTH_SHORT).show();
                    return;
                }
//                int[] colors = generateContrastingColors(1);
                if(overlay != null){
                    overlay.remove();
                }

                addHeatMap(locationLatLngs);


                //get diagnosis locations
                //    convert to laglng
                // add to heatm


            };

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // generate heat maps of all diseasea

            }
        });

    }

    public  void getFarms(){
        ArrayList<FarmFieldModal> farmFieldModalArrayList = new ArrayList<>();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Farms");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot snapshot1: snapshot.getChildren()){
                    FarmFieldModal farmFieldModal =snapshot1.getValue(FarmFieldModal.class);
                    farmFieldModalArrayList.add(farmFieldModal);
                    //polpulate sinner and show famr boundaries that are selected
                    populateFarmSpinner(farmFieldModalArrayList, databaseReference);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(DiseaseHeatmapActivity.this, "Failed to load Farms" +error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void populateFarmSpinner(ArrayList<FarmFieldModal>farmFieldModalArrayList, DatabaseReference databaseReference){
        Map<String, String> farmMap = new HashMap<>();
//        farmMap.put("Select Farm", "");
        for(FarmFieldModal farm : farmFieldModalArrayList){
            farmMap.put(farm.getFarmName(), farm.getFarmID());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                new ArrayList<>(farmMap.keySet())
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        farmSpinner= findViewById(R.id.idFarmSpinner);
        farmSpinner.setAdapter(adapter);
//        farmSpinner.setSelection(0);
        farmSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selectedFarmName = (String) adapterView.getItemAtPosition(i);
                farmId = farmMap.get(selectedFarmName);
                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        FarmFieldModal fieldModal = snapshot.child(farmId).getValue(FarmFieldModal.class);
                        String farmLocation = fieldModal.getFarmLocation();
                        if(farmLocation != null){
                            // generate farm boundaries
                            List<LatLng> farmLatLngs =  jsonToLatLng(farmLocation);
                            drawPolygon(farmLatLngs);
                        }else{
                            Toast.makeText(DiseaseHeatmapActivity.this, "Farm Boundaries not set please set ..", Toast.LENGTH_SHORT).show();
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(DiseaseHeatmapActivity.this, " Failed to get data" + error, Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

//    change json to LatLng

    private List<LatLng> jsonToLatLng(String jsonCoordinates){
        Gson gson = new Gson();
        LatLng[] latLngs = gson.fromJson(jsonCoordinates, LatLng[].class);
        return  Arrays.asList(latLngs);
    }

    private void drawPolygon(List<LatLng> receivedMarkers) {
//      clear any farm boundaries
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

//    --------------------------------- Heatmap stuff------------------------------
private List<LatLng> getDiagnosisLatLang(ArrayList<DiagnosisModal> diagnosisModalArrayList) {
    Gson gson = new Gson();
    List<LatLng> locationsList = new ArrayList<>();
    for (DiagnosisModal diag : diagnosisModalArrayList) {
        //show the the disease location for the specific farm
        if (diag.getDiseaseID().equals(diseaseID) && diag.getFarmID().equals(farmId)) {
            String jsonCoords = diag.getLocation();
            LatLng location = gson.fromJson(jsonCoords, LatLng.class);
            locationsList.add(location);
            Log.d(TAG, "getDiagnosisLatLang: " + location.toString());
        }
    }
    return locationsList;
}

// create heatmap
    private void addHeatMap(List<LatLng> list){

        if(list == null || list.isEmpty()){
            Log.e(TAG, "addHeatMap: List is empty" );
            Toast.makeText(this, "Could not load the heatmap", Toast.LENGTH_SHORT).show();
            return;
        }

        moveCamera(list.get(0));
        //clear the map
//        if (overlay != null) {
//            overlay.remove();
//        }

        // Only one disease, use the same colors for both gradients
            int[] colors = generateContrastingColors(2);
            // Define the start points for the gradient
            float[] startPoints = {0.2f, 1.0f}; // Two colors for each gradient

            // Create gradient with the provided colors and start points
            Gradient gradient = new Gradient(colors, startPoints);

            HeatmapTileProvider provider = new HeatmapTileProvider.Builder()
                    .data(list)
                    .gradient(gradient)
                    .build();
            overlay = mMap.addTileOverlay(new TileOverlayOptions().tileProvider(provider));
            overlay.getFadeIn();
    }


    private  int getRandomNumberInRange(int min , int max){
        Random r = new Random();
        return r.nextInt((max-min) + 1) + min;
    }
    private int[] generateContrastingColors(int numColors){
        int[]colors = new int[numColors];
        float[] hsv = new float[3];
        Random rand = new Random();
//         generate unique contrasting colors
        for (int i = 0; i < numColors; i++) {
//            generate random hue value
            hsv[0] =rand.nextInt(360);
            // Set saturation and value to make the color vibrant
            hsv[1] = 1.0f;
            hsv[2] = 1.0f;
            // Convert HSV to RGB and store the color
            colors[i] = Color.HSVToColor(hsv);
        }
        return  colors;
    }


//    private void addHeatMap(List<LatLng> list, List<String> diseaseIds) {
//        if (list == null || list.isEmpty()) {
//            Log.e(TAG, "addHeatMap: List is empty");
//            Toast.makeText(this, "Could not load the heatmap", Toast.LENGTH_SHORT).show();
//            return;
//        }
//        // Clear the map
//        if (overlay != null) {
//            overlay.remove();
//        }
//
//        if (diseaseIds.size() == 1) {
//            // Only one disease, use the same colors for both gradients
//            int[] colors = generateContrastingColors(2);
//            // Define the start points for the gradient
//            float[] startPoints = {0.2f, 1.0f}; // Two colors for each gradient
//
//            // Create gradient with the provided colors and start points
//            Gradient gradient = new Gradient(colors, startPoints);
//
//            HeatmapTileProvider provider = new HeatmapTileProvider.Builder()
//                    .data(list)
//                    .gradient(gradient)
//                    .build();
//            overlay = mMap.addTileOverlay(new TileOverlayOptions().tileProvider(provider));
//            overlay.getFadeIn();
//        } else {
//            // Multiple diseases, generate unique contrasting colors for each disease
//            for (String diseaseId : diseaseIds) {
//                int[] colors = generateContrastingColors(2); // Generate 2 colors for each disease
//                // Define the start points for the gradient
//                float[] startPoints = {0.2f, 1.0f}; // Two colors for each gradient
//
//                // Create gradient with the provided colors and start points
//                Gradient gradient = new Gradient(colors, startPoints);
//
//                HeatmapTileProvider provider = new HeatmapTileProvider.Builder()
//                        .data(list)
//                        .gradient(gradient)
//                        .build();
//                overlay = mMap.addTileOverlay(new TileOverlayOptions().tileProvider(provider));
//                overlay.getFadeIn();
//            }
//        }
//    }

}
