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
import com.google.firebase.Firebase;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends AppCompatActivity {
    AppCompatButton signup;
    TextInputEditText emailText, passwordText , conf_passwordText;
    TextView loginText;
    FirebaseAuth mAuth;
    ProgressBar progressBar;

    //  check if the user is signed in
    @Override
    public void onStart(){
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
//            open the main activity
            Intent intent  = new Intent(getApplicationContext(), UserDetails .class);
            startActivity(intent);
            finish();

        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        emailText = findViewById(R.id.userEmailView);
        passwordText = findViewById(R.id.userPasswordView);
//        conf_passwordText = findViewById(R.id.userConfPasswordView);
        signup = findViewById(R.id.appCompatRegisterButton);
        progressBar = findViewById(R.id.progress_circular_reg);
        loginText = findViewById(R.id.click_to_login);

        loginText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });





//        when the sign up buton is clicked

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                progressBar.setVisibility(view.VISIBLE);
                String email, password, confpassword;
                email = String.valueOf(emailText.getText());
                password = String.valueOf(passwordText.getText());
                confpassword = String.valueOf(passwordText.getText());

                if (TextUtils.isEmpty(email)){
                    Toast.makeText(RegisterActivity.this, "Enter Email", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(password)){
                    Toast.makeText(RegisterActivity.this, "Enter Password", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(confpassword)){
                    Toast.makeText(RegisterActivity.this, "Enter conformation password", Toast.LENGTH_SHORT).show();
                    return;
                }
                
//                check if  password match
                if(!password.equals(confpassword)){
                    Toast.makeText(RegisterActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                }

//                create user
                mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener( new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressBar.setVisibility(view.GONE);
                        if(task.isSuccessful()){
//                            FirebaseUser user = mAuth.getCurrentUser();

                            Toast.makeText(RegisterActivity.this, "Authentication successful", Toast.LENGTH_SHORT).show();
                            Intent intent  = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(intent);
                            finish();
                        }else{
                            Toast.makeText(RegisterActivity.this, "Authentication Failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                
            }
        });
    }
}