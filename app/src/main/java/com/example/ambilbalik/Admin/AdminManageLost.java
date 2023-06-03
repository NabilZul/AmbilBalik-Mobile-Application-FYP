package com.example.ambilbalik.Admin;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ambilbalik.AddEvent;
import com.example.ambilbalik.InputLocation;
import com.example.ambilbalik.Lost;
import com.example.ambilbalik.MyLostItem;
import com.example.ambilbalik.R;
import com.example.ambilbalik.ViewMyLostItem;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

public class AdminManageLost extends AppCompatActivity {

    Lost lost = new Lost();

    ImageView itemPicture;
    EditText itemName, itemDescription,eventDate;
    TextView ownerName;
    String lostID;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_manage_lost);

        mAuth = FirebaseAuth.getInstance();

        lost = (Lost) getIntent().getSerializableExtra("Hold Lost");

        eventDate = findViewById(R.id.eventDate);
        itemPicture = findViewById(R.id.itemPicture);
        itemName = findViewById(R.id.itemName);
        ownerName = findViewById(R.id.ownerName);
        itemDescription = findViewById(R.id.itemDescription);

        displayLostItem(lost);
    }

    public void displayLostItem(Lost lost) {
        itemName.setText(lost.getItemName());
        itemDescription.setText(lost.getItemDescription());
        ownerName.setText(lost.getOwnerName());
        eventDate.setText(lost.getEventDate());
        Picasso.get().load(lost.getImaggeUrl()).into(itemPicture);
    }

    public void removeItem(View view) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(AdminManageLost.this);
        dialog.setCancelable(false);
        dialog.setTitle("Confirmation Alert");
        dialog.setMessage("Are you sure you want to delete this item?" );
        dialog.setPositiveButton("Remove", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                //execute Delete
                db.collection("lost").document("Lost " + lost.getLostUid()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(AdminManageLost.this, "Item been deleted", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(AdminManageLost.this, AdminLostList.class);
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

    public void toLostList(View view) {
        Intent intent = new Intent(AdminManageLost.this, AdminLostList.class);
        startActivity(intent);

    }
}