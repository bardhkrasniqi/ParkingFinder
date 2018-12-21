package com.parking.bardh.parkingfinder.Zones;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.parking.bardh.parkingfinder.App.AppConfig;
import com.parking.bardh.parkingfinder.ClientActivity;
import com.parking.bardh.parkingfinder.Parkings.ParkingFragment;
import com.parking.bardh.parkingfinder.R;
import com.parking.bardh.parkingfinder.RecyclerTouchListener;
import com.parking.bardh.parkingfinder.model.Zone;
import org.json.JSONArray;
import org.json.JSONObject;


import cz.msebera.android.httpclient.Header;

public class ZoneFragment extends Fragment {

    public static ZoneFragment newInstance() {
        return new ZoneFragment();
    }

    private static final String TAG = "zoneFragment";
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_zones, container, false);
        Log.d(TAG, "onCreateView Zone");
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerViewZone);

        mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "Resume");
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(AppConfig.URL+"zone.php", new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                try {
                    JSONArray zoneJsonArray = (JSONArray) response.get("data");
                    AppConfig.setZoneList(Zone.zoneFromJson(zoneJsonArray));
                   setData();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                Toast.makeText(getActivity().getApplicationContext(), "Request faield! Try Again", Toast.LENGTH_SHORT).show();
            }


            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Toast.makeText(getActivity().getApplicationContext(), "Request faield! Try Again", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setData(){
        mAdapter = new ZoneAdapter(AppConfig.ZoneList);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mAdapter);

        mRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(getActivity().getApplicationContext(), mRecyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Zone z = AppConfig.ZoneList.get(position);
                ParkingFragment.setZone(z);
                changeFragment(new ParkingFragment().newInstance());
            }

            @Override
            public void onLongClick(View view, int position) {
            }
        }));
    }

    private void changeFragment(Fragment fragment){
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.mainContent,fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
}
