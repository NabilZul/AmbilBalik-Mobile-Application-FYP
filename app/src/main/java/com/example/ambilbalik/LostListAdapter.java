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

public class LostListAdapter extends RecyclerView.Adapter<LostListAdapter.LostListViewHolder> {
    Context context;
    ArrayList<Lost> lostArrayList;
    FirebaseFirestore firebaseFirestore;
    private final OnItemClickListener listener;


    public LostListAdapter(Context context, ArrayList<Lost> lostArrayList, OnItemClickListener listener) {
        this.context=context;
        this.lostArrayList=lostArrayList;
        this.listener= listener;
        firebaseFirestore=FirebaseFirestore.getInstance();
    }


    @NonNull
    @Override
    public LostListAdapter.LostListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.lostlist,parent,false);
        return new LostListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LostListAdapter.LostListViewHolder holder, int position) {

        Lost lost=lostArrayList.get(position);
        holder.lostName.setText(lost.getItemName());
        holder.userName.setText(lost.getOwnerName());
        Picasso.get().load(lost.getImaggeUrl()).into(holder.itemPicture);
//        holder.request_date.setText(booking.getRequest_date());
//        holder.request_time.setText(booking.getRequest_time());
//        holder.status.setText(booking.getBooking_status());
//        holder.request_service.setText(booking.getRequest_service());
//        holder.booking_id.setText(booking.getBooking_uid());
        holder.bind(lostArrayList.get(position), listener);

    }

    @Override
    public int getItemCount() {
        return lostArrayList.size();
    }

    public class LostListViewHolder extends RecyclerView.ViewHolder {


        TextView lostName,userName;
        ImageView itemPicture;

        public LostListViewHolder(@NonNull View itemView) {
            super(itemView);
            lostName=itemView.findViewById(R.id.lostName);
            userName=itemView.findViewById(R.id.userName);
            itemPicture=itemView.findViewById(R.id.itemPicture);
//            request_date=itemView.findViewById(R.id.request_date);
//            request_time=itemView.findViewById(R.id.request_time);
//            request_service=itemView.findViewById(R.id.gig_service);
//            booking_id=itemView.findViewById(R.id.booking_id);
//            status=itemView.findViewById(R.id.status);

        }

        public void bind(final Lost lost, final LostListAdapter.OnItemClickListener listener) {
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
