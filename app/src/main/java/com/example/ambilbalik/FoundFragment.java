package com.example.ambilbalik;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class FoundFragment extends Fragment {
    RecyclerView recyclerFound;
    ArrayList<Found> foundArrayList;
    FoundListAdapter foundListAdapter;
    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    androidx.appcompat.widget.SearchView search;
    ArrayList<Found> search1;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.activity_found_fragment, container, false);
        recyclerFound = root.findViewById(R.id.recyclerFound);
        recyclerFound.setHasFixedSize(true);
        recyclerFound.setLayoutManager(new LinearLayoutManager(inflater.getContext()));
        search = root.findViewById(R.id.search);
        foundArrayList = new ArrayList<Found>();
        search1 = foundArrayList;

        foundListAdapter = new FoundListAdapter(inflater.getContext(), foundArrayList, new FoundListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Found found) {
                Intent intent = new Intent(inflater.getContext(), DisplayFoundItemDetail.class);
                intent.putExtra("Hold Found", found);
                startActivity(intent);
            }
        });
        recyclerFound.setAdapter(foundListAdapter);
        foundListAdapter.notifyDataSetChanged();

        search.setOnQueryTextListener(new SearchView.OnQueryTextListener()
        {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                search1 = new ArrayList<Found>();

                for (Found found : foundArrayList) {
                    if (found.getItemName().toLowerCase().contains(newText.toLowerCase())) {
                        if (!search1.contains(found)) {
                            search1.add(found);
                        }
                    }
                }
                foundListAdapter= new FoundListAdapter(inflater.getContext(), search1, new FoundListAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(Found found) {
                        Intent intent = new Intent(inflater.getContext(), DisplayFoundItemDetail.class);
                        intent.putExtra("Hold Found", found);
                        startActivity(intent);
                    }
                });
                recyclerFound.setAdapter(foundListAdapter);
                foundListAdapter.notifyDataSetChanged();
                return false;
            }
        });
        displayView();

        // Inflate the layout for this fragment
        return root;

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
                foundListAdapter.notifyDataSetChanged();
            }
        });
    }
}