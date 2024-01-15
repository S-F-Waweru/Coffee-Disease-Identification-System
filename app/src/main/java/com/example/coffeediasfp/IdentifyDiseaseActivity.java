package com.example.coffeediasfp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationManager;
import android.media.ThumbnailUtils;
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
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;
import com.example.coffeediasfp.ml.CoffeeDiseaseModel;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;


public class IdentifyDiseaseActivity extends AppCompatActivity {

    int imageSize = 224;

    private ImageView imageView;

//    Interpreter tflite = new Interpreter(loadModelFile)
    Button btnCapture;
    Button btnChoose;

    AppCompatButton btnSave;

    TextView result;
    TextView confidenceTV;

//    Location Stuff
    FusedLocationProviderClient mFusedLocationClient;
    TextView latitudeTextView , longitudeTextView;
    String longitude, latitude;
    int PERMISSION_ID = 44;
//   the string that will hold the disease with the  highest confidence
    String diseaseName;
    float maxConfidence =  0 ;


    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReferenceDiagnosis;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_identify_disease);

//        Initialize  UI elements
        imageView = findViewById(R.id.imageView);
        btnCapture = findViewById(R.id.btnCapture);
        btnChoose = findViewById(R.id.btnChoose);
        btnSave = findViewById(R.id.btnSave);
        longitudeTextView = findViewById(R.id.lonTextView);
        latitudeTextView = findViewById(R.id.latTextView);
        result = findViewById(R.id.diseaseResult);
        confidenceTV = findViewById(R.id.confidencesTV);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReferenceDiagnosis = firebaseDatabase.getReference("Diagnosis");



//        capture the photo
        ActivityResultLauncher<Intent> captureLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),result->{
                    if(result.getResultCode()   == Activity.RESULT_OK){
                        Bundle extras = result.getData().getExtras();
                        if(extras != null){
                            Bitmap image = (Bitmap) extras.get("data");
//          center  crop the image to be square
                            int dimension = Math.min(image.getWidth(), image.getHeight());
                            image = ThumbnailUtils.extractThumbnail(image, dimension, dimension);
                            imageView.setImageBitmap(image);

                            image = Bitmap.createScaledBitmap(image, imageSize, imageSize, false);
                            classifyImage(image);
                        }

                    }
//                    if(result.getResultCode() == Activity.RESULT_OK ){
//                        // process the captured Photo
//                        Bundle extras = result.getData().getExtras();
//                        if (extras != null){
//                            Bitmap imageBitmap = (Bitmap) extras.get("data");
//                            imageView.setImageBitmap(imageBitmap);
//                        }
//                    }

                });

//        Choose am image from the gallery

        ActivityResultLauncher <Intent> chooseLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result-> {
                    if(result.getResultCode() == Activity.RESULT_OK && result.getData() != null){
//                        process the chosen image from gallery
                        Uri selectedImageUri = result.getData().getData();
                        try {
                            Bitmap image = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri);
//                            image to be square
                            int dimension = Math.min(image.getWidth(), image.getHeight());
                            image = ThumbnailUtils.extractThumbnail(image, dimension, dimension);
                            imageView.setImageBitmap(image);

                            image = Bitmap.createScaledBitmap(image, imageSize, imageSize, false);
                            classifyImage(image);
//                            imageView.setImageBitmap(bitmap);
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
                if(checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED){
                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    captureLauncher.launch(takePictureIntent);
//                    startActivityForResult(takePictureIntent, 1);
                }else{
                    requestPermissions(new String[]{Manifest.permission.CAMERA}, 100);
                }

            }
        });


        btnChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Launch the gallery to chose an image
                Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                chooseLauncher.launch(galleryIntent);
                getLastLocation();
//                startActivityForResult(galleryIntent, 1);
            }

        });


        //-----------------------------------  save the Results in The Diagnosis Node----------------------------------------
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent  intent = new Intent(getApplicationContext(), Diagnosis.class);
//                create a bundle with allthe data and send it

                intent.putExtra("longitude",longitude );
                intent.putExtra("latitude", latitude);
                intent.putExtra("diseaseName",diseaseName );
                intent.putExtra("confidence", maxConfidence);
                startActivity(intent);
            }
        });
    }




//-----------------------------------  Loading the model and classifying the image method ----------------------------------------

    public void classifyImage(Bitmap image){
        try {
            CoffeeDiseaseModel model = CoffeeDiseaseModel.newInstance(getApplicationContext());

            // Creates inputs for reference.
            TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 224, 224, 3}, DataType.FLOAT32);
            ByteBuffer byteBuffer = ByteBuffer.allocateDirect(4 * imageSize * imageSize *   3);
            byteBuffer.order(ByteOrder.nativeOrder());

            int [] intValues = new int[imageSize * imageSize];
            image.getPixels(intValues, 0, image.getWidth(), 0, 0, image.getWidth(), image.getHeight());
            int pixel = 0;
            for(int i = 0 ; i < imageSize ; i++){
                for (int j= 0 ; j < imageSize ; j++){
                    int val = intValues[pixel++]; // RGB
//                    perform bitwise operation to extract RGB
                    byteBuffer.putFloat(((val >> 16) & 0xFF)* (1.f /255.f));
                    byteBuffer.putFloat(((val >> 8) & 0xFF)* (1.f /255.f));
                    byteBuffer.putFloat((val  & 0xFF)* (1.f /255.f));

                }
            }


            inputFeature0.loadBuffer(byteBuffer);

            // Runs model inference and gets result.
            CoffeeDiseaseModel.Outputs outputs = model.process(inputFeature0);
            TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();

            float [] confidences = outputFeature0.getFloatArray();
            int maxPos = 0;

            for(int i = 0; i < confidences.length ; i++){
                if(confidences[i] > maxConfidence){
                    maxConfidence = confidences[i];
                    maxPos = i;
                }
            }

            String [] classes = {"Healthy", "Leaf Rust", "Phoma", "Miner", "Cerscopora"};

            result.setText(classes[maxPos]);
            diseaseName = classes[maxPos];


//            Create a hash map with disease name and confidences

            StringBuilder s  = new StringBuilder(" ");
            for(int i = 0; i < classes.length; i++){
                s.append(String.format("%s: %.2f%%\n", classes[i], confidences[i] * 100));
            }

            confidenceTV.setText(s.toString());

            // Releases model resources if no longer used.
            model.close();
        } catch (IOException e) {
            // TODO Handle the exception
        }

    }


//-----------------------------------  Location methods and stuff ----------------------------------------

    @SuppressLint("missingPermission")
    public void getLastLocation(){
//        check if permissions are given

        if(checkPermissions()){
//            check if location is enabled
            if(isLocationEnabled()){
                //get last location
                mFusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>(){
                    @Override
                    public void onComplete(@NonNull Task<Location> task){
                        Location location =task.getResult();
                        if(location == null ){
                            requestNewLocationData();
                        }else{

//                            the lat and long methods return a  double  add "" to tuen to string
                            latitude = location.getLatitude() + "" ;
                            longitude = location.getLongitude() + "" ;
                            latitudeTextView.setText(latitude);
                            longitudeTextView.setText(longitude);

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
//            initializing location Request object with appropriate methods
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
