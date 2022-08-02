package com.example.licenta_tinder_caini;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.licenta_tinder_caini.Matches.MatchesActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {

    private EditText mNameField, mDescriptionField, mDogNameField, mDogAgeField,mDogBreedField,mCityField;

    private Button  mConfirm, mLogout;

    private ImageView mProfileImage;

    //variable to sava the profile image URL to the database
    private FirebaseAuth mAuth;
    private DatabaseReference mUserDatabase;

    private String userId, name,dogname,dog_age,dog_breed,city,profileImageUrl, description,userSex;

    private Uri resultUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mNameField = (EditText) findViewById(R.id.Profile_user_name);
        mDescriptionField = (EditText) findViewById(R.id.Profile_Description);
        mDogAgeField=(EditText) findViewById(R.id.Profile_dog_age);
        mDogBreedField=(EditText) findViewById(R.id.Profile_dog_Breed);
        mCityField=(EditText) findViewById(R.id.Profile_etcity);
        mDogNameField=(EditText)findViewById(R.id.dogName);

        mConfirm = (Button) findViewById(R.id.Profile_Confirm);
        mLogout = (Button) findViewById(R.id.Profile_Logout);

        mProfileImage = (ImageView) findViewById(R.id.profileImage);

        mAuth = FirebaseAuth.getInstance();
        userId = mAuth.getCurrentUser().getUid();

        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);

        //displaying user information
        getUserInfo();

        // intent for profile image
        mProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, 1);
            }
        });

        //start saving information to the database
        mConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveUserInformation();
            }
        });

        //logout
        mLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                finish();
                startActivity(new Intent(ProfileActivity.this,ChooseLoginRegistrationActivity.class));

            }
        });

    }

    private void getUserInfo() {
        mUserDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                    if(map.get("name")!=null){
                        name = map.get("name").toString();
                        mNameField.setText(name);
                    }
                    if (map.get("dogname") != null) {
                        dogname = map.get("dogname").toString();
                        mDogNameField.setText(dogname);
                    }
                    if (map.get("dog_age") != null) {
                        dog_age = map.get("dog_age").toString();
                        mDogAgeField.setText(dog_age);
                    }
                    if (map.get("dog_breed") != null) {
                        dog_breed = map.get("dog_breed").toString();
                        mDogBreedField.setText(dog_breed);
                    }
                    if (map.get("city") != null) {
                        city = map.get("city").toString();
                        mCityField.setText(city);
                    }

                    if (map.get("description") != null) {
                        description = map.get("description").toString();
                        mDescriptionField.setText(description);
                    }
                    if (map.get("sex") != null) {
                        userSex = map.get("sex").toString();
                    }

                    Glide.clear(mProfileImage);
                    if(map.get("profileImageUrl")!=null){
                        profileImageUrl = map.get("profileImageUrl").toString();
                        switch(profileImageUrl) {
                            case "default":
                                Glide.with(getApplication()).load(R.drawable.cocker).into(mProfileImage);
                                break;
                            default:
                                Glide.with(getApplication()).load(profileImageUrl).into(mProfileImage);
                                break;
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    //saving user information
    private void saveUserInformation() {
        name = mNameField.getText().toString();
        dogname = mDogNameField.getText().toString();
        dog_age = mDogAgeField.getText().toString();
        dog_breed = mDogBreedField.getText().toString();
        city = mCityField.getText().toString();
        description = mDescriptionField.getText().toString();
        profileImageUrl = mProfileImage.toString();

        Map userInfo = new HashMap();
        userInfo.put("name", name);
        userInfo.put("dogname", dogname);
        userInfo.put("profileImageUrl",profileImageUrl);
        userInfo.put("description", description);
        userInfo.put("dog_age", dog_age);
        userInfo.put("dog_breed", dog_breed);
        userInfo.put("city", city);


        mUserDatabase.updateChildren(userInfo);

        //Uploading Profile Image To Firebase
        if (resultUri != null) {
            StorageReference filepath = FirebaseStorage.getInstance().getReference().child("profileImages").child(userId);
            Bitmap bitmap = null;

            try {
                bitmap = MediaStore.Images.Media.getBitmap(getApplication().getContentResolver(), resultUri);
            } catch (IOException e) {
                e.printStackTrace();
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 20, baos);
            byte[] data = baos.toByteArray();
            UploadTask uploadTask = filepath.putBytes(data);

            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    finish();
                }
            });

            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Map newImage = new HashMap();
                            newImage.put("profileImageUrl", uri.toString());
                            mUserDatabase.updateChildren(newImage);

                            finish();
                            return;
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            finish();
                            return;
                        }
                    });
                }
            });
        }else{
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            final Uri imageUri = data.getData();
            resultUri = imageUri;
            mProfileImage.setImageURI(resultUri);
        }
    }

    public void goToChat(View view) {
        Intent intent = new Intent(ProfileActivity.this, MatchesActivity.class);
        startActivity(intent);
        return;
    }

    public void goToProfile(View view) {
        Intent intent = new Intent(ProfileActivity.this, ProfileActivity.class);
        startActivity(intent);
        return;
    }

    public void goToMatches(View view) {
        Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
        startActivity(intent);
        return;
    }
}
