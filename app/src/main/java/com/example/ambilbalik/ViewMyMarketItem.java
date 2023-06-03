package com.example.ambilbalik;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ambilbalik.Admin.AdminManageMarket;
import com.example.ambilbalik.Admin.AdminMarketList;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

public class ViewMyMarketItem extends AppCompatActivity {
    Found found = new Found();
    EditText itemName,itemDescription,eventDate;
    ImageView itemPicture;
    TextView founderName;
    FirebaseFirestore db= FirebaseFirestore.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_my_market_item);
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
    public void toMarketList(View view) {
        Intent intent = new Intent(ViewMyMarketItem.this, MyMarketList.class);
        startActivity(intent);
    }

    public void removeItem(View view) {
        DocumentReference reference = db.collection("found").document("Found " + found.getFoundUid());
        reference.update("itemStatus","B");
        Intent intent = new Intent(ViewMyMarketItem.this, MyMarketList.class);
        startActivity(intent);
    }
}