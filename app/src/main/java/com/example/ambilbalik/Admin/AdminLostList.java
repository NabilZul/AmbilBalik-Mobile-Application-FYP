package com.example.ambilbalik.Admin;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.ambilbalik.Lost;
import com.example.ambilbalik.MyLostAdapter;
import com.example.ambilbalik.MyLostItem;
import com.example.ambilbalik.R;
import com.example.ambilbalik.ViewMyLostItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class AdminLostList extends AppCompatActivity {
    RecyclerView myLostRecyclerView;
    ArrayList<Lost> lostArrayList;
    MyLostAdapter myLostAdapter;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_lost_list);

        myLostRecyclerView = findViewById(R.id.MyLostRecyclerView);
        myLostRecyclerView.setHasFixedSize(true);
        myLostRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        lostArrayList = new ArrayList<Lost>();

        myLostAdapter = new MyLostAdapter(AdminLostList.this, lostArrayList, new MyLostAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Lost lost) {
                Intent intent = new Intent(AdminLostList.this, AdminManageLost.class);
                intent.putExtra("Hold Lost",lost);
                startActivity(intent);
            }
        });

        myLostRecyclerView.setAdapter(myLostAdapter);

        displayView();
    }

    public void displayView() {

        db.collection("lost").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null){
                    Log.e("error", error.getMessage());
                    return;
                }
                for (DocumentChange dc : value.getDocumentChanges())
                {
                    if (dc.getType() == DocumentChange.Type.ADDED){
                        lostArrayList.add(dc.getDocument().toObject(Lost.class));
                    }
                }
                myLostAdapter.notifyDataSetChanged();
            }
        });
    }
    public void goBackDashboard(View view) {
        Intent intent = new Intent(AdminLostList.this,AdminDashboard.class);
        startActivity(intent);
    }
}