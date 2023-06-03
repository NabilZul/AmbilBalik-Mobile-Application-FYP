package com.example.ambilbalik.Admin;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ambilbalik.Lost;
import com.example.ambilbalik.MyLostAdapter;
import com.example.ambilbalik.R;
import com.example.ambilbalik.User;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.MyViewHolder> {
    Context context;
    ArrayList<User> userArrayList;
    FirebaseFirestore firebaseFirestore;
    private final OnItemClickListener listener;

    public UserListAdapter(Context context, ArrayList<User> userArrayList, OnItemClickListener listener) {

        this.context = context;
        this.userArrayList = userArrayList;
        this.listener = listener;
        firebaseFirestore = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public UserListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.lostlist, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        User user = userArrayList.get(position);
        holder.userEmail.setText(user.getUserEmail());
        holder.userName.setText(user.getUserName());
        Picasso.get().load(user.getImagesUrl()).into(holder.userPicture);
        holder.bind(userArrayList.get(position), listener);

    }

    @Override
    public int getItemCount() {
        return userArrayList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView userName, userEmail;
        ImageView userPicture;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            userEmail = itemView.findViewById(R.id.lostName);
            userName = itemView.findViewById(R.id.userName);
            userPicture = itemView.findViewById(R.id.itemPicture);


        }

        public void bind(final User user, final UserListAdapter.OnItemClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(user);
                }
            });
        }


    }

    public interface OnItemClickListener {
        void onItemClick(User user);
    }
}
