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
import com.example.ambilbalik.User;
import com.example.ambilbalik.ViewMyLostItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class AdminUserList extends AppCompatActivity {
    RecyclerView userRecyclerView;
    ArrayList<User> userArrayList;
    UserListAdapter userListAdapter;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_user_list);

        userRecyclerView = findViewById(R.id.userRecyclerView);
        userRecyclerView.setHasFixedSize(true);
        userRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        userArrayList = new ArrayList<User>();

        userListAdapter = new UserListAdapter(AdminUserList.this, userArrayList, new UserListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(User user) {
                Intent intent = new Intent(AdminUserList.this, AdminManageUser.class);
                intent.putExtra("Hold User",user);
                startActivity(intent);
            }
        });

        userRecyclerView.setAdapter(userListAdapter);

        displayView();
    }

    public void displayView(){
        db.collection("users").whereEqualTo("userType","user").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {

                    Log.e("error", error.getMessage());
                    return;
                }

                for (DocumentChange dc : value.getDocumentChanges()) {

                    if (dc.getType() == DocumentChange.Type.ADDED) {
                        userArrayList.add(dc.getDocument().toObject(User.class));

                    }
                }

                userListAdapter.notifyDataSetChanged();
            }

        });
    }

    public void goBackDashboard(View view) {
        Intent intent = new Intent(AdminUserList.this, AdminDashboard.class);
        startActivity(intent);
    }
}