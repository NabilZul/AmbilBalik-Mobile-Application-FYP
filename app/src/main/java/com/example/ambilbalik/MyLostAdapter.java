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

public class MyLostAdapter extends RecyclerView.Adapter<MyLostAdapter.MyLostViewHolder> {
    Context context;
    ArrayList<Lost> lostArrayList;
    FirebaseFirestore firebaseFirestore;
    private final OnItemClickListener listener;

    public MyLostAdapter(Context context, ArrayList<Lost> lostArrayList, OnItemClickListener listener) {

        this.context=context;
        this.lostArrayList=lostArrayList;
        this.listener=listener;
        firebaseFirestore=FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public MyLostAdapter.MyLostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.lostlist,parent,false);
        return new MyLostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyLostViewHolder holder, int position) {

        Lost lost=lostArrayList.get(position);
        holder.lostName.setText(lost.getItemName());
        holder.userName.setText(lost.getOwnerName());
        Picasso.get().load(lost.getImaggeUrl()).into(holder.itemPicture);
        holder.bind(lostArrayList.get(position), listener);

    }

    @Override
    public int getItemCount() {
        return lostArrayList.size();
    }

    public static class MyLostViewHolder extends RecyclerView.ViewHolder{

        TextView lostName,userName;
        ImageView itemPicture;


        public MyLostViewHolder(@NonNull View itemView) {
            super(itemView);
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            lostName=itemView.findViewById(R.id.lostName);
            userName=itemView.findViewById(R.id.userName);
            itemPicture=itemView.findViewById(R.id.itemPicture);



        }

        public void bind(final Lost lost, final OnItemClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    listener.onItemClick(lost);
                }
            });
        }


    }

    public interface OnItemClickListener {
        void onItemClick(Lost lost);
    }

}
