package com.example.coffeediasfp;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.tv.TvInputService;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.mail.Authenticator;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.Transport;
import javax.mail.Message.RecipientType;





public class Diagnosis extends AppCompatActivity implements EmailService.EmailCallback, OnFetchDetailsListener{

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReferenceFarm;
    DatabaseReference databaseReferenceRecommendations;
    DatabaseReference databaseReferenceDiseases;

     Spinner farmSpinner;
     Button saveDiagnosis;

//     Farm id
    String farmID;
    String diseaseID;

    private  ArrayList<RecommendationModal>recommendationModalArrayList;
    private  ArrayList<DiseaseModal> diseaseModalArrayList;

    String latitude, longitude, diseaseName;
    ProgressBar progressBar;
    float  diseaseConfidence;

    TextView diseaseNameTV, confidenceTV, recommendationTV;

    StringBuilder recommendations;

    DiseaseModal diseaseModal;

    DatabaseReference databaseReferenceDiagnosis;
    String userId, email;
    FirebaseUser user ;
    FirebaseAuth mAuth ;


    private static final String CHANNEL_ID = "my_channel_id";
    private static final CharSequence CHANNEL_NAME = "My Channel";

    //on start method
    public void onStart(){
        super.onStart();

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        if(user != null){
            userId = user.getUid();
            email = user.getEmail();
            //        database stuff
            firebaseDatabase = FirebaseDatabase.getInstance();
            databaseReferenceFarm = firebaseDatabase.getReference("AllFarms").child(userId).child("Farms");
            //        loadFarms , generate diagnosis modal;
            loadFarms();
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
        setContentView(R.layout.activity_diagnosis);
        firebaseDatabase = FirebaseDatabase.getInstance();
        farmSpinner = findViewById(R.id.idFarmSpinner);
        databaseReferenceRecommendations = firebaseDatabase.getReference("Recommendations");
        databaseReferenceDiagnosis = firebaseDatabase.getReference("Diagnosis");
        diseaseNameTV = findViewById(R.id.diseaseName);
        confidenceTV =findViewById(R.id.confidencesTV);
        recommendationTV = findViewById(R.id.recommendationText);
        saveDiagnosis = findViewById(R.id.idAddDiagnosis);
        progressBar = findViewById(R.id.idDiagnosisProgressBar);

        recommendationModalArrayList = new ArrayList<>();
        diseaseModalArrayList = new ArrayList<>();

        latitude = getIntent().getStringExtra("latitude");
        longitude = getIntent().getStringExtra("longitude");

        diseaseName = getIntent().getStringExtra("diseaseName");
        diseaseConfidence = getIntent().getFloatExtra("confidence", 100) * 100;

        getDiseaseWithName(diseaseName);

//        getRecomendationforDisease(diseaseID);

        //sendEmail(this,"ndegwaproff@gmail.com", "Test Email", "THis is an Email to test my function if you see this know that you have passed");
        diseaseNameTV.setText(diseaseName);
        confidenceTV.setText(diseaseConfidence + "% ");

//         notificationMessage(this, "First" , "We will Rock you");

        //    ----------------------------------------------Save Diagnisis For the farm -------------------------------------------------------------

        saveDiagnosis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Strings for the data to be out in modal
                String diagnosisID = UUID.randomUUID().toString();
                String farmidText = farmID;
                String diseaseIDTExt = diseaseID;
                String recommendationText = String.valueOf(recommendations);
                String percentageText = diseaseConfidence + "% ";
                LatLng latLng = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));

                Gson gson = new Gson();

                String location = gson.toJson(latLng);

