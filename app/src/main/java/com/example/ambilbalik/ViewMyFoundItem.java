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
import android.os.PersistableBundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
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
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class ViewMyFoundItem extends AppCompatActivity implements OnMapReadyCallback {

    Found found = new Found();
    LinearLayout editQuery;
    ImageView itemPicture,openCalendar;
    EditText itemName,itemDescription,eventDate;
    TextView editInformation, founderName;
    MapView mapView;
    String foundID, newString;
    MarkerOptions markerOptions;
    GoogleMap m;

    private Uri mImageUri;
    String currentPhotoPath;
    private static final int CAMERA_REQUEST_CODE = 111;

    private StorageReference storage = FirebaseStorage.getInstance().getReference("Items/");

    FirebaseFirestore db= FirebaseFirestore.getInstance();
    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    private FirebaseAuth mAuth;
    boolean clicked = false;
    boolean isPermissionGranted, edit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_my_found_item);

        found = (Found) getIntent().getSerializableExtra("Hold Found");

        editQuery = findViewById(R.id.editQuery);
        itemPicture = findViewById(R.id.itemPicture);
        itemName = findViewById(R.id.itemName);
        itemDescription = findViewById(R.id.itemDescription);
        editInformation = findViewById(R.id.editInformation);
        founderName = findViewById(R.id.founderName);
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
//                reqDate = eventDate.getText().toString();

            }

        }, newCalendar.get(Calendar.YEAR ), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

        openCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startTime.show();
            }
        });

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                newString= null;
            } else {
                newString= extras.getString("STRING_I_NEED");
            }
        } else {
            newString= (String) savedInstanceState.getSerializable("STRING_I_NEED");
        }

        mapView = findViewById(R.id.mapView);

        checkMyPermission();

        if (isPermissionGranted){
            mapView.getMapAsync(this);
            mapView.onCreate(savedInstanceState);
        }

        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        String myDate = format1.format(date);
        // System.out.println("Current Date Time : " + myDate);
        Log.e("text", myDate);
//        calendar.set(year, monthOfYear, dayOfMonth);

//        currentDate = format1.format(calendar.getTime());

//        currentDate = DateFormat.getDateInstance().format(calendar.getTime());

//       currentDate=  DateFormat.getDateInstance().format(calendar.getTime());
//        Log.e("adadadad", calendar.getTime().toString());
//        Log.e("adadadad", currentDate);
//        bottomNav();

        //holdID();
        if (myDate.equals(found.getAfter1Year())){
            DocumentReference reference = db.collection("found").document("Found " + found.getFoundUid());
            reference.update("itemStatus","B");
            Log.e("Here",myDate);
        }

        displayFoundItem(found);
        holdID();
    }

    public void displayFoundItem(Found found){
        itemName.setText(found.getItemName());
        itemDescription.setText(found.getItemDescription());
        founderName.setText(found.getFounderName());
        eventDate.setText(found.getEventDate());
        Picasso.get().load(found.getImageUrl()).into(itemPicture);

    }

    public void holdID(){
        db.collection("found").whereEqualTo("foundUid", found.getFoundUid()).get().
                addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            for (QueryDocumentSnapshot document : task.getResult()){
                                foundID = document.getId();
                            }
                        }
                    }
                });
    }

    public void toMyFoundItem(View view) {
        Intent intent = new Intent(ViewMyFoundItem.this, MyFoundItem.class);
        startActivity(intent);
    }

    public void deleteFoundItem(View view) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(ViewMyFoundItem.this);
        dialog.setCancelable(false);
        dialog.setTitle("Delete Confirmation");
        dialog.setMessage("Are you sure you want to delete this item?" );
        dialog.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                //execute Delete

                db.collection("found").document(foundID).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(ViewMyFoundItem.this, "Item been deleted", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(ViewMyFoundItem.this, MyFoundItem.class);
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
        //m.getUiSettings().setAllGesturesEnabled(true);
        editQuery.setVisibility(View.VISIBLE);
        itemName.setEnabled(true);
        itemDescription.setEnabled(true);
        eventDate.setEnabled(true);
        openCalendar.setVisibility(View.VISIBLE);
        edit =! edit;
        clicked=true;
        m.getUiSettings().setAllGesturesEnabled(true);
        if (edit){
            m.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng latLng) {
                    //create marker
                    markerOptions = new MarkerOptions();
                    //set Marker Position
                    markerOptions.position(latLng);
                    //set market lat and long
                    markerOptions.title(latLng.latitude + " : " + latLng.longitude);

                    //clear previously clicked position
                    m.clear();
                    //zoom marker
                    m.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));
                    //add marker on map
                    m.addMarker(markerOptions);
                }
            });
        }else
        {
        m.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {

            }
        });
        }
    }

    public void canselEdit(View view) {
        editQuery.setVisibility(View.INVISIBLE);
        itemName.setEnabled(false);
        itemDescription.setEnabled(false);
        eventDate.setEnabled(false);
        openCalendar.setVisibility(View.INVISIBLE);
        onMapReady(m);
        m.clear();
        onMapReady(m);
        displayFoundItem(found);
        edit=false;
        clicked=false;
        m.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {

            }
        });
    }

    public void saveEdit(View view) {



String name = itemName.getText().toString();
String desc = itemDescription.getText().toString();
if (name.isEmpty()||desc.isEmpty()){
    Toast.makeText(ViewMyFoundItem.this,"Please Dont Leave Any Blank Space",Toast.LENGTH_SHORT).show();
}else {

    updateItemInfo();
    editQuery.setVisibility(View.INVISIBLE);
    editQuery.setEnabled(false);
    itemName.setEnabled(false);
    itemDescription.setEnabled(false);
    eventDate.setEnabled(false);
    openCalendar.setVisibility(View.INVISIBLE);
    //m.clear();
    //onMapReady(m);
    m.getUiSettings().setAllGesturesEnabled(false);
    edit = false;
    clicked = false;
    m.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
        @Override
        public void onMapClick(LatLng latLng) {

        }
    });
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
                                    DocumentReference documentReference = db.collection("found").document(foundID);
                                    documentReference
                                            .update(
                                                    "itemName", itemName.getText().toString(),
                                                    "itemDescription", itemDescription.getText().toString(),
                                                    "eventDate", eventDate.getText().toString(),
                                                    "imageUrl", task.getResult().toString(),
                                                    "itemLatitude",String.valueOf(markerOptions.getPosition().latitude),
                                                    "itemLongitude",String.valueOf(markerOptions.getPosition().longitude)

                                            );

