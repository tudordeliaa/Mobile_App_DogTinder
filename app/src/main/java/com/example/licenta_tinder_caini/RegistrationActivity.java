package com.example.licenta_tinder_caini;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.widget.EditText;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class RegistrationActivity extends AppCompatActivity {
    //Minimum six characters, at least one letter, one number and one special character:
    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^" +
                    "(?=.*[0-9])" +         //at least 1 digit
                    "(?=.*[a-z])" +         //at least 1 lower case letter
                    "(?=.*[A-Z])" +         //at least 1 upper case letter
                    "(?=.*[a-zA-Z])" +      //any letter
                    "(?=.*[@#$%^&+=])" +    //at least 1 special character
                    "(?=\\S+$)" +           //no white spaces
                    ".{6,}" +               //at least 4 characters
                    "$");

    private Button mRegister;
    private EditText mEmail, mPassword, mName,mDogName, mCity;
    private ImageButton mBack;

    CardView card1,card2,card3,card4;

    private ProgressBar mprogressBar;

    private RadioGroup mRadioGroup;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthStateListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        mAuth = FirebaseAuth.getInstance();
        firebaseAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user !=null){
                    Intent intent = new Intent(RegistrationActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                    return;
                }
            }
        };


        mRegister = (Button) findViewById(R.id.btnregister3);
        mBack =(ImageButton)findViewById(R.id.backtochoose2);

        mEmail = (EditText) findViewById(R.id.etemail);
        mPassword = (EditText) findViewById(R.id.etpassword);
        mName = (EditText) findViewById(R.id.etname);
        mDogName=(EditText)findViewById(R.id.dogName);
        mCity =(EditText)findViewById(R.id.etCity);

        mprogressBar=(ProgressBar)findViewById(R.id.btnpreogressBar);

        card1 = (CardView)findViewById(R.id.card1);
        card2 = (CardView)findViewById(R.id.card2);
        card3 = (CardView)findViewById(R.id.card3);
        card4 = (CardView)findViewById(R.id.card4);

        mRadioGroup = (RadioGroup) findViewById(R.id.radioGroup);

        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegistrationActivity.this, ChooseLoginRegistrationActivity.class);
                startActivity(intent);
                finish();
                return;
            }
        });


        mRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mprogressBar.setVisibility(View.VISIBLE);
                int selectId = mRadioGroup.getCheckedRadioButtonId();

                final RadioButton radioButton = (RadioButton) findViewById(selectId);

                if(radioButton.getText() == null){
                    return;
                }

                final String email = mEmail.getText().toString();
                //
                final String password = mPassword.getText().toString().trim();
                if (password.isEmpty()) {
                    mPassword.setError("Field can't be empty");
                } else if (!PASSWORD_PATTERN.matcher(password).matches()) {
                    mPassword.setError("Password too weak");
                } else {
                    mPassword.setError(null);
                }
                //
                final String name = mName.getText().toString();
                final String dogname =mDogName.getText().toString();
                final String city = mCity.getText().toString();


                mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(RegistrationActivity.this,
                        new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        mprogressBar.setVisibility(View.GONE);
                        if(!task.isSuccessful()){
                            Toast.makeText(RegistrationActivity.this, "Sign up error", Toast.LENGTH_SHORT).show();
                        }else{
                            //salvez datele in baza de date
                            String userId = mAuth.getCurrentUser().getUid();
                            DatabaseReference currentUserDb = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);
                            Map userInfo = new HashMap<>();
                            userInfo.put("name", name);
                            userInfo.put("city", city);
                            userInfo.put("dogname",dogname);
                            userInfo.put("sex", radioButton.getText().toString());
                            userInfo.put("profileImageUrl", "default");
                            currentUserDb.updateChildren(userInfo);
                            Toast.makeText(RegistrationActivity.this, "Registration was successful", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }




    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(firebaseAuthStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(firebaseAuthStateListener);
    }
}
