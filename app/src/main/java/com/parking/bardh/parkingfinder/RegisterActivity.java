package com.parking.bardh.parkingfinder;

import android.app.ActionBar;
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
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.parking.bardh.parkingfinder.App.AppConfig;
import com.parking.bardh.parkingfinder.model.User;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class RegisterActivity extends AppCompatActivity {

    private static Button btn_backtologin;
    private static Button btn_register;
    private EditText mEditTextFullName;
    private EditText mEditTextCar;
    private EditText mEditTextEmail;
    private EditText mEditTextPassword;
    private static TextView mtextViewRegister;
    private final String TAG = "RegisterActivity2";
    private static User user = null;
    private UserLocalStore uls;
    // Progress Dialog Object
    private ProgressDialog prgDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mEditTextFullName = findViewById(R.id.editText_fullname);
        mEditTextCar = findViewById(R.id.editText_car);
        mEditTextEmail = findViewById(R.id.editText_email);
        mEditTextPassword = findViewById(R.id.editText_password);
        mtextViewRegister = findViewById(R.id.textView_register);
        btn_register = findViewById(R.id.btn_register);
        btn_backtologin = findViewById(R.id.btn_backtologin);
        Log.d(TAG,"onCreate");
        if(user != null){
            uls = new UserLocalStore(this);
            mEditTextFullName.setText(user.getFullname());
            mEditTextCar.setText(user.getCarIdentification());
            mEditTextEmail.setText(user.getEmail());

            mtextViewRegister.setText("Change profile");
            btn_backtologin.setText("Back");
            btn_backtologin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(user.getUserRoleId() == 1){
                        startActivity(new Intent(getApplicationContext(),AdministratorActivity.class));
                    }else if (user.getUserRoleId() == 3){
                        startActivity(new Intent(getApplicationContext(),ClientActivity.class));
                    }
                }
            });
            btn_register.setText("Change");
            btn_register.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String new_ufname = mEditTextFullName.getText().toString();
                    String new_ucar = mEditTextCar.getText().toString();
                    String new_uemail = mEditTextEmail.getText().toString();
                    String new_upassword = mEditTextPassword.getText().toString();

                    if (!TextUtils.isEmpty(new_ufname) || !TextUtils.isEmpty(new_uemail) || !TextUtils.isEmpty(new_upassword)) {
                        if (new_upassword.length() < 6) {
                            Toast.makeText(getApplicationContext(), "Passwords must be at least 6 characters", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (Patterns.EMAIL_ADDRESS.matcher(new_uemail).matches()) {
                            RequestParams params = new RequestParams();
                            user.setFullname(new_ufname);
                            user.setCarIdentification(new_ucar);
                            user.setEmail(new_uemail);

                            JSONObject data = User.updateUser(user, new_upassword);
                            params.put("data", data);
                            changeUser(params);


                        } else {
                            Toast.makeText(getApplicationContext(), "Please enter valid email", Toast.LENGTH_LONG).show();
                        }

                    }
                };
            });
        }else{
            btn_backtologin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    btnBackToLoginClick(v);
                }
            });
            btn_register.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    btnRegisterClick(v);
                }
            });
        }
    }



    public void btnRegisterClick(View v) {

        prgDialog = new ProgressDialog(this);
        prgDialog.setMessage("Please wait...");
        prgDialog.setCancelable(false);

        String new_ufname = mEditTextFullName.getText().toString();
        String new_ucar = mEditTextCar.getText().toString();
        String new_uemail = mEditTextEmail.getText().toString();
        String new_upassword = mEditTextPassword.getText().toString();

        if (!TextUtils.isEmpty(new_ufname) || !TextUtils.isEmpty(new_uemail) || !TextUtils.isEmpty(new_upassword)) {
            if (new_upassword.length() < 6) {
                Toast.makeText(getApplicationContext(), "Passwords must be at least 6 characters", Toast.LENGTH_SHORT).show();
                return;
            }
            if (Patterns.EMAIL_ADDRESS.matcher(new_uemail).matches()) {
                RequestParams params = new RequestParams();
                JSONObject data = User.registerUser(new_ufname, new_ucar, new_uemail, new_upassword);
                params.put("data", data);
                prgDialog.show();
                createUser(params);

            } else {
                Toast.makeText(getApplicationContext(), "Please enter valid email", Toast.LENGTH_LONG).show();
            }

        } else {
            Toast.makeText(getApplicationContext(), "Please fill the form with *", Toast.LENGTH_LONG).show();
        }
    }

    public void btnBackToLoginClick(View v) {
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
    }

    private void createUser(final RequestParams params) {
        AsyncHttpClient client = new AsyncHttpClient();
        client.post(AppConfig.URL+"register_user.php", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                try {
                    if (response.getBoolean("success")) {
                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                        intent.putExtra("email",mEditTextEmail.getText());
                        startActivity(intent);
                    } else {
                        if (response.getString("data").contains("Duplicate")) {
                            Toast.makeText(getApplicationContext(), "Email is already used", Toast.LENGTH_SHORT).show();
                            mEditTextEmail.requestFocus();
                            return;
                        }
                        Toast.makeText(getApplicationContext(), "Request Failed", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "ERROR:"+response.toString());
                    }
                } catch (Exception e) {
                   e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                Toast.makeText(getApplicationContext(), "Request Failed", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "ERROR:"+responseString);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Toast.makeText(getApplicationContext(), "Request Failed", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "ERROR:"+errorResponse.toString());
            }
        });
        prgDialog.hide();
    }

    private void changeUser(final RequestParams params) {
        AsyncHttpClient client = new AsyncHttpClient();
        client.post(AppConfig.URL+"change_user.php", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                try {
                    if (response.getBoolean("success")) {
                        Intent intent = null;
                        if(user.getUserRoleId() == 1){
                            intent = new Intent(getApplicationContext(),AdministratorActivity.class);
                        }else if (user.getUserRoleId() == 3){
                            intent = new Intent(getApplicationContext(),ClientActivity.class);
                        }
                        uls.storeUserData(user);
                        Toast.makeText(getApplicationContext(), response.getString("data"), Toast.LENGTH_SHORT).show();
                        user = null;
                        startActivity(intent);
                    } else {
                        if (response.getString("data").contains("Duplicate")) {
                            Toast.makeText(getApplicationContext(), "Email is already used", Toast.LENGTH_SHORT).show();
                            mEditTextEmail.requestFocus();
                            return;
                        }
                        Toast.makeText(getApplicationContext(), "Request Failed", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "ERROR:"+response.toString());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                Toast.makeText(getApplicationContext(), "Request Failed", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "ERROR:"+responseString);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Toast.makeText(getApplicationContext(), "Request Failed", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "ERROR:"+errorResponse.toString());
            }
        });

    }

    public static void editProfile(User u){
       user = u;
    }
}
