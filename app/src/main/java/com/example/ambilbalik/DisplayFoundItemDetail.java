package com.example.ambilbalik;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
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
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class DisplayFoundItemDetail extends AppCompatActivity implements OnMapReadyCallback {

    Found found;
    TextView itemName,founderName,displayCoodinate;
    ImageView itemPicture;
    String newString;
    EditText eventDate, itemDescription;
    boolean isPermissionGranted;
    Button showDetail;
    LinearLayout layoutDetail;

    MapView mapView;
    MarkerOptions markerOptions;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_found_item_detail);

        found = (Found) getIntent().getSerializableExtra("Hold Found");

        itemName=findViewById(R.id.itemName);
        founderName=findViewById(R.id.founderName);
        displayCoodinate=findViewById(R.id.displayCoordinate);
        itemPicture=findViewById(R.id.itemPicture);
        eventDate = findViewById(R.id.eventDate);
        itemDescription = findViewById(R.id.itemDescription);
        showDetail = findViewById(R.id.showDetail);
        layoutDetail = findViewById(R.id.layoutDetail);

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

    private void checkMyPermission(){
        Dexter.withContext(this).withPermission(Manifest.permission.ACCESS_FINE_LOCATION).withListener(new PermissionListener() {
            @Override
            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                Toast.makeText(DisplayFoundItemDetail.this, "Permission Granted", Toast.LENGTH_SHORT).show();
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
        LatLng utemLocation = new LatLng(Double.parseDouble(found.getItemLatitude()),Double.parseDouble(found.getItemLongitude()));
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
        map.addMarker(new MarkerOptions().position(utemLocation).title("Item Found Here:"));
                map.moveCamera(CameraUpdateFactory.newLatLng(utemLocation));

//                //create marker
//                markerOptions = new MarkerOptions();
//                //add marker on map
//                map.addMarker(markerOptions);


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

    public void displayFoundItem(Found found)
    {
        itemName.setText(found.getItemName());
        founderName.setText(found.getFounderName());
        Picasso.get().load(found.getImageUrl()).into(itemPicture);
        eventDate.setText(found.getEventDate());
        itemDescription.setText(found.getItemDescription());
        displayCoodinate.setText(String.valueOf(found.getItemLatitude()) + " : " + String.valueOf(found.getItemLongitude()));
//        markerOptions.position
//        markerOptions.getPosition(found.getItemLatitude().toString())
    }

    public void backToFoundFragment(View view){
        Intent intent = new Intent(DisplayFoundItemDetail.this,UserSearch.class);
        startActivity(intent);
    }


    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    public void OpenInfo(View view) {
        showDetail.setVisibility(View.INVISIBLE);
        layoutDetail.setVisibility(View.VISIBLE);
    }

    public void hideDetail(View view) {
        layoutDetail.setVisibility(View.INVISIBLE);
        showDetail.setVisibility(View.VISIBLE);
    }


//    public void contactUser(View view) {
//
//        String message = "Hi, I am contacting you from AmbilBalik";
//
//        try {
//            PackageManager packageManager = this.getPackageManager();
//            Intent i = new Intent(Intent.ACTION_VIEW);
//            String url = "https://api.whatsapp.com/send?phone=+6" + found.getFounderName() + "&text=" + message;
//            i.setPackage("com.whatsapp");
//            i.setData(Uri.parse(url));
//            if (i.resolveActivity(packageManager) != null) {
//                startActivity(i);
//            }
//        } catch (Exception e) {
//        }
//    }

    public void contactFounder(View view) {
        String message = "Hi, I am contacting you from AmbilBalik";

        try {
            PackageManager packageManager = this.getPackageManager();
            Intent i = new Intent(Intent.ACTION_VIEW);
            String url = "https://api.whatsapp.com/send?phone=+6" + found.getFounderPhone() + "&text=" + message;
            i.setPackage("com.whatsapp");
            i.setData(Uri.parse(url));
            if (i.resolveActivity(packageManager) != null) {
                startActivity(i);
            }
        } catch (Exception e) {
        }
    }
}