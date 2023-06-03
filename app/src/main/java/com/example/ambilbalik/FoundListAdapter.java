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

public class FoundListAdapter extends RecyclerView.Adapter<FoundListAdapter.FoundListViewHolder> {
    Context context;
    ArrayList<Found> foundArrayList;
    FirebaseFirestore firebaseFirestore;
    private final FoundListAdapter.OnItemClickListener listener;


    public FoundListAdapter(Context context, ArrayList<Found> foundArrayList, FoundListAdapter.OnItemClickListener listener) {
        this.context=context;
        this.foundArrayList=foundArrayList;
        this.listener= listener;
        firebaseFirestore=FirebaseFirestore.getInstance();
    }


    @NonNull
    @Override
    public FoundListAdapter.FoundListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.lostlist,parent,false);
        return new FoundListAdapter.FoundListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FoundListAdapter.FoundListViewHolder holder, int position) {

        Found found=foundArrayList.get(position);
        holder.lostName.setText(found.getItemName());
        holder.userName.setText(found.getFounderName());
        //holder.displayCoordinate.setText(String.valueOf(found.getItemLatitude()) + String.valueOf(found.getItemLongitude()));
        Picasso.get().load(found.getImageUrl()).into(holder.itemPicture);
//        holder.request_date.setText(booking.getRequest_date());
//        holder.request_time.setText(booking.getRequest_time());
//        holder.status.setText(booking.getBooking_status());
//        holder.request_service.setText(booking.getRequest_service());
//        holder.booking_id.setText(booking.getBooking_uid());
        holder.bind(foundArrayList.get(position), listener);

    }

    @Override
    public int getItemCount() {
        return foundArrayList.size();
    }

    public class FoundListViewHolder extends RecyclerView.ViewHolder {


        TextView lostName,userName,displayCoordinate;
        ImageView itemPicture;

        public FoundListViewHolder(@NonNull View itemView) {
            super(itemView);
            lostName=itemView.findViewById(R.id.lostName);
            userName=itemView.findViewById(R.id.userName);
            itemPicture=itemView.findViewById(R.id.itemPicture);
            displayCoordinate=itemView.findViewById(R.id.displayCoordinate);
//            request_date=itemView.findViewById(R.id.request_date);
//            request_time=itemView.findViewById(R.id.request_time);
//            request_service=itemView.findViewById(R.id.gig_service);
//            booking_id=itemView.findViewById(R.id.booking_id);
//            status=itemView.findViewById(R.id.status);

        }

        public void bind(final Found found, final FoundListAdapter.OnItemClickListener listener) {
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
