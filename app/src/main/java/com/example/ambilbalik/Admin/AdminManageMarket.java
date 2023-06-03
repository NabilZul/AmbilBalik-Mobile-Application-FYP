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
import com.example.ambilbalik.Found;
import com.example.ambilbalik.InputLocation;
import com.example.ambilbalik.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

public class AdminManageMarket extends AppCompatActivity {
    Found found = new Found();
    EditText itemName,itemDescription,eventDate;
    ImageView itemPicture;
    TextView founderName;
    FirebaseFirestore db= FirebaseFirestore.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_manage_market);

        found = (Found) getIntent().getSerializableExtra("Hold Found");

        itemPicture = findViewById(R.id.itemPicture);
        itemName = findViewById(R.id.itemName);
        itemDescription = findViewById(R.id.itemDescription);
        eventDate = findViewById(R.id.eventDate);
        founderName = findViewById(R.id.founderName);
        displayMarketItem(found);
    }
    public void displayMarketItem(Found found){
        itemName.setText(found.getItemName());
        itemDescription.setText(found.getItemDescription());
        founderName.setText(found.getFounderName());
        eventDate.setText(found.getEventDate());
        Picasso.get().load(found.getImageUrl()).into(itemPicture);

    }
    public void removeItem(View view) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(AdminManageMarket.this);
        dialog.setCancelable(false);
        dialog.setTitle("Confirmation Alert");
        dialog.setMessage("Are you sure you want to remove this item?" );
        dialog.setPositiveButton("Remove", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                //execute Delete
                DocumentReference reference = db.collection("found").document("Found " + found.getFoundUid());
                reference.update("itemStatus","B");
                Intent intent = new Intent(AdminManageMarket.this, AdminMarketList.class);
                startActivity(intent);

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

    public void toMarketList(View view) {
        Intent intent = new Intent(AdminManageMarket.this, AdminMarketList.class);
        startActivity(intent);
    }
}