                DiagnosisModal diagnosisModal = new DiagnosisModal(diagnosisID, farmidText,diseaseIDTExt,recommendationText, location,percentageText  );

//                DiagnosisModal diagnosisModal = new DiagnosisModal(diagnosisID, farmidText, diseaseIDTExt, recommendationText, longitude, latitude,percentageText);
//                longitude, latitude
//
//                add the Strings to the modal
                databaseReferenceDiagnosis.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                    databaseReferenceDiagnosis.child(diagnosisID).setValue(diagnosisModal);
//                    setNotification(farmID, diseaseID);
                    //redirect to the DIagnisis list
                        Toast.makeText(Diagnosis.this, "Record added successfully" + diseaseID, Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(), DiagnosisList.class));
                        finish();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(Diagnosis.this, "failed ....."  + error, Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });
    }


    private void getRecomendationforDisease(String diseaseID){

        if(diseaseID != null) {
            recommendationModalArrayList.clear();
            recommendations = new StringBuilder();
            databaseReferenceRecommendations.orderByChild("diseaseID").equalTo(diseaseID).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        RecommendationModal recommendationModal = dataSnapshot.getValue(RecommendationModal.class);
                        recommendations.append(String.format("%s.  \n", recommendationModal.getRecommendationText()));
                        recommendationTV.setText(recommendations.toString());
                        Log.d("recommendation", String.valueOf(recommendations));
                        progressBar.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(getApplicationContext(), "Failed ... " + error, Toast.LENGTH_SHORT).show();

                }
            });
        }else{
            Toast.makeText(this, "DiseaseId is Null", Toast.LENGTH_SHORT).show();
        }
    }

//    getdiseasename and load it disea sename Tv
    private void  getDiseaseWithName(String diseaseName){

        databaseReferenceDiseases = firebaseDatabase.getReference("Diseases");

            databaseReferenceDiseases.orderByChild("diseaseName").equalTo(diseaseName).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                   diseaseModal = snapshot.getValue(DiseaseModal.class);
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        diseaseModal = dataSnapshot.getValue(DiseaseModal.class);
                        if (diseaseModal != null) {
                            diseaseID = diseaseModal.getDiseaseID();
                            Log.d("DiseaseID", diseaseID);
                            Toast.makeText(Diagnosis.this, "the DiseaseModal is not null" + diseaseID, Toast.LENGTH_SHORT).show();
                            getRecomendationforDisease(diseaseID);
                        } else {
                            Toast.makeText(Diagnosis.this, "the DiseaseModal is null", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(Diagnosis.this, "Failed to Retrieve" + error, Toast.LENGTH_SHORT).show();
                }

              });

    }

//   ------------------------------ Loading and populating the Farm Spinner

    private void loadFarms() {
        databaseReferenceFarm = firebaseDatabase.getReference("AllFarms").child(userId).child("Farms");
//            getting all Farms  ***
        databaseReferenceFarm.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<FarmFieldModal> farmList = new ArrayList<>();
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    FarmFieldModal farm = snapshot1.getValue(FarmFieldModal.class);
                    farmList.add(farm);
                }
                populateSpinner(farmList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Diagnosis.this, "Failed to retrieve the Farms..." + error, Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void populateSpinner(List<FarmFieldModal> farmlist){
        Map<String, String> farmMap = new HashMap<>();

        for(FarmFieldModal farmField : farmlist){
            farmMap.put(farmField.getFarmID(), farmField.getFarmName());
        }
//        adapter for the spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                new ArrayList<>(farmMap.values())
        );
         adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
         farmSpinner.setAdapter(adapter);

         farmSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
             @Override
             public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                 farmID = new ArrayList<>(farmMap.keySet()).get(i);
             }

             @Override
             public void onNothingSelected(AdapterView<?> adapterView) {

             }
         });

    }

    // ---------------------------------------Notifications ------------------------------------------

    private void notificationMessage(Context context, String title, String content){
        // Create an intent when the notification is clicked
        Intent i = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, i , PendingIntent.FLAG_IMMUTABLE);

        // Create the notification channel (required for Android Oreo and above)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        // Create the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(content)
                .setSmallIcon(R.drawable.leaf)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        //Get the notification manager system service
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Display the notification
        notificationManager.notify(0, builder.build());
    }
    public void saveNotification(String farmID, String diseaseID){
        Log.d("Notification", farmID);
        if (farmID != null && diseaseID != null) {
            // notificationMessage();
            Log.d("Notification", farmID + diseaseID);
        } else {
            Log.e("sendNotification", "farmID or diseaseID is null");
        }
    }