//                                    user.setImageURL(task.getResult().toString());
                                }
                            });
                        }
                    });
            Toast.makeText(ViewMyFoundItem.this, "Profile successfully updated", Toast.LENGTH_SHORT).show();

        } else {
            DocumentReference documentReference = db.collection("found").document(foundID);
            documentReference
                    .update(
                            "itemName", itemName.getText().toString(),
                            "itemDescription", itemDescription.getText().toString(),
                            "eventDate", eventDate.getText().toString(),
                            "itemLatitude",String.valueOf(markerOptions.getPosition().latitude),
                            "itemLongitude",String.valueOf(markerOptions.getPosition().longitude)
                    );
            Toast.makeText(ViewMyFoundItem.this, "Profile successfully updated", Toast.LENGTH_SHORT).show();

        }
    }

    private void checkMyPermission(){
        Dexter.withContext(this).withPermission(Manifest.permission.ACCESS_FINE_LOCATION).withListener(new PermissionListener() {
            @Override
            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                Toast.makeText(ViewMyFoundItem.this, "Permission Granted", Toast.LENGTH_SHORT).show();
                isPermissionGranted=true;
            }

            @Override
            public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package",getPackageName(),"");
                intent.setData(uri);
                startActivity(intent);
            }

            @Override
            public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                permissionToken.continuePermissionRequest();
            }
        }).check();
    }

    @Override
    public void onMapReady(GoogleMap map) {
        m= map;
//        map.getUiSettings().setTiltGesturesEnabled(false);
//        map.getUiSettings().setScrollGesturesEnabled(false);
//        map.getUiSettings().setZoomGesturesEnabled(false);
//        map.getUiSettings().setZoomControlsEnabled(false);
//        map.getUiSettings().setIndoorLevelPickerEnabled(false);
        map.getUiSettings().setAllGesturesEnabled(false);
//        map.getUiSettings().setIndoorLevelPickerEnabled(false);
//        map.getUiSettings().setCompassEnabled(false);
//        map.getUiSettings().setMapToolbarEnabled(false);
//        map.getUiSettings().setScrollGesturesEnabled(false);
//        map.getUiSettings().setScrollGesturesEnabledDuringRotateOrZoom(false);

        LatLng utemLocation = new LatLng(Double.parseDouble(found.getItemLatitude()),Double.parseDouble(found.getItemLongitude()));
        LatLngBounds adelaideBounds = new LatLngBounds(
                new LatLng(2.304404, 102.313474), // SW bounds
                new LatLng(2.319718, 102.327781)  // NE bounds
        );
// Constrain the camera target to the utem.
        map.setLatLngBoundsForCameraTarget(adelaideBounds);
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(utemLocation)      // Sets the center of the map to utem
                .zoom(16)                   // Sets the zoom
                .build();                   // Creates a CameraPosition from the builder
        map.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        map.addMarker(new MarkerOptions().position(utemLocation).title("Item Found Here"));
        map.moveCamera(CameraUpdateFactory.newLatLng(utemLocation));





    }

    @Override
    protected  void onStart(){
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onResume(){
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState, @NonNull PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    private void openFileChooser() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(ViewMyFoundItem.this);
        dialog.setCancelable(false);
        dialog.setPositiveButton("Take Photo", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                if (ContextCompat.checkSelfPermission(ViewMyFoundItem.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
                    //Take Permission
                    ActivityCompat.requestPermissions(ViewMyFoundItem.this, new String[]{Manifest.permission.CAMERA}, CAMERA_REQUEST_CODE);

                } else {
                    Toast.makeText(ViewMyFoundItem.this, "Permission already Granted", Toast.LENGTH_SHORT).show();
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

    public void editFoundPicture(View view) {
        if (clicked) {
            openFileChooser();
        }
    }

}