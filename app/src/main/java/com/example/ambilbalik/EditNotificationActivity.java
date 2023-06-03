package com.example.ambilbalik;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

public class EditNotificationActivity extends AppCompatActivity {
    Found found = new Found();
    EditText itemName,itemDescription,eventDate;
    ImageView itemPicture;
    TextView founderName;

    FirebaseFirestore db= FirebaseFirestore.getInstance();
    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_notification2);

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

    public void toEditNotification(View view) {
        Intent intent = new Intent(EditNotificationActivity.this,EditNotification.class);
        startActivity(intent);
    }

    public void notYet(View view) {
        Intent intent = new Intent(EditNotificationActivity.this,EditNotification.class);
        startActivity(intent);
    }

    public void addMarketPlace(View view) {
        DocumentReference reference = db.collection("found").document("Found " + found.getFoundUid());
        reference.update("itemStatus","C");
        Intent intent = new Intent(EditNotificationActivity.this, EditNotification.class);
        startActivity(intent);
    }
}