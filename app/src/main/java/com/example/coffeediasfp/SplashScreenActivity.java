package com.example.coffeediasfp;

import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashScreenActivity extends AppCompatActivity {
    Animation anim;
    ImageView imageView;

    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    FirebaseUser currentUser = mAuth.getCurrentUser();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
//        declare an image view to show the animation
        imageView = findViewById(R.id.imageView);
        //create the animation
        anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in) ;


        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }


            @Override
            public void onAnimationEnd(Animation animation) {
                FirebaseUser currentUser = mAuth.getCurrentUser();
                if(currentUser != null){
//            open the main activity
                    Intent intent  = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                    finish();
                }else{
                    Intent intent  = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

    imageView.startAnimation(anim);
    }
}