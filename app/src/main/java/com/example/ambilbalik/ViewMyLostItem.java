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
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class ViewMyLostItem extends AppCompatActivity {

    Lost lost = new Lost();
    LinearLayout editQuery;
    ImageView itemPicture, openCalendar;
    EditText itemName, itemDescription;
    TextView editInformation, ownerName, eventDate;
    String lostID;

    private Uri mImageUri;
    String currentPhotoPath;
    private static final int CAMERA_REQUEST_CODE = 111;

    private StorageReference storage = FirebaseStorage.getInstance().getReference("Items/");

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    private FirebaseAuth mAuth;
    boolean clicked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_my_lost_item);
        mAuth = FirebaseAuth.getInstance();

        lost = (Lost) getIntent().getSerializableExtra("Hold Lost");

        editQuery = findViewById(R.id.editQuery);
        itemPicture = findViewById(R.id.itemPicture);
        itemName = findViewById(R.id.itemName);
        ownerName = findViewById(R.id.ownerName);
        itemDescription = findViewById(R.id.itemDescription);
        editInformation = findViewById(R.id.editInformation);
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
            }

        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

        openCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startTime.show();
            }
        });

        displayLostItem(lost);
        holdID();

    }

    public void holdID() {
        db.collection("lost").whereEqualTo("lostUid", lost.getLostUid()).get().
                addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                lostID = document.getId();
                            }
                        }
                    }
                });
    }

    public void displayLostItem(Lost lost) {
        itemName.setText(lost.getItemName());
        itemDescription.setText(lost.getItemDescription());
        ownerName.setText(lost.getOwnerName());
        eventDate.setText(lost.getEventDate());
        Picasso.get().load(lost.getImaggeUrl()).into(itemPicture);
    }

    public void toMyLostItem(View view) {
        Intent intent = new Intent(ViewMyLostItem.this, MyLostItem.class);
        startActivity(intent);
    }

    public void deleteLostItem(View view) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(ViewMyLostItem.this);
        dialog.setCancelable(false);
        dialog.setTitle("Delete Confirmation");
        dialog.setMessage("Are you sure you want to delete this item?" );
        dialog.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                //execute Delete
                db.collection("lost").document(lostID).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(ViewMyLostItem.this, "Item been deleted", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(ViewMyLostItem.this, MyLostItem.class);
                        startActivity(intent);
                    }
                });

            }
        })
                .setNegativeButton("Cancel ", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Action for "Cancel".

                    }
                });

        final AlertDialog alert = dialog.create();
        alert.show();

        alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(android.R.color.holo_red_light));
        alert.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.black));
    }



    public void editContent(View view) {

        editQuery.setVisibility(View.VISIBLE);
        itemName.setEnabled(true);
        itemDescription.setEnabled(true);
        openCalendar.setVisibility(View.VISIBLE);
        eventDate.setEnabled(true);

        clicked = true;
    }

    public void canselEdit(View view) {
        editQuery.setVisibility(View.INVISIBLE);
        itemName.setEnabled(false);
        itemDescription.setEnabled(false);
        openCalendar.setVisibility(View.INVISIBLE);
        eventDate.setEnabled(false);
        clicked = false;
        displayLostItem(lost);
    }

    public void saveEdit(View view) {
String name = itemName.getText().toString();
String desc = itemDescription.getText().toString();
if (name.isEmpty()||desc.isEmpty()){
    Toast.makeText(ViewMyLostItem.this,"Please Dont Leave Any Blank Space",Toast.LENGTH_SHORT).show();
}else {
    updateItemInfo();
    editQuery.setVisibility(View.INVISIBLE);
    itemName.setEnabled(false);
    itemDescription.setEnabled(false);
    openCalendar.setVisibility(View.INVISIBLE);
    eventDate.setEnabled(false);
    clicked = false;
}
    }

    public void updateItemInfo() {


        if (mImageUri != null) {
            StorageReference fileReference = storage.child(itemName + "." + getFileExtension(mImageUri));

            fileReference.putFile(mImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            taskSnapshot.getStorage().getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    DocumentReference documentReference = db.collection("lost").document(lostID);
                                    documentReference
                                            .update(
                                                    "itemName", itemName.getText().toString(),
                                                    "itemDescription", itemDescription.getText().toString(),
                                                    "eventDate", eventDate.getText().toString(),
                                                    "imaggeUrl", task.getResult().toString()
                                            );

//                                    user.setImageURL(task.getResult().toString());
                                }
                            });
                        }
                    });
            Toast.makeText(ViewMyLostItem.this, "Profile successfully updated", Toast.LENGTH_SHORT).show();

        } else {
            DocumentReference documentReference = db.collection("lost").document(lostID);
            documentReference
                    .update(
                            "itemName", itemName.getText().toString(),
                            "itemDescription", itemDescription.getText().toString(),
                            "eventDate", eventDate.getText().toString()

                    );
            Toast.makeText(ViewMyLostItem.this, "Profile successfully updated", Toast.LENGTH_SHORT).show();

        }
    }

    private void openFileChooser() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(ViewMyLostItem.this);
        dialog.setCancelable(false);
        dialog.setPositiveButton("Take Photo", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                if (ContextCompat.checkSelfPermission(ViewMyLostItem.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
                    //Take Permission
                    ActivityCompat.requestPermissions(ViewMyLostItem.this, new String[]{Manifest.permission.CAMERA}, CAMERA_REQUEST_CODE);

                } else {
                    Toast.makeText(ViewMyLostItem.this, "Permission already Granted", Toast.LENGTH_SHORT).show();
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
        switch (requestCode) {
            case CAMERA_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    File f = new File(currentPhotoPath);
                    itemPicture.setImageURI(Uri.fromFile(f));
                    mImageUri = Uri.fromFile(f);
                }
                break;
            case 1:
                if (resultCode == RESULT_OK) {
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
        String imageFileName = itemName + ".";
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
                Log.e("TAG", ex.getMessage());

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

    public void editLostPicture(View view) {
        if (clicked) {
            openFileChooser();
        }
    }
}