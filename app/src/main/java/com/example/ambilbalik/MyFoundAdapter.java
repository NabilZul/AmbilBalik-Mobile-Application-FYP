package com.example.ambilbalik;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MyFoundAdapter extends RecyclerView.Adapter<MyFoundAdapter.MyFoundViewHolder> {
        Context context;
        ArrayList<Found> foundArrayList;
        FirebaseFirestore firebaseFirestore;
private final OnItemClickListener listener;

public MyFoundAdapter(Context context, ArrayList<Found> foundArrayList, OnItemClickListener listener) {

        this.context=context;
        this.foundArrayList=foundArrayList;
        this.listener=listener;
        firebaseFirestore=FirebaseFirestore.getInstance();
        }

@NonNull
@Override
public MyFoundAdapter.MyFoundViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.lostlist,parent,false);
        return new MyFoundViewHolder(view);
        }

@Override
public void onBindViewHolder(@NonNull MyFoundViewHolder holder, int position) {

        Found found=foundArrayList.get(position);
        holder.lostName.setText(found.getItemName());
        holder.userName.setText(found.getFounderName());
        Picasso.get().load(found.getImageUrl()).into(holder.itemPicture);
        holder.bind(foundArrayList.get(position), listener);

        }

@Override
public int getItemCount() {
        return foundArrayList.size();
        }

public static class MyFoundViewHolder extends RecyclerView.ViewHolder{

    TextView lostName,userName;
    ImageView itemPicture;


    public MyFoundViewHolder(@NonNull View itemView) {
        super(itemView);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        lostName=itemView.findViewById(R.id.lostName);
        userName=itemView.findViewById(R.id.userName);
        itemPicture=itemView.findViewById(R.id.itemPicture);

    }

    public void bind(final Found found, final MyFoundAdapter.OnItemClickListener listener) {
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                listener.onItemClick(found);
            }
        });
    }


}

public interface OnItemClickListener {
    void onItemClick(Found found);
}

}

