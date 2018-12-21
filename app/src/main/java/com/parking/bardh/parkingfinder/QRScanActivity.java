package com.parking.bardh.parkingfinder;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Vibrator;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.google.zxing.Result;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.parking.bardh.parkingfinder.App.AppConfig;
import com.parking.bardh.parkingfinder.Parkings.ParkingAdapter;
import com.parking.bardh.parkingfinder.Parkings.ParkingFragment;
import com.parking.bardh.parkingfinder.model.User;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import cz.msebera.android.httpclient.Header;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

import static android.Manifest.permission.CAMERA;


public class QRScanActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    private static final int REQUEST_CAMERA = 1;
    ZXingScannerView ScannerView;
    private UserLocalStore uls;
    private String TAG = "QRScanActivity";
    private static String confirmation_code = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrscan);
        ScannerView = new ZXingScannerView(this);
        setContentView(ScannerView);
        uls = new UserLocalStore(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkPermission()) {
            } else {
                requestPermissions();
            }
        }
    }

    @Override
    public void handleResult(Result result) {
        if (result != null) {
            String resultTxt = result.getText();
            String decode_txt = null;
            String number = null;
            String parking = null;
            String carID = uls.getUserLoggedIn().getCarIdentification();
            try {
                byte[] data = Base64.decode(resultTxt, Base64.DEFAULT);
                decode_txt = new String(data, "UTF-8");
                Log.d("QRScanActivity2","decode_txt:"+decode_txt);
                JSONObject obj = new JSONObject(decode_txt);
//                number = "+37745409217";
                number = "+"+obj.getString("sms");
                parking = obj.getString("id_parking");
                Log.d("QRScanActivity2","AANUmber:"+number);
                Log.d("QRScanActivity2","AAparking:"+parking);

            } catch (Throwable t) {
                Log.e("QRScanActivity2", "Could not parse malformed JSON: \"" + decode_txt + "\"");
                Log.e("QRScanActivity2",t.toString());
            }
            //TODO: Pjesa e targave
            RequestParams params = new RequestParams();
            JSONObject data = new JSONObject();
            try {
                data.put("id_parking", parking);
                data.put("id_user", uls.getUserLoggedIn().getUserId());
                data.put("carID", uls.getUserLoggedIn().getCarIdentification());
            } catch (Exception e) {
                e.printStackTrace();
            }

            Log.d("QRScanActivity2","parking:"+parking);
            Log.d("QRScanActivity2","DATA:"+data);
            Log.d("QRScanActivity2","params:"+params);
            params.put("data", data);
            insertActiveParking(params);

            SmsManager sms = SmsManager.getDefault();
            sms.sendTextMessage(number, null, parking + ":" + carID, ClientActivity.sendPI, ClientActivity.deliveredPI);
            Log.d("SmsReceiver", "SMSSENT@1:");
            Toast.makeText(this, "Loading Data", Toast.LENGTH_LONG).show();

            Vibrator vib = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
            vib.vibrate(100);
        }
        onBackPressed();
    }

    private void insertActiveParking(final RequestParams params){
        AsyncHttpClient client = new AsyncHttpClient();
        client.post(AppConfig.URL+"active_parking.php", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                try {
                    if (response.getBoolean("success")) {
                        Log.d("QRScanActivity2",response.getString("confirmation_code"));
                        ClientActivity.confirmation_code = response.getString("confirmation_code");
//                        Toast.makeText(getApplicationContext(), "Confirmation: "+response.getString("confirmation_code"), Toast.LENGTH_LONG).show();
                        ParkingFragment.dateSetChanged();
                    } else {
                        Toast.makeText(getApplicationContext(), "Request Failed", Toast.LENGTH_SHORT).show();
                        Log.e("QRScanActivity2", "ERROR:"+response.toString());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                Toast.makeText(getApplicationContext(), "Request Failed", Toast.LENGTH_SHORT).show();
                Log.e("QRScanActivity2", "ERRORA:"+responseString);
                Log.e("QRScanActivity2", "ERROR:"+throwable.toString());
                throwable.printStackTrace();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Toast.makeText(getApplicationContext(), "Request Failed", Toast.LENGTH_SHORT).show();
                Log.e("QRScanActivity2", "ERROR:"+errorResponse.toString());
                Log.e("QRScanActivity2", "ERROR:"+throwable.toString());
                throwable.printStackTrace();
            }
        });
    }

    private boolean checkPermission() {
        return (ContextCompat.checkSelfPermission(QRScanActivity.this, CAMERA) == PackageManager.PERMISSION_GRANTED);
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{CAMERA}, REQUEST_CAMERA);
    }

    @Override
    protected void onPause() {
        super.onPause();
        ScannerView.stopCamera();
    }

    @Override
    protected void onResume() {
        super.onResume();

        ScannerView.setResultHandler(this);
        ScannerView.startCamera();
    }
}
