package com.example.ambilbalik;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

public class ViewMarketItem extends AppCompatActivity {
    Found found = new Found();
    EditText itemName,itemDescription,eventDate;
    ImageView itemPicture;
    TextView founderName;

    FirebaseFirestore db= FirebaseFirestore.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_market_item);
        found = (Found) getIntent().getSerializableExtra("Hold Found");

        itemPicture = findViewById(R.id.itemPicture);
        itemName = findViewById(R.id.itemName);
        itemDescription = findViewById(R.id.itemDescription);
        eventDate = findViewById(R.id.eventDate);
        founderName = findViewById(R.id.founderName);
        displayFoundItem(found);
    }
    public void displayFoundItem(Found found){
        itemName.setText(found.getItemName());
        itemDescription.setText(found.getItemDescription());
        founderName.setText(found.getFounderName());
        eventDate.setText(found.getEventDate());
        Picasso.get().load(found.getImageUrl()).into(itemPicture);

    }
    public void contact(View view) {
        String message = "Hi, I am contacting you from AmbilBalik";

        try {
            PackageManager packageManager = this.getPackageManager();
            Intent i = new Intent(Intent.ACTION_VIEW);
            String url = "https://api.whatsapp.com/send?phone=+6" + found.getFounderName() + "&text=" + message;
            i.setPackage("com.whatsapp");
            i.setData(Uri.parse(url));
            if (i.resolveActivity(packageManager) != null) {
                startActivity(i);
            }
        } catch (Exception e) {
        }
    }


    public void toMarketplace(View view) {
        Intent intent = new Intent(ViewMarketItem.this, Marketplace.class);
        startActivity(intent);
    }
}