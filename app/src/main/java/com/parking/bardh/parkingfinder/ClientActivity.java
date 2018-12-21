package com.parking.bardh.parkingfinder;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.parking.bardh.parkingfinder.Parkings.ParkingFragment;
import com.parking.bardh.parkingfinder.Zones.ZoneFragment;


public class ClientActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private UserLocalStore uls;
    int MY_PREMISSIONS_REQUEST_SEND_SMS = 1;
    int MY_PERMISSIONS_REQUEST_RECEIVE_SMS = 1;
    public static String confirmation_code = "";

    String SENT = "SMS_SENT";
    String DELIVERED = "SMS_DELIVERED";
    public static PendingIntent sendPI, deliveredPI;
    BroadcastReceiver smsSentReceiver, smsDeliveredReciver, smsReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        uls = new UserLocalStore(this);
        setContentView(R.layout.activity_main);


        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.mainContent, ParkingFragment.newInstance())
                    .commitNow();
        }

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

        sendPI = PendingIntent.getBroadcast(this, 0, new Intent(SENT), 0);
        deliveredPI = PendingIntent.getBroadcast(this, 0, new Intent(DELIVERED), 0);


    }

    @Override
    protected void onStart() {
        super.onStart();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, MY_PREMISSIONS_REQUEST_SEND_SMS);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECEIVE_SMS}, MY_PERMISSIONS_REQUEST_RECEIVE_SMS);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        smsSentReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
//                        Toast.makeText(context, "SMS Sent!", Toast.LENGTH_SHORT).show();
                        Log.d("SmsReceiver","SMS SENT!");
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        Toast.makeText(context, "Generic Failure!", Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        Toast.makeText(context, "NO Service!", Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        Toast.makeText(context, "NULL_PDU!", Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        Toast.makeText(context, "RADIO_OFF!", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        };

        smsDeliveredReciver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
//                        Toast.makeText(context, "SMS Sent!", Toast.LENGTH_SHORT).show();
                        Log.d("SmsReceiver","SMS SENT!");
                        break;
                    case Activity.RESULT_CANCELED:
                        Toast.makeText(context, "SMS not delivered!", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        };


        smsReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String TAG = "SmsReceiver";
                // Get the data (SMS data) bound to intent
                Bundle bundle = intent.getExtras();

                SmsMessage[] msgs = null;

                String str = "";

                if (bundle != null) {
                    // Retrieve the SMS Messages received
                    Object[] pdus = (Object[]) bundle.get("pdus");
                    msgs = new SmsMessage[pdus.length];

//                     For every SMS message received
                    for (int i = 0; i < msgs.length; i++) {
                        msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                        String number = msgs[i].getOriginatingAddress();
                        if (number.equals("+37745409217")) {
                            Log.d(TAG, "NR OK");
                            String message = msgs[i].getMessageBody();
                            if (message.contains(":")) {
                                Log.d(TAG, "CONTAINS :");
                                SmsManager sms = SmsManager.getDefault();
                                String carID = message.split(":")[1];
                                String smsText = "Transaksioni përfundoi me sukses. Kodi i konfirmimit "+confirmation_code;
                                sms.sendTextMessage("+37745409217", null, smsText, ClientActivity.sendPI, ClientActivity.deliveredPI);
                                Log.d(TAG, "SMS SENT@2");
                                Log.d(TAG, smsText);
                                Log.d(TAG, "" + smsText.length());
                            } else {
                                Log.d(TAG, "NOT CONTAINS :");
                                if (message.contains("Transaksioni përfundoi me sukses.")) {
                                    Toast.makeText(context, "Transaksioni përfundoi me sukses.", Toast.LENGTH_LONG).show();
                                    Toast.makeText(getApplicationContext(), "Confirmation: "+confirmation_code, Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(context, "Transaksioni dështoi! Provoni përsëri", Toast.LENGTH_LONG).show();
                                }
                            }

                        }
                    }

                    // Display the entire SMS Message

                    Log.d(TAG, str);
                    Log.d(TAG, "LENGTH::" + msgs.length);
                    Log.d(TAG, msgs.toString());
                }
            }
        };

        // Register a broadcast receiver
        IntentFilter intentFilter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
        intentFilter.setPriority(999);

        registerReceiver(smsSentReceiver, new IntentFilter(SENT));
        registerReceiver(smsDeliveredReciver, new IntentFilter(DELIVERED));
        registerReceiver(smsReceiver, intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(smsSentReceiver);
        unregisterReceiver(smsDeliveredReciver);
        unregisterReceiver(smsReceiver);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();


        switch (id) {
            case R.id.nav_all_parking:
                changeFragment(ParkingFragment.newInstance());
                break;
            case R.id.nav_scanqr:
                startActivity(new Intent(getApplicationContext(), QRScanActivity.class));
                break;
            case R.id.nav_map:
                startActivity(new Intent(getApplicationContext(),MapsActivity.class));
                break;
            case R.id.nav_find_by_zone:
                changeFragment(ZoneFragment.newInstance());
                break;

            case R.id.nav_profile:
                RegisterActivity.editProfile(uls.getUserLoggedIn());
                startActivity(new Intent(getApplicationContext(),RegisterActivity.class));
                break;

            case R.id.nav_logout:
                uls.setUserLoggedIn(false);
                uls.storeUserData(null);
                Intent intent = new Intent(ClientActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void changeFragment(Fragment fragment){
        FragmentManager fragmentManager =  getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.mainContent,fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

}
