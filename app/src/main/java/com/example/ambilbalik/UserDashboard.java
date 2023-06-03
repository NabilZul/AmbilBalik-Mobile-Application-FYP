package com.example.ambilbalik;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class UserDashboard extends AppCompatActivity {
    BottomNavigationView bottom_navigator;
    String currentDate,foundID;
    Found found = new Found();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    SliderView sliderView;
    int[] images = {R.drawable.poster1, R.drawable.poster2, R.drawable.poster3};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_dashboard);

        bottom_navigator = findViewById(R.id.bottom_navigator);
        bottom_navigator.setSelectedItemId(R.id.home);

        sliderView = findViewById(R.id.imageSlider);
        SliderAdapter sliderAdapter = new SliderAdapter(images);

        sliderView.setSliderAdapter(sliderAdapter);
        sliderView.setIndicatorAnimation(IndicatorAnimationType.WORM);
        sliderView.setSliderTransformAnimation(SliderAnimations.DEPTHTRANSFORMATION);
        sliderView.startAutoCycle();

        bottomNav();
    }



    public void bottomNav(){
        bottom_navigator.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.home:
                        Intent intent = new Intent(UserDashboard.this,UserDashboard.class);
                        startActivity(intent);
                        break;

                    case R.id.search:
                        Intent intent1 = new Intent(UserDashboard.this,UserSearch.class);
                        startActivity(intent1);
                        break;

                    case R.id.profile:
                        Intent intent2 = new Intent(UserDashboard.this,UserProfile.class);
                        startActivity(intent2);
                        break;
                }
                return false;

            }
        });
    }

    public void goAddEvent(View view){
        Intent intent = new Intent(UserDashboard.this, AddEvent.class);
        startActivity(intent);
    }

    public void toMyLostItem(View view) {
        Intent intent= new Intent(UserDashboard.this, MyLostItem.class);
        startActivity(intent);
    }

    public void toMyFoundItem(View view) {
        Intent intent = new Intent(UserDashboard.this,MyFoundItem.class);
        startActivity(intent);
    }

    public void toMarketplace(View view) {
        Intent intent = new Intent(UserDashboard.this,Marketplace.class);
        startActivity(intent);
    }
}