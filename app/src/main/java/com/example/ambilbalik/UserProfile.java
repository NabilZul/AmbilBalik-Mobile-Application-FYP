package com.example.ambilbalik;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;

public class UserProfile extends AppCompatActivity {
    BottomNavigationView bottom_navigator;
    EditText userName,userPhone,userIc,userAddress,userEmail,userPassword;
    TextView cancelUpdate,edit, text1;
    ImageView userImage;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String uid = user.getUid();
    boolean clicked = false;
    private Uri mImageUri;
    String currentPhotoPath;
    private static final int  CAMERA_REQUEST_CODE = 111;

    private StorageReference storage = FirebaseStorage.getInstance().getReference("Users/");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        userName = findViewById(R.id.userName);
        userPhone = findViewById(R.id.userPhone);
        userIc = findViewById(R.id.userIc);
        userAddress = findViewById(R.id.userAddress);
        userEmail = findViewById(R.id.userEmail);
        userPassword = findViewById(R.id.userPassword);
        cancelUpdate = findViewById(R.id.cancelUpdate);
        edit = findViewById(R.id.edit);
        text1 = findViewById(R.id.text1);

        userImage = findViewById(R.id.userImage);
        mAuth=FirebaseAuth.getInstance();

        bottom_navigator = findViewById(R.id.bottom_navigator);
        bottom_navigator.setSelectedItemId(R.id.profile);
        bottomNav();
        getUser();
    }

    public void getUser(){
        DocumentReference documentReference = db.collection("users").document("User " +uid);
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()){
                        userName.setText(document.getData().get("userName").toString());
                        userPhone.setText(document.getData().get("phoneNumber").toString());
                        userIc.setText(document.getData().get("icNumber").toString());
                        userAddress.setText(document.getData().get("userAddress").toString());
                        userEmail.setText(document.getData().get("userEmail").toString());
                        userPassword.setText(document.getData().get("userPassword").toString());
                        String link = document.getData().get("imagesUrl").toString();
                        Picasso.get().load(link).into(userImage);
                    }else {
                        Toast.makeText(UserProfile.this, "pisyang", Toast.LENGTH_SHORT).show();
                        System.out.println(uid);
                    }

                }
                else{
                    Toast.makeText(UserProfile.this, "lagi Pisyang", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void bottomNav(){
        bottom_navigator.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.home:
                        Intent intent = new Intent(UserProfile.this,UserDashboard.class);
                        startActivity(intent);
                        break;

                    case R.id.search:
                        Intent intent1 = new Intent(UserProfile.this,UserSearch.class);
                        startActivity(intent1);
                        break;

                    case R.id.profile:
                        Intent intent2 = new Intent(UserProfile.this,UserProfile.class);
                        startActivity(intent2);
                        break;
                }
                return false;
            }
        });
    }



    public void updateUserProfile(){

        if (mImageUri != null) {
            StorageReference fileReference = storage.child(uid + "." + getFileExtension(mImageUri));

            fileReference.putFile(mImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            taskSnapshot.getStorage().getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    DocumentReference documentReference = db.collection("users").document("User " +uid);
                                    documentReference
                                            .update(
                                                    "userName",userName.getText().toString(),
                                                    "phoneNumber",userPhone.getText().toString(),
                                                    "userPassword",userPassword.getText().toString(),
                                                    "userAddress",userAddress.getText().toString(),
                                                    "icNumber",userIc.getText().toString(),
                                                    "imagesUrl",task.getResult().toString()
                                            );

//                                    user.setImageURL(task.getResult().toString());
                                }
                            });
                        }
                    });
            Toast.makeText(UserProfile.this, "Profile successfully updated", Toast.LENGTH_SHORT).show();

        } else {
            DocumentReference documentReference = db.collection("users").document("User " +uid);
            documentReference
                    .update(
                            "userName",userName.getText().toString(),
                            "phoneNumber",userPhone.getText().toString(),
                            "userPassword",userPassword.getText().toString(),
                            "userAddress",userAddress.getText().toString(),
                            "icNumber",userIc.getText().toString()

                    );
            Toast.makeText(UserProfile.this, "Profile successfully updated", Toast.LENGTH_SHORT).show();

        }
    }

    public void editProfile(View view) {
String name = userName.getText().toString();
String phone = userPhone.getText().toString();
String password = userPassword.getText().toString();
String address = userAddress.getText().toString();
String ic = userIc.getText().toString();
        if (clicked) {
            if (name.isEmpty() || phone.isEmpty() || password.isEmpty() || address.isEmpty() || ic.isEmpty()) {
                Toast.makeText(UserProfile.this, "Please Dont Leave Blank Space", Toast.LENGTH_SHORT).show();
            } else {

                updateUserProfile();
                userName.setEnabled(false);
                userPhone.setEnabled(false);
                userPassword.setEnabled(false);
                userPassword.setVisibility(View.INVISIBLE);
                text1.setVisibility(View.INVISIBLE);
                userAddress.setEnabled(false);
                //userEmail.setEnabled(false);
                userIc.setEnabled(false);
                edit.setText("Edit Profile");
                cancelUpdate.setVisibility(View.INVISIBLE);
                clicked = false;
            }
        }
        else{
                userName.setEnabled(true);
                userPhone.setEnabled(true);
                userPassword.setEnabled(true);
                userPassword.setVisibility(View.VISIBLE);
                text1.setVisibility(View.VISIBLE);
                userAddress.setEnabled(true);
                //userEmail.setEnabled(true);
                userIc.setEnabled(true);
                edit.setText("Save");
                cancelUpdate.setVisibility(View.VISIBLE);
                clicked = true;
            }

    }

    public void canselEdit(View view) {
        userName.setEnabled(false);
        userPhone.setEnabled(false);
        userPassword.setEnabled(false);
        userPassword.setVisibility(View.INVISIBLE);
        text1.setVisibility(View.INVISIBLE);
        userAddress.setEnabled(false);
        //userEmail.setEnabled(false);
        userIc.setEnabled(false);
        edit.setText("Edit Profile");
        cancelUpdate.setVisibility(View.INVISIBLE);
        clicked = false;
        getUser();
    }



    private void openFileChooser(){
        AlertDialog.Builder dialog = new AlertDialog.Builder(UserProfile.this);
        dialog.setCancelable(false);
        dialog.setPositiveButton("Take Photo", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                if (ContextCompat.checkSelfPermission(UserProfile.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
                    //Take Permission
                    ActivityCompat.requestPermissions(UserProfile.this, new String[]{Manifest.permission.CAMERA},  CAMERA_REQUEST_CODE);

                } else {
                    Toast.makeText(UserProfile.this, "Permission already Granted", Toast.LENGTH_SHORT).show();
                    dispatchTakePictureIntent();
                }
            }
        })
                .setNegativeButton("Gallery", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent photoPickerIntent = new Intent();
                        photoPickerIntent.setType("image/*");
                        photoPickerIntent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(photoPickerIntent, 1);
                    }
                });
        final AlertDialog alert = dialog.create();
        alert.show();
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case  CAMERA_REQUEST_CODE:
                if(resultCode == RESULT_OK){
                    File f = new File(currentPhotoPath);
                    userImage.setImageURI(Uri.fromFile(f));
                    mImageUri = Uri.fromFile(f);
                }
                break;
            case 1:
                if(resultCode == RESULT_OK){
                    mImageUri = data.getData();
                    Picasso.get().load(mImageUri).into(userImage);
                }
                break;
        }
    }

    private String getFileExtension(Uri mImageUri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(mImageUri));
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String imageFileName = userName + ".";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                "imageFileName",  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                Log.e("TAG", ex.getMessage() );

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE);
            }
        }
    }

   public void editPicture(View view) {
        if (clicked){
            openFileChooser();
        }
        }

    public void signOut() {
        // [START auth_sign_out]
        FirebaseAuth.getInstance().signOut();
        // [END auth_sign_out]
    }

    public void logout(View view) {
        signOut();
        Intent intent = new Intent(UserProfile.this, Login.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}
