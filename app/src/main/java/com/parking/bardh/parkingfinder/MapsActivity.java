package com.parking.bardh.parkingfinder;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.data.kml.KmlContainer;
import com.google.maps.android.data.kml.KmlLayer;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.Iterator;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        try {
            KmlLayer layer = new KmlLayer(mMap, R.raw.parkingfindermap, getApplicationContext());
            layer.addLayerToMap();
            for (KmlContainer container : layer.getContainers()) {
                if (container.hasProperty("name")) {
                    Log.d("RegisterActivity2", "AA:"+container.getContainers());
                    Iterator<KmlContainer> it_kml =  container.getContainers().iterator();
                    while(it_kml.hasNext()) {
                        KmlContainer kml = it_kml.next();
                        Log.d("RegisterActivity2", "SS:" + kml.getPlacemarks());
                    }
                }
            }
        } catch (IOException | XmlPullParserException e) {
            e.printStackTrace();
        }

        LatLngBounds PRISHTINA = new LatLngBounds(
                new LatLng(42.61081, 21.13385),new LatLng(42.68304, 21.19325));

         mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(PRISHTINA, 0));
        CameraUpdate zoom=CameraUpdateFactory.zoomTo(15);
        mMap.animateCamera(zoom);

    }

}
