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
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.net.URLEncoder;

public class DisplayLostItemDetail extends AppCompatActivity {
    Lost lost;
    TextView ownerName;
    EditText itemName, itemDescription, eventDate;
    ImageView itemPicture;

    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_lost_item_detail);
        lost = (Lost) getIntent().getSerializableExtra("Hold Lost");

        itemPicture = findViewById(R.id.itemPicture);
        itemName = findViewById(R.id.itemName);
        ownerName = findViewById(R.id.ownerName);
        itemDescription = findViewById(R.id.itemDescription);
        eventDate = findViewById(R.id.eventDate);


        displayLostItem(lost);
    }

    public void displayLostItem(Lost lost) {
        ownerName.setText(lost.getOwnerName());
        itemName.setText(lost.getItemName());
        itemDescription.setText(lost.getItemDescription());
        eventDate.setText(lost.getEventDate());
        Picasso.get().load(lost.getImaggeUrl()).into(itemPicture);
    }

    public void backToLostFragment(View view) {
        Intent intent = new Intent(DisplayLostItemDetail.this, UserSearch.class);
        startActivity(intent);
    }

    public void contactUser(View view) {

        String message = "Hi, I am contacting you from AmbilBalik";

        try {
            PackageManager packageManager = this.getPackageManager();
            Intent i = new Intent(Intent.ACTION_VIEW);
            String url = "https://api.whatsapp.com/send?phone=+6" + lost.getOwnerPhone() + "&text=" + message;
            i.setPackage("com.whatsapp");
            i.setData(Uri.parse(url));
            if (i.resolveActivity(packageManager) != null) {
                startActivity(i);
            }
        } catch (Exception e) {
        }
    }
}