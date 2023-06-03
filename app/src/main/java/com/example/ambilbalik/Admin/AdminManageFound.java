package com.example.ambilbalik.Admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.provider.Settings;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ambilbalik.AddEvent;
import com.example.ambilbalik.Found;
import com.example.ambilbalik.InputLocation;
import com.example.ambilbalik.R;
import com.example.ambilbalik.ViewMyFoundItem;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.squareup.picasso.Picasso;

public class AdminManageFound extends AppCompatActivity implements OnMapReadyCallback {
    Found found = new Found();

    ImageView itemPicture;
    EditText itemName,itemDescription,eventDate;
    TextView founderName;
    MapView mapView;
    String foundID, newString;
    MarkerOptions markerOptions;
    GoogleMap m;
    boolean isPermissionGranted;

    FirebaseFirestore db= FirebaseFirestore.getInstance();
    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_manage_found);

        found = (Found) getIntent().getSerializableExtra("Hold Found");


        itemPicture = findViewById(R.id.itemPicture);
        itemName = findViewById(R.id.itemName);
        itemDescription = findViewById(R.id.itemDescription);
        founderName = findViewById(R.id.founderName);
        eventDate = findViewById(R.id.eventDate);


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
        displayFoundItem(found);
    }

    public void displayFoundItem(Found found){
        itemName.setText(found.getItemName());
        itemDescription.setText(found.getItemDescription());
        founderName.setText(found.getFounderName());
        eventDate.setText(found.getEventDate());
        Picasso.get().load(found.getImageUrl()).into(itemPicture);

    }

    public void removeItem(View view) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(AdminManageFound.this);
        dialog.setCancelable(false);
        dialog.setTitle("Confirmation Alert");
        dialog.setMessage("Are you sure you want to delete this item?" );
        dialog.setPositiveButton("Remove", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                //execute Delete
                db.collection("found").document("Found " + found.getFoundUid()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(AdminManageFound.this, "Item been deleted", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(AdminManageFound.this, AdminFoundList.class);
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

    private void checkMyPermission(){
        Dexter.withContext(this).withPermission(Manifest.permission.ACCESS_FINE_LOCATION).withListener(new PermissionListener() {
            @Override
            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                Toast.makeText(AdminManageFound.this, "Permission Granted", Toast.LENGTH_SHORT).show();
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
        map.getUiSettings().setAllGesturesEnabled(false);

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

    public void toMyFoundList(View view) {
        Intent intent = new Intent(AdminManageFound.this, AdminFoundList.class);
        startActivity(intent);
    }
}