package com.gymlazy.lazyrestaurant;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {
    private double mLatitude;
    private double mLongitude;
    private static final String EXTRA_COORDINATES = "com.gymlazy.lazyrestaurant.mapsactivity.extra_coordinates";
    private GoogleMap mMap;

    public static Intent newIntent(Context packageContext, double[] coordinates){
        Intent i = new Intent(packageContext, MapsActivity.class);
        i.putExtra(EXTRA_COORDINATES, coordinates);
        return i;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.res_activity_map);

        double[] dCoordinates = getIntent().getDoubleArrayExtra(EXTRA_COORDINATES);
        mLatitude = dCoordinates[0];
        mLongitude = dCoordinates[1];

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // Respond to the action bar's Up/Home button
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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

        // Add a marker in Sydney and move the camera
        LatLng resLat = new LatLng(mLatitude, mLongitude);
        mMap.addMarker(new MarkerOptions().position(resLat));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(resLat, 18));
    }
}