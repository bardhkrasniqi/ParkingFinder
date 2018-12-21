package com.parking.bardh.parkingfinder.Zones;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.parking.bardh.parkingfinder.R;
import com.parking.bardh.parkingfinder.model.Zone;

import java.util.List;

public class ZoneAdapter extends RecyclerView.Adapter<ZoneAdapter.MyViewHolder>{

    private List<Zone> zoneList;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView tvZone;

        public MyViewHolder(View view) {
            super(view);
            tvZone = (TextView) view.findViewById(R.id.textView_zona);
        }
    }

    public ZoneAdapter(List<Zone> zoneList){
        this.zoneList = zoneList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.zone_recycleview_list,parent,false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ZoneAdapter.MyViewHolder holder, int position) {
        Zone zone = zoneList.get(position);
        holder.tvZone.setText(zone.getName());
    }

    @Override
    public int getItemCount() {
        return zoneList.size();
    }
}
