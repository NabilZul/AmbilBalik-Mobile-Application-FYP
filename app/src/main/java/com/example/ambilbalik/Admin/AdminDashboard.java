package com.example.ambilbalik.Admin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.ambilbalik.Login;
import com.example.ambilbalik.R;
import com.example.ambilbalik.UserProfile;
import com.google.firebase.auth.FirebaseAuth;

public class AdminDashboard extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);
    }

    public void lostList(View view) {
        Intent intent = new Intent(AdminDashboard.this, AdminLostList.class);
        startActivity(intent);
    }

    public void signOut() {
        // [START auth_sign_out]
        FirebaseAuth.getInstance().signOut();
        // [END auth_sign_out]
    }

    public void logout(View view) {
        signOut();
        Intent intent = new Intent(AdminDashboard.this, Login.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }


    public void foundList(View view) {
        Intent intent = new Intent(AdminDashboard.this, AdminFoundList.class);
        startActivity(intent);
    }

    public void userList(View view) {
        Intent intent = new Intent(AdminDashboard.this, AdminUserList.class);
        startActivity(intent);
    }

    public void marketPlaceList(View view) {
        Intent intent = new Intent(AdminDashboard.this, AdminMarketList.class);
        startActivity(intent);
    }
}