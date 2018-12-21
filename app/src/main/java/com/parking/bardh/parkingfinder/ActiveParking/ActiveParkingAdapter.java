package com.parking.bardh.parkingfinder.ActiveParking;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.parking.bardh.parkingfinder.R;
import com.parking.bardh.parkingfinder.model.ActiveParking;


import java.util.List;

public class ActiveParkingAdapter  extends RecyclerView.Adapter<ActiveParkingAdapter.ViewHolder> {
    private List<ActiveParking> activeParkingList;
    private static final String TAG = "ActiveParkingAdapter";

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_ap_carID;
        TextView tv_ap_out;
        TextView tv_ap_mleft;


        public ViewHolder(View v) {
            super(v);
            tv_ap_carID = (TextView) v.findViewById(R.id.textView_ap_carId);
            tv_ap_out = (TextView) v.findViewById(R.id.textView_ap_out);
            tv_ap_mleft = (TextView) v.findViewById(R.id.textView_ap_mleft);
        }
    }

    public ActiveParkingAdapter(List<ActiveParking> list) {
        activeParkingList = list;
    }

    @Override
    public ActiveParkingAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view.
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.administrator_recycleview_list, viewGroup, false);


        return new ActiveParkingAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ActiveParkingAdapter.ViewHolder viewHolder, final int position) {

        ActiveParking ap = activeParkingList.get(position);
        viewHolder.tv_ap_carID.setText(ap.getParking().getParking_name());
        viewHolder.tv_ap_out.setText(ap.getCarID());
        viewHolder.tv_ap_mleft.setText("("+ap.getMinutes_left()+"min)");
    }

    @Override
    public int getItemCount() {
        return activeParkingList.size();
    }
}
