package com.parking.bardh.parkingfinder.Parkings;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.parking.bardh.parkingfinder.App.AppConfig;
import com.parking.bardh.parkingfinder.LoginActivity;
import com.parking.bardh.parkingfinder.R;
import com.parking.bardh.parkingfinder.RecyclerTouchListener;
import com.parking.bardh.parkingfinder.model.Parking;
import com.parking.bardh.parkingfinder.model.Zone;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class ParkingFragment extends Fragment {

    public static ParkingFragment newInstance() {
        return new ParkingFragment();
    }

    private String TAG = "TestFragment";
    private static Zone zone = null;
    private static RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    public static ParkingAdapter mAdapter;
    private static final int SPAN_COUNT = 2;
    private static Context context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_parkings, container, false);
        Log.d(TAG, "onCreateView Parking");
        context = getContext();
        getParkingData();

        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerViewParking);
        mLayoutManager = new GridLayoutManager(getActivity(), SPAN_COUNT);
        mRecyclerView.setLayoutManager(mLayoutManager);
        return view;
    }

    public void getParkingData() {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams param = new RequestParams();
        if(zone == null) {
            param.put("all", "");
        }else{
            param.put("byZone",zone.getZoneID());
            zone=null;
        }

        client.get(AppConfig.URL + "parking.php", param, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                try {
                    if (response.getBoolean("success")) {
                        JSONArray parkingJsonArray = (JSONArray) response.get("data");
                        AppConfig.setParkingList(Parking.parkingFromJson(parkingJsonArray));
                        setData();
                    }else{
                        Toast.makeText(getContext(), "Collecting data failed. Try again", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getActivity().getApplicationContext(),LoginActivity.class));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                Toast.makeText(getContext(), "Request faield! Try Again", Toast.LENGTH_SHORT).show();
            }


            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Toast.makeText(getContext(), "Request faield! Try Again", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setData() {
        mAdapter = new ParkingAdapter(AppConfig.ParkingList);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(getActivity().getApplicationContext(), mRecyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Parking p = AppConfig.ParkingList.get(position);
            }

            @Override
            public void onLongClick(View view, int position) {

                final Parking p = AppConfig.ParkingList.get(position);

                final Dialog myDialog = new Dialog(getContext());
                myDialog.setContentView(R.layout.parking_info_popup);
                TextView tv_pp_pname = (TextView) myDialog.findViewById(R.id.textView_pp_pname);
                TextView tv_pp_paddress = (TextView) myDialog.findViewById(R.id.textView_pp_paddress);
                TextView tv_pp_pinfo = (TextView) myDialog.findViewById(R.id.textView_pp_pinfo);
                TextView tv_pp_pinfo1 = (TextView) myDialog.findViewById(R.id.textView_pp_pinfo1);
                TextView tv_pp_pclose = (TextView) myDialog.findViewById(R.id.textView_pp_close);
                Button btn_gps = (Button) myDialog.findViewById(R.id.btn_pp_gps);

                btn_gps.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String uri = "http://maps.google.com/maps?daddr="+p.getParking_latitude()+","+p.getParking_longitude();
                        Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                                Uri.parse(uri));
                        startActivity(intent);
                    }
                });

                tv_pp_pname.setText(p.getParking_name());
                tv_pp_paddress.setText(p.getParking_address());
                tv_pp_pinfo.setText(p.getParking_work_time() + "    " + p.getParking_price() + "€");
                tv_pp_pinfo1.setText(p.getParking_description());


                tv_pp_pclose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        myDialog.dismiss();
                    }
                });

                myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                myDialog.show();
            }
        }));
    }

    public static void dateSetChanged(){
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams param = new RequestParams();
        if(zone == null) {
            param.put("all", "");
        }else{
            param.put("byZone",zone.getZoneID());
            zone=null;
        }

        client.get(AppConfig.URL + "parking.php", param, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                try {
                    if (response.getBoolean("success")) {
                        JSONArray parkingJsonArray = (JSONArray) response.get("data");
                        AppConfig.setParkingList(Parking.parkingFromJson(parkingJsonArray));
                        mAdapter = new ParkingAdapter(AppConfig.ParkingList);
                        mRecyclerView.setAdapter(mAdapter);
                        mRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(context, mRecyclerView, new RecyclerTouchListener.ClickListener() {
                            @Override
                            public void onClick(View view, int position) {
                                Parking p = AppConfig.ParkingList.get(position);
                            }

                            @Override
                            public void onLongClick(View view, int position) {

                                final Parking p = AppConfig.ParkingList.get(position);

                                final Dialog myDialog = new Dialog(context);
                                myDialog.setContentView(R.layout.parking_info_popup);
                                TextView tv_pp_pname = (TextView) myDialog.findViewById(R.id.textView_pp_pname);
                                TextView tv_pp_paddress = (TextView) myDialog.findViewById(R.id.textView_pp_paddress);
                                TextView tv_pp_pinfo = (TextView) myDialog.findViewById(R.id.textView_pp_pinfo);
                                TextView tv_pp_pinfo1 = (TextView) myDialog.findViewById(R.id.textView_pp_pinfo1);
                                TextView tv_pp_pclose = (TextView) myDialog.findViewById(R.id.textView_pp_close);
                                Button btn_gps = (Button) myDialog.findViewById(R.id.btn_pp_gps);

                                btn_gps.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        String uri = "http://maps.google.com/maps?daddr="+p.getParking_latitude()+","+p.getParking_longitude();
                                        Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                                                Uri.parse(uri));
                                        context.startActivity(intent);
                                    }
                                });

                                tv_pp_pname.setText(p.getParking_name());
                                tv_pp_paddress.setText(p.getParking_address());
                                tv_pp_pinfo.setText(p.getParking_work_time() + "    " + p.getParking_price() + "€");
                                tv_pp_pinfo1.setText(p.getParking_description());


                                tv_pp_pclose.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        myDialog.dismiss();
                                    }
                                });

                                myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                myDialog.show();
                            }
                        }));
                    }else{
                        Toast.makeText(context, "Collecting data failed. Try again", Toast.LENGTH_SHORT).show();
                        context.startActivity(new Intent(context,LoginActivity.class));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                Toast.makeText(context, "Request faield! Try Again", Toast.LENGTH_SHORT).show();
            }


            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Toast.makeText(context, "Request faield! Try Again", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static void setZone(Zone z) {
        zone = z;
    }
}
