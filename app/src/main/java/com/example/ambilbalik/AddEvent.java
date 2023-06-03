package com.example.ambilbalik;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AddEvent extends AppCompatActivity {

    EditText EdtItemName,EdtItemDescription,eventDate;
    Spinner eventSpinner;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    ArrayList<String> eventList = new ArrayList<>();
    User thisUser;
    Lost lost = new Lost();
    Found found = new Found();
    String userName,userPhone;
    ImageView itemPicture,openCalendar;
    private StorageReference storage = FirebaseStorage.getInstance().getReference("Items/");
    private static final int  CAMERA_REQUEST_CODE = 111;
    private Uri mImageUri;
    String currentPhotoPath, reqDate, endDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

        //thisUser = (User)getIntent().getSerializableExtra("Retriever");

        EdtItemName = findViewById(R.id.itemName);
        EdtItemDescription = findViewById(R.id.itemDescription);
        eventSpinner = findViewById(R.id.eventSpinner);
        itemPicture = findViewById(R.id.itemPicture);
        eventDate = findViewById(R.id.eventDate);
        openCalendar = findViewById(R.id.openCalendar);

        final Calendar newCalendar = Calendar.getInstance();
        final DatePickerDialog startTime = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
                Log.e("adadadad", newDate.getTime().toString());
                eventDate.setText(format1.format(newDate.getTime()));
                reqDate = eventDate.getText().toString();
                Calendar oneYear = Calendar.getInstance();
                oneYear.set(year +1 , monthOfYear, dayOfMonth);
                endDate = format1.format(oneYear.getTime());

            }

        }, newCalendar.get(Calendar.YEAR ), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

        openCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startTime.show();
            }
        });

        readEvent();
    }

    public void goBackDashboard(View view){
        Intent intent = new Intent(AddEvent.this,UserDashboard.class);
        startActivity(intent);
    }

    public void readEvent(){
        db.collection("event").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    for (QueryDocumentSnapshot document: task.getResult()){
                        eventList.add(document.getString("eventType"));
                    }
                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(AddEvent.this, android.R.layout.simple_spinner_item,eventList);
                    arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    eventSpinner.setAdapter(arrayAdapter);
                }
            }
        });
    }

    public void proceed(View view){

        String eventName = (String) eventSpinner.getSelectedItem().toString();
        String itemName = EdtItemName.getText().toString();
        String itemDescription = EdtItemDescription.getText().toString();
        String path = String.valueOf(System.nanoTime());
        String reqDate = eventDate.getText().toString();

if (itemName.isEmpty() || itemDescription.isEmpty() || reqDate.isEmpty()){
    Toast.makeText(AddEvent.this,"Please Fill all the requirement !",Toast.LENGTH_SHORT).show();
}
else {
    if (eventName.equals("Lost")) {
        if (mImageUri != null) {
//                Bitmap bitmap = null;
//                try {
//                     bitmap =  MediaStore.Images.Media.getBitmap(this.getContentResolver(), mImageUri);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                bitmap = shrinkBitmap(mImageUri.getPath(),150,150);
//                Uri uri = getImageUri(this, bitmap);
            StorageReference fileReference = storage.child(EdtItemName + "." + getFileExtension(mImageUri));
            fileReference.putFile(mImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            taskSnapshot.getStorage().getDownloadUrl()
                                    .addOnCompleteListener(new OnCompleteListener<Uri>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Uri> task) {
                                            String url = task.getResult().toString();
                                            FirebaseFirestore.getInstance().collection("users").document("User " + FirebaseAuth.getInstance().getCurrentUser().getUid()).get()
                                                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                        @Override
                                                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                            lost.setUserUid(user.getUid());
                                                            lost.setOwnerName(documentSnapshot.get("userName").toString());
                                                            lost.setOwnerPhone(documentSnapshot.get("phoneNumber").toString());
                                                            lost.setItemName(itemName);
                                                            lost.setItemDescription(itemDescription);
                                                            lost.setEventName(eventName);
                                                            lost.setLostUid(path);
                                                            lost.setImaggeUrl(url);
                                                            lost.setEventDate(reqDate);

                                                            db.collection("lost").document("Lost " + path)
                                                                    .set(lost)
                                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                        @Override
                                                                        public void onSuccess(Void aVoid) {
                                                                            Toast.makeText(AddEvent.this, "Lost Event Submitted", Toast.LENGTH_SHORT).show();
                                                                            Intent intent = new Intent(AddEvent.this, UserDashboard.class);
                                                                            startActivity(intent);

                                                                        }
                                                                    })
                                                                    .addOnFailureListener(new OnFailureListener() {
                                                                        @Override
                                                                        public void onFailure(@NonNull Exception e) {

                                                                        }
                                                                    });
                                                        }
                                                    });
                                        }
                                    });
                        }
                    });
        }
        else{
            Toast.makeText(AddEvent.this,"Please Put Item Picture",Toast.LENGTH_SHORT).show();
        }
    }
    if (eventName.equals("Found")) {
        if (mImageUri != null) {
//                Bitmap bitmap = null;
//                try {
//                    bitmap =  MediaStore.Images.Media.getBitmap(this.getContentResolver(), mImageUri);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                bitmap = shrinkBitmap(mImageUri.getPath(),150,150);
//                Uri uri = getImageUri(this, bitmap);
            StorageReference fileReference = storage.child(EdtItemName + "." + getFileExtension(mImageUri));
            fileReference.putFile(mImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            taskSnapshot.getStorage().getDownloadUrl()
                                    .addOnCompleteListener(new OnCompleteListener<Uri>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Uri> task) {
                                            String url = task.getResult().toString();
                                            FirebaseFirestore.getInstance().collection("users").document("User " + FirebaseAuth.getInstance().getCurrentUser().getUid()).get()
                                                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                        @Override
                                                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                            found.setUserUid(user.getUid());
                                                            found.setFounderName(documentSnapshot.get("userName").toString());
                                                            found.setFounderPhone(documentSnapshot.get("phoneNumber").toString());
                                                            found.setItemName(itemName);
                                                            found.setItemDescription(itemDescription);
                                                            found.setEventName(eventName);
                                                            found.setFoundUid(path);
                                                            found.setItemStatus("A");
                                                            found.setImageUrl(url);
                                                            found.setEventDate(reqDate);
                                                            found.setAfter1Year(endDate);


                                                            db.collection("found").document("Found " + path)
                                                                    .set(found)
                                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                        @Override
                                                                        public void onSuccess(Void aVoid) {
                                                                            Toast.makeText(AddEvent.this, "Input Item Location", Toast.LENGTH_SHORT).show();
                                                                            Intent intent = new Intent(AddEvent.this, InputLocation.class);
                                                                            String strName = null;
                                                                            intent.putExtra("STRING_I_NEED", path);
                                                                            startActivity(intent);

                                                                        }
                                                                    })
                                                                    .addOnFailureListener(new OnFailureListener() {
                                                                        @Override
                                                                        public void onFailure(@NonNull Exception e) {

                                                                        }
                                                                    });
                                                        }
                                                    });
                                        }
                                    });
                        }
                    });
        }else{
            Toast.makeText(AddEvent.this,"Please Put Item Picture",Toast.LENGTH_SHORT).show();
        }


    }
}

    }

    public void addImage(View view) {
        openFileChooser();
    }

    private void openFileChooser(){
        AlertDialog.Builder dialog = new AlertDialog.Builder(AddEvent.this);
        dialog.setCancelable(false);
        dialog.setPositiveButton("Take Photo", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                if (ContextCompat.checkSelfPermission(AddEvent.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
                    //Take Permission
                    ActivityCompat.requestPermissions(AddEvent.this, new String[]{Manifest.permission.CAMERA},  CAMERA_REQUEST_CODE);

                } else {
                    Toast.makeText(AddEvent.this, "Permission already Granted", Toast.LENGTH_SHORT).show();
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
                    itemPicture.setImageURI(Uri.fromFile(f));
                    mImageUri = Uri.fromFile(f);
                }
                break;
            case 1:
                if(resultCode == RESULT_OK){
                    mImageUri = data.getData();
                    Picasso.get().load(mImageUri).into(itemPicture);
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
        String imageFileName = EdtItemName + ".";
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

    //To shrink image size...

//    public Bitmap shrinkBitmap(String file, int width, int height)
//    {
//        BitmapFactory.Options bmpFactoryOptions = new BitmapFactory.Options();
//        bmpFactoryOptions.inJustDecodeBounds = true;
//        Bitmap bitmap = BitmapFactory.decodeFile(file, bmpFactoryOptions);
//
//        int heightRatio = (int) Math.ceil(bmpFactoryOptions.outHeight / (float) height);
//        int widthRatio = (int) Math.ceil(bmpFactoryOptions.outWidth / (float) width);
//
//        if(heightRatio > 1 || widthRatio > 1)
//        {
//            if(heightRatio > widthRatio)
//            {
//                bmpFactoryOptions.inSampleSize = heightRatio;
//            }
//            else
//            {
//                bmpFactoryOptions.inSampleSize = widthRatio;
//            }
//        }
//
//        bmpFactoryOptions.inJustDecodeBounds = false;
//        bitmap = BitmapFactory.decodeFile(file, bmpFactoryOptions);
//        return bitmap;
//    }
//
//    public Uri getImageUri(Context inContext, Bitmap inImage) {
//        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
//        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
//        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
//        return Uri.parse(path);
//    }
}