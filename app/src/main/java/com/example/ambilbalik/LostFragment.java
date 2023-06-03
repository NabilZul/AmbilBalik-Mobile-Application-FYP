package com.example.ambilbalik;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Locale;

public class LostFragment extends Fragment  {
    RecyclerView recyclerLost;
    ArrayList<Lost> lostArrayList;
    LostListAdapter lostListAdapter;
    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    androidx.appcompat.widget.SearchView search;
    ArrayList<Lost> search1;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.activity_lost_fragment, container, false);
        recyclerLost = root.findViewById(R.id.recyclerLost);
        recyclerLost.setHasFixedSize(true);
        recyclerLost.setLayoutManager(new LinearLayoutManager(inflater.getContext()));
        search = root.findViewById(R.id.search);

        lostArrayList = new ArrayList<Lost>();

        search1 = lostArrayList;

        lostListAdapter = new LostListAdapter(inflater.getContext(), lostArrayList, new LostListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Lost lost) {
                Intent intent = new Intent(inflater.getContext(), DisplayLostItemDetail.class);
                intent.putExtra("Hold Lost", lost);
                startActivity(intent);
            }
        });
        recyclerLost.setAdapter(lostListAdapter);
        lostListAdapter.notifyDataSetChanged();

        search.setOnQueryTextListener(new SearchView.OnQueryTextListener()
        {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                search1 = new ArrayList<Lost>();

                for (Lost lost : lostArrayList) {
                    if (lost.getItemName().toLowerCase().contains(newText.toLowerCase())) {
                        if (!search1.contains(lost)) {
                            search1.add(lost);
                        }
                    }
                }
                lostListAdapter= new LostListAdapter(inflater.getContext(),search1, new LostListAdapter.OnItemClickListener(){
                    @Override
                    public void onItemClick(Lost lost) {
                        Intent intent = new Intent(inflater.getContext(), DisplayLostItemDetail.class);
                        intent.putExtra("Hold Lost", lost);
                        startActivity(intent);
                    }
                });
                recyclerLost.setAdapter(lostListAdapter);
                lostListAdapter.notifyDataSetChanged();
                return false;
            }
        });
        displayView();

        // Inflate the layout for this fragment
        return root;
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
                lostListAdapter.notifyDataSetChanged();
            }
        });
    }
}
