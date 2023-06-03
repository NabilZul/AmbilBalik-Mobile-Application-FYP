package com.example.ambilbalik;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.tabs.TabLayout;

public class UserSearch extends AppCompatActivity {
    BottomNavigationView bottom_navigator;
    TabLayout tabLayout;
    ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_search);

        bottom_navigator = findViewById(R.id.bottom_navigator);
        bottom_navigator.setSelectedItemId(R.id.search);
        tabLayout=findViewById(R.id.tabLayout);
        viewPager=findViewById(R.id.viewPager);

        tabLayout.addTab(tabLayout.newTab().setText("Lost"));
        tabLayout.addTab(tabLayout.newTab().setText("Found"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final TabAdapter tabAdapter = new TabAdapter(this, getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(tabAdapter);

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        bottomNav();
    }

    public void bottomNav(){
       bottom_navigator.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
           @Override
           public boolean onNavigationItemSelected(@NonNull MenuItem item) {
               switch (item.getItemId()){
                   case R.id.home:
                       Intent intent = new Intent(UserSearch.this,UserDashboard.class);
                       startActivity(intent);
                       break;

                   case R.id.search:
                       Intent intent1 = new Intent(UserSearch.this,UserSearch.class);
                       startActivity(intent1);
                       break;

                   case R.id.profile:
                       Intent intent2 = new Intent(UserSearch.this,UserProfile.class);
                       startActivity(intent2);
                       break;
               }
               return false;

           }
       });
    }
}