package com.example.ambilbalik.Admin;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ambilbalik.AddEvent;
import com.example.ambilbalik.InputLocation;
import com.example.ambilbalik.Lost;
import com.example.ambilbalik.R;
import com.example.ambilbalik.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

public class AdminManageUser extends AppCompatActivity {
    User user = new User();
    EditText userName,userPhone,userIc,userAddress,userEmail;
    TextView userID;
    ImageView userPicture;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_manage_user);

        mAuth = FirebaseAuth.getInstance();

        user = (User) getIntent().getSerializableExtra("Hold User");

        userName = findViewById(R.id.userName);
        userPhone = findViewById(R.id.userPhone);
        userIc = findViewById(R.id.userIc);
        userAddress = findViewById(R.id.userAddress);
        userEmail = findViewById(R.id.userEmail);
        userID = findViewById(R.id.userID);
        userPicture = findViewById(R.id.userPicture);

        displayUser(user);
    }

    public void displayUser(User user){
        userName.setText(user.getUserName());
        userPhone.setText(user.getPhoneNumber());
        userIc.setText(user.getIcNumber());
        userAddress.setText(user.getUserAddress());
        userEmail.setText(user.getUserEmail());
        userID.setText(user.getUid());
        Picasso.get().load(user.getImagesUrl()).into(userPicture);
    }

    public void removeUser(View view) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(AdminManageUser.this);
        dialog.setCancelable(false);
        dialog.setTitle("Confirmation Alert");
        dialog.setMessage("Are you sure you want to remove this user?" );
        dialog.setPositiveButton("Remove", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                //execute Delete
                db.collection("users").document("User " + user.getUid()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(AdminManageUser.this, "User been deleted", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(AdminManageUser.this, AdminUserList.class);
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

    public void toUserList(View view) {
        Intent intent = new Intent(AdminManageUser.this, AdminUserList.class);
        startActivity(intent);
    }
}