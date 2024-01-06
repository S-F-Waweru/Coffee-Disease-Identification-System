package com.example.coffeediasfp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    AppCompatButton login;
    TextInputEditText emailText, passwordText;
    TextView click_to_register;
    ProgressBar progressBar;
    FirebaseAuth mAuth;
//    FirebaseUser currentUser;

    @Override
    public void onStart(){
        super.onStart();
//        check if the user is signed in
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
//            open the main activity
            Intent intent  = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();

        }

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        login = findViewById(R.id.appCompatLoginButton);
        emailText = findViewById(R.id.loginEmailView);
        passwordText = findViewById(R.id.loginPasswordView);
        progressBar = findViewById(R.id.progress_circular_login);
        click_to_register = findViewById(R.id.click_to_register);
//        handle the register text
        click_to_register.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
            startActivity(intent);
            finish();
        });
//        Handle the login button on click
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                progressBar.setVisibility(view.VISIBLE);
                String email, password, confpassword;
                email = String.valueOf(emailText.getText());
                password = String.valueOf(passwordText.getText());
                confpassword = String.valueOf(passwordText.getText());

                if (TextUtils.isEmpty(email)){
                    Toast.makeText(LoginActivity.this, "Enter Email", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(password)){
                    Toast.makeText(LoginActivity.this, "Enter Password", Toast.LENGTH_SHORT).show();
                }


                mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressBar.setVisibility(view.GONE);
//                        check if sign in is succesfull
                        if(task.isSuccessful()){
                            Toast.makeText(LoginActivity.this, "Sign in successful", Toast.LENGTH_SHORT).show();
                            Intent intent  = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(intent);
                            finish();
                        }else{
                            Toast.makeText(LoginActivity.this, "Authentication Failed", Toast.LENGTH_SHORT).show();
                        }

                    }
                });

            }
        });

    }
}