package com.example.ambilbalik;

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
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

public class InputLocation extends AppCompatActivity implements OnMapReadyCallback {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    Found found = new Found();
    String newString;
    boolean isPermissionGranted;
    MapView mapView;
    MarkerOptions markerOptions;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_location);


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
        Log.e( "onCreate: ","1" );
        if (isPermissionGranted){
            Log.e( "onCreate: ","2" );
            mapView.getMapAsync(InputLocation.this);
            Log.e( "onCreate: ","3" );
            mapView.onCreate(savedInstanceState);
        }
        Log.e( "onCreate: ","4" );
    }

    public void backToAddEvent(View view){
        AlertDialog.Builder dialog = new AlertDialog.Builder(InputLocation.this);
        dialog.setCancelable(false);
        dialog.setTitle("Confirmation Alert");
        dialog.setMessage("Are you sure you want to delete this item?" );
        dialog.setPositiveButton("Discard", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                //execute Delete
                db.collection("found").document("Found "+newString).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(InputLocation.this, "Item been discarded", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(InputLocation.this, AddEvent.class);
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
                Toast.makeText(InputLocation.this, "Permission Granted", Toast.LENGTH_SHORT).show();
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
        LatLng utemLocation = new LatLng(2.3138, 102.3211);
        LatLngBounds adelaideBounds = new LatLngBounds(
                new LatLng(2.304404, 102.313474), // SW bounds
                new LatLng(2.319718, 102.327781)  // NE bounds
        );
// Constrain the camera target to the utem.
        map.setLatLngBoundsForCameraTarget(adelaideBounds);
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(utemLocation)      // Sets the center of the map to utem
                .zoom(17)                   // Sets the zoom
                .build();                   // Creates a CameraPosition from the builder
        map.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                //create marker
                markerOptions = new MarkerOptions();
                //set Marker Position
                markerOptions.position(latLng);
                //set market lat and long
                markerOptions.title(latLng.latitude + " : " + latLng.longitude);

                //clear previously clicked position
                map.clear();
                //zoom marker
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,18));
                //add marker on map
                map.addMarker(markerOptions);
            }
        });
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

    public void saveLocation(View view){

        Log.e("haha",String.valueOf(markerOptions.getPosition().latitude));
        DocumentReference reference = db.collection("found").document("Found "+newString);
        //found.setItemLatitude(String.valueOf(markerOptions.getPosition().latitude));
        //found.setItemLongitude(String.valueOf(markerOptions.getPosition().longitude));

        reference.update("itemLatitude",String.valueOf(markerOptions.getPosition().latitude));
        reference.update("itemLongitude",String.valueOf(markerOptions.getPosition().longitude));
        Toast.makeText(InputLocation.this, "Found Event Submitted", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(InputLocation.this,UserDashboard.class);
        startActivity(intent);

    }
}