package com.parking.bardh.parkingfinder;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.parking.bardh.parkingfinder.App.AppConfig;
import com.parking.bardh.parkingfinder.model.User;
import com.parking.bardh.parkingfinder.model.Zone;

import org.json.JSONArray;
import org.json.JSONObject;


import cz.msebera.android.httpclient.Header;


public class LoginActivity extends AppCompatActivity {

    //    Elements
    private Button mLogin;
    private Button mRegister;
    private EditText mEditTextUsername;
    private EditText mEditTextPassword;
    // Progress Dialog Object
    private ProgressDialog prgDialog;
    private UserLocalStore uls;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mEditTextUsername = findViewById(R.id.editText_username);
        mEditTextPassword = findViewById(R.id.editText_password);
        mRegister = findViewById(R.id.btn_register);
        mLogin = findViewById(R.id.btn_login);
        mEditTextUsername.setText("bardh@gmail.com");
        mEditTextPassword.setText("123456");

        uls = new UserLocalStore(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        uls.clearUserData();
        Intent intent = getIntent();
        if (intent.getExtras() != null) {
            String intent_email = intent.getExtras().get("email").toString();
            mEditTextUsername.setText(intent_email);
            mEditTextPassword.setText("");
            Toast.makeText(getApplicationContext(), "You are registed on our system. Proceed with Login", Toast.LENGTH_SHORT).show();
        }
    }

    // Button Login
    public void btnLoginClick(View view) {
        //initialize ProgressDialog
        prgDialog = new ProgressDialog(this);
        // Set Progress Dialog Text
        prgDialog.setMessage("Please wait...");
        // Set Cancelable as False
        prgDialog.setCancelable(false);

        // Getting email & password from inputs
        String email = mEditTextUsername.getText().toString();
        String password = mEditTextPassword.getText().toString();

        RequestParams params = new RequestParams();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {

            Toast.makeText(getApplicationContext(), "Please fill the form, don't leave any field blank", Toast.LENGTH_LONG).show();
        } else {
            if (Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                params.put("email", email);
                params.put("password", password);
                authenticate(params);
            } else {
                Toast.makeText(getApplicationContext(), "Please enter valid email", Toast.LENGTH_LONG).show();
            }

        }
    }

    // Button Register
    public void btnRegisterClick(View v) {
        Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
        startActivity(intent);
    }

    // Authentication request on server
    public void authenticate(RequestParams params) {
        prgDialog.show();
        AsyncHttpClient client = new AsyncHttpClient();
        String url = AppConfig.URL + "authentication.php";
        client.get(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                try {
                    if (response.getBoolean("success")) {

                        JSONObject userJson = (JSONObject) response.get("data");
                        User u = User.userFromJson(userJson);
                        uls.setUserLoggedIn(true);
                        uls.storeUserData(u);
                        Intent intent = null;
                        if (u.getUserRoleId() == 3) {
                            intent = new Intent(getApplicationContext(), ClientActivity.class);
                        } else {
                            intent = new Intent(getApplicationContext(), AdministratorActivity.class);
                        }
                        getZones();
                        startActivity(intent);
                    } else {
                        Toast.makeText(getApplicationContext(), "Login failed. Try again", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                prgDialog.hide();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                Toast.makeText(getApplicationContext(), "Request Failed", Toast.LENGTH_SHORT).show();
                Log.e("ParkingError", "ERROR:" + responseString);
                prgDialog.hide();
            }


            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                prgDialog.hide();
                Toast.makeText(getApplicationContext(), "Request Failed", Toast.LENGTH_SHORT).show();
                Log.e("ParkingError", throwable.toString());
            }
        });
    }

    private void getZones() {
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(AppConfig.URL + "zone.php", new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                try {
                    JSONArray zoneJsonArray = (JSONArray) response.get("data");
                    AppConfig.setZoneList(Zone.zoneFromJson(zoneJsonArray));
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
}
