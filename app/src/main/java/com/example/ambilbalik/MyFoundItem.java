package com.example.ambilbalik;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class MyFoundItem extends AppCompatActivity {
    RecyclerView myFoundRecyclerView;
    ArrayList<Found> foundArrayList;
    MyFoundAdapter myFoundAdapter;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_found_item);
        myFoundRecyclerView = findViewById(R.id.MyFoundRecyclerView);
        myFoundRecyclerView.setHasFixedSize(true);
        myFoundRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        foundArrayList = new ArrayList<Found>();

        myFoundAdapter = new MyFoundAdapter(MyFoundItem.this, foundArrayList, new MyFoundAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Found found) {
                Intent intent = new Intent(MyFoundItem.this,ViewMyFoundItem.class);
                intent.putExtra("Hold Found",found);
                startActivity(intent);
            }
        });

        myFoundRecyclerView.setAdapter(myFoundAdapter);

        displayView();
    }

    public void displayView(){
        db.collection("found").whereEqualTo("userUid", currentUser.getUid()).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {

                    Log.e("error", error.getMessage());
                    return;
                }

                for (DocumentChange dc : value.getDocumentChanges()) {

                    if (dc.getType() == DocumentChange.Type.ADDED) {
                        foundArrayList.add(dc.getDocument().toObject(Found.class));

                    }
                }

                myFoundAdapter.notifyDataSetChanged();
            }

        });
    }

    public void goBackDashboard(View view) {
        Intent intent = new Intent(MyFoundItem.this, UserDashboard.class);
        startActivity(intent);
    }

    public void editNoti(View view) {
        Intent intent = new Intent(MyFoundItem.this, EditNotification.class);
        startActivity(intent);
    }
}