package com.example.licenta_tinder_caini;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity {

    private RelativeLayout relativeLayout;
    private EditText userEmail;
    private Button userPass;
    private ImageButton mBack;
    private ProgressBar progressBar;

    FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password_nou);

        relativeLayout = findViewById(R.id.relative_layout);
        userEmail = findViewById(R.id.etUser_email);
        userPass = findViewById(R.id.btnForgotPass);
        progressBar = findViewById(R.id.preogressBar);
        mBack =(ImageButton)findViewById(R.id.backtologin);

        firebaseAuth = FirebaseAuth.getInstance();

        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ForgotPasswordActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
                return;
            }
        });


        userPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               progressBar.setVisibility(View.VISIBLE);
                firebaseAuth.sendPasswordResetEmail(userEmail.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        progressBar.setVisibility(View.GONE);
                        if(task.isSuccessful()){
                            Toast.makeText(ForgotPasswordActivity.this, "The password reset request has been sent by email", Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(ForgotPasswordActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

    }
}