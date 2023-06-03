package com.example.ambilbalik.Admin;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.ambilbalik.Found;
import com.example.ambilbalik.MyFoundAdapter;
import com.example.ambilbalik.MyFoundItem;
import com.example.ambilbalik.R;
import com.example.ambilbalik.UserDashboard;
import com.example.ambilbalik.ViewMyFoundItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class AdminFoundList extends AppCompatActivity {
    RecyclerView myFoundRecyclerView;
    ArrayList<Found> foundArrayList;
    MyFoundAdapter myFoundAdapter;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_found_list);
        myFoundRecyclerView = findViewById(R.id.MyFoundRecyclerView);
        myFoundRecyclerView.setHasFixedSize(true);
        myFoundRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        foundArrayList = new ArrayList<Found>();

        myFoundAdapter = new MyFoundAdapter(AdminFoundList.this, foundArrayList, new MyFoundAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Found found) {
                Intent intent = new Intent(AdminFoundList.this, AdminManageFound.class);
                intent.putExtra("Hold Found",found);
                startActivity(intent);
            }
        });

        myFoundRecyclerView.setAdapter(myFoundAdapter);
        displayView();
    }

    public void displayView() {

        db.collection("found").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null){
                    Log.e("error", error.getMessage());
                    return;
                }
                for (DocumentChange dc : value.getDocumentChanges())
                {
                    if (dc.getType() == DocumentChange.Type.ADDED){
                        foundArrayList.add(dc.getDocument().toObject(Found.class));
                    }
                }
                myFoundAdapter.notifyDataSetChanged();
            }
        });
    }

    public void goBackDashboard(View view) {
        Intent intent = new Intent(AdminFoundList.this, AdminDashboard.class);
        startActivity(intent);
    }
}