//public void sendEmail(Context context,String recipientEmail, String subject, String body) {
//    // Check for internet connection
////    if (!isConnected()) {
////        // Handle the case where there is no internet connection
////        Toast.makeText(this, "No internet connection. Email will be sent when connection is available.", Toast.LENGTH_SHORT).show();
////        // You may choose to save the email details and send it later when the connection is available
////        // For simplicity, we'll just return here
////        return;
////    }
//    Intent intent = new Intent(context, EmailService.class);
//    intent.putExtra("recipientEmail", recipientEmail);
//    intent.putExtra("subject", subject);
//    intent.putExtra("body", body);
//
////    EmailService emailService = new EmailService();
////    //Set the callback
////    emailService.setCallback(Diagnosis.this);
////    // Start Service
////    emailService.
//    startService(intent);
//}

    // Callback method for email sent successfully
    @Override
    public void onEmailSent() {
        // Notify the user about the successful email sending
        Toast.makeText(this, "Email sent successfully", Toast.LENGTH_SHORT).show();
        Log.d("onEmailSent", "onEmailSent: horray!!!" );
    }

    // Callback method for email sending failure
    @Override
    public void onEmailFailed() {
        // Notify the user about the email sending failure
        Toast.makeText(this, "Failed to send email", Toast.LENGTH_SHORT).show();
        Log.d("onEmailFailed", "onEmailFailed:  BOOOOO!!! :(");
    }

    public boolean isConnected(){
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }
// save the count in Notifications and check if the is is viable to send a notification
public void getNotificationcount(String userId){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference notifications = database.getReference("Notifications").child(userId);
        //get all notificationCount for each disease on the farm
    notifications.addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            for(DataSnapshot farms : snapshot.getChildren()){
                    for (DataSnapshot disease: farms.getChildren()){
                        NotificationCount count = disease.getValue(NotificationCount.class);
//                      // counr gotter
                        Log.d(TAG, "onDataChange: " + count.getCount());
                        int countNum = count.getCount();
                        if ((countNum  % 5) == 0){
                            notificationMessage(getApplicationContext(), "This is a test", "this is to test the" +
                                    " functionality of the  notification module");
                        }
                    }
            }
        }
        @Override
        public void onCancelled(@NonNull DatabaseError error) {
            Toast.makeText(Diagnosis.this, "Failed" + error, Toast.LENGTH_SHORT).show();
        }
    });
}
public NotificationModel createNotification(String farmId, String diseaseID, int notificationCount){
        DatabaseReference notificationsRef = FirebaseDatabase.getInstance().getReference(userId).child("Notifications");
        String diseaseName = onDiseaseNameFetch(diseaseID);
        String farmName = onFarmFetch(diseaseID);
        String title = "Report of disease " + diseaseName  + " on your farm";
        String body = "The total number of  reports are "+ notificationCount  +"for disease " +
                diseaseName + " in the farm " + farmName;
        String notificationID = UUID.randomUUID().toString();
        // create a notification model
    NotificationModel notification = new NotificationModel(notificationID, farmId, diseaseID, title, body, notificationCount);
        notificationsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //save the notification
                notificationsRef.child(notificationID).setValue(notification);
                //send notification
                notificationMessage(getApplicationContext(), title, body);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    return notification;
}


    @Override
    public String onFarmFetch(String farmID) {
        final String[] farmName = new String[1];
        DatabaseReference farmRef = FirebaseDatabase.getInstance().getReference("ALlFarms")
                .child(userId).child("Farms").child(farmID);
        farmRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                FarmFieldModal farm = snapshot.getValue(FarmFieldModal.class);
                farmName[0] = farm.getFarmName().toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Diagnosis.this, "Error" + error, Toast.LENGTH_SHORT).show();
            }
        });
        return Arrays.toString(farmName);
    }

    @Override
    public String onDiseaseNameFetch(String diseaseID) {
        DatabaseReference diseaseRef = FirebaseDatabase.getInstance().getReference("Diseases").child(diseaseID);
        final String[] diseaseName = new String[1];
    diseaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            DiseaseModal disease = snapshot.getValue(DiseaseModal.class);
            diseaseName[0] = disease.getDiseaseName();
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    });

        return diseaseName[0];
    }

    // send SMS
    private void sendSMS(@NonNull String no, String msg){
        Intent intent = new Intent(getApplicationContext(), Diagnosis.class);
        PendingIntent pi = PendingIntent.getActivity(getApplicationContext(),0, intent, PendingIntent.FLAG_IMMUTABLE);

        //Get the SmsManager instance and call the sendTextMessage method to send message
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(no.toString(), null, msg.toString(),
                pi, null
                );
        Toast.makeText(getApplicationContext()    , "Message Sent Successfully", Toast.LENGTH_SHORT).show();
    }
}




