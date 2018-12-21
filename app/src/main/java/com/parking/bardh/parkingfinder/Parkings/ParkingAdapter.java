package com.parking.bardh.parkingfinder.Parkings;


import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import android.view.ViewGroup;
import android.widget.TextView;

import com.parking.bardh.parkingfinder.R;
import com.parking.bardh.parkingfinder.model.Parking;

import java.util.List;

public class ParkingAdapter extends RecyclerView.Adapter<ParkingAdapter.ViewHolder> {

    private List<Parking> parkingList;
    private static final String TAG = "TestFragment";

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView parkingName;
        TextView parkingPrice;
        TextView parkingSize;
        TextView parkingTime;
        TextView parkingAddress;

        public ViewHolder(View v) {
            super(v);
            parkingName = (TextView) v.findViewById(R.id.textView_parkingName);
            parkingPrice = (TextView) v.findViewById(R.id.textView_parkingPrice);
            parkingSize = (TextView) v.findViewById(R.id.textView_parkingSize);
            parkingTime = (TextView) v.findViewById(R.id.textView_parkingTime);
            parkingAddress = (TextView) v.findViewById(R.id.textView_parkingAddress);
            parkingAddress.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "ADDRESS CLICK");
                }
            });
        }
    }

    public ParkingAdapter(List<Parking> list) {
        parkingList = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view.
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.parking_recycleview_grid, viewGroup, false);


        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {

        Parking p = parkingList.get(position);
        viewHolder.parkingName.setText(p.getParking_name());
        viewHolder.parkingPrice.setText(p.getParking_price() + "€");
        viewHolder.parkingSize.setText(p.getActive() + "/" + p.getParking_space());
        viewHolder.parkingTime.setText(p.getParking_work_time());
        viewHolder.parkingAddress.setText(p.getParking_address());
        double diff = p.getParking_space() - p.getActive();
        Float percentage = (float)(diff / p.getParking_space()) * 100;

        if (percentage > 50) {
            Drawable d = ContextCompat.getDrawable(viewHolder.itemView.getContext(), R.drawable.background_border_green).mutate();
            viewHolder.itemView.setBackground(d);
        } else if (percentage > 10) {
            Drawable d = ContextCompat.getDrawable(viewHolder.itemView.getContext(), R.drawable.background_border_yellow).mutate();
            viewHolder.itemView.setBackground(d);
        } else {
            Drawable d = ContextCompat.getDrawable(viewHolder.itemView.getContext(), R.drawable.background_border_red).mutate();
            viewHolder.itemView.setBackground(d);
        }

    }

    @Override
    public int getItemCount() {
        return parkingList.size();
    }


}
