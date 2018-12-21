package com.parking.bardh.parkingfinder;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.parking.bardh.parkingfinder.ActiveParking.ActiveParkingAdapter;
import com.parking.bardh.parkingfinder.App.AppConfig;
import com.parking.bardh.parkingfinder.Parkings.ParkingAdapter;
import com.parking.bardh.parkingfinder.Parkings.ParkingFragment;
import com.parking.bardh.parkingfinder.Zones.ZoneAdapter;
import com.parking.bardh.parkingfinder.model.ActiveParking;
import com.parking.bardh.parkingfinder.model.Parking;
import com.parking.bardh.parkingfinder.model.Zone;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

import cz.msebera.android.httpclient.Header;


public class AdministratorActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private UserLocalStore uls;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private ActiveParkingAdapter mAdapter;
    private List<ActiveParking> mActiveParkingList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_administrator);
        uls = new UserLocalStore(this);

        //Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.app_name);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        //NavigationView
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View headerView = navigationView.getHeaderView(0);
        TextView txt_header_user = (TextView) headerView.findViewById(R.id.header_user);
        TextView txt_header_userCard = (TextView) headerView.findViewById(R.id.header_carID);
        txt_header_user.setText(uls.getUserLoggedIn().getFullname());
        txt_header_userCard.setText(uls.getUserLoggedIn().getCarIdentification());


        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerViewAdministrator);

        mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        getParkingData1();
        getParkingData();
    }

    public void getParkingData1() {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams param = new RequestParams();

            param.put("all", "");


        client.get(AppConfig.URL + "parking.php", param, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                try {
                    if (response.getBoolean("success")) {
                        JSONArray parkingJsonArray = (JSONArray) response.get("data");
                        AppConfig.setParkingList(Parking.parkingFromJson(parkingJsonArray));
                    }else{
                        Toast.makeText(getApplicationContext(), "Collecting data failed. Try again", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(),LoginActivity.class));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                Toast.makeText(getApplicationContext(), "Request faield! Try Again", Toast.LENGTH_SHORT).show();
            }


            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Toast.makeText(getApplicationContext(), "Request faield! Try Again", Toast.LENGTH_SHORT).show();
            }
        });
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        switch (id) {
            case R.id.nav_all_parking:
                startActivity(new Intent(getApplicationContext(),AdministratorActivity.class));
                break;
            case R.id.nav_profile:
                RegisterActivity.editProfile(uls.getUserLoggedIn());
                startActivity(new Intent(getApplicationContext(),RegisterActivity.class));
                break;
            case R.id.nav_logout:
                uls.setUserLoggedIn(false);
                uls.storeUserData(null);
                Intent intent = new Intent(AdministratorActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void getParkingData() {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams param = new RequestParams();
         param.put("all","");

        client.get(AppConfig.URL + "active_parking.php", param, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                try {
                    if (response.getBoolean("success")) {
                        JSONArray activeParkingJsonArray = (JSONArray) response.get("data");
                        mActiveParkingList = ActiveParking.parkingFromJson(activeParkingJsonArray);
                        setData();
                    }else{
                        Toast.makeText(getApplicationContext(), "Collecting data failed. Try again", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(),LoginActivity.class));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                Toast.makeText(getApplicationContext(), "Request faield! Try Again", Toast.LENGTH_SHORT).show();
            }


            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Toast.makeText(getApplicationContext(), "Request faield! Try Again", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setData() {
        mAdapter = new ActiveParkingAdapter(mActiveParkingList);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), mRecyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {

            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
    }
}
