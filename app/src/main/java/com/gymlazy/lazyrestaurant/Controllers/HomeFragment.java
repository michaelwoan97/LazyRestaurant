package com.gymlazy.lazyrestaurant.Controllers;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.gymlazy.lazyrestaurant.Models.Restaurant;
import com.gymlazy.lazyrestaurant.Models.RestaurantList;
import com.gymlazy.lazyrestaurant.R;

import org.json.JSONException;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Copyright: GymLazy All rights reserved
 *
 * @author michaelwoan97
 * Creation Date: 2021-06-07
 * Description: HomeFragment
 */
public class HomeFragment extends Fragment implements LocationListener {
    private Button mSearchBtn;
    private Button mSearchNearbyBtn;
    private EditText mDestination;
    private TextInputLayout mDestinationLayout;
    private LocationManager mLocationManager;
    private static final String TAG = "HomeFragment";
    private int mNormalRequest = 0;
    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters

    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 minute

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.home_layout, container, false);

        mDestinationLayout = (TextInputLayout) v.findViewById(R.id.desination_layout);
        mDestination = (EditText) v.findViewById(R.id.destination);
        mDestination.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboardFrom(HomeFragment.this.getContext(), v);
                }
            }
        });
        mSearchBtn = (Button) v.findViewById(R.id.search_btn);
        mSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDestination.getText().toString().isEmpty()) {
                    mDestinationLayout.setError("Sorry, please provide a location!");
                    return;
                }
                mDestinationLayout.setError(null);
                Intent i = RestaurantListActivity.newIntent(HomeFragment.this.getContext(), mNormalRequest, mDestination.getText().toString());
                startActivity(i);
                HomeFragment.this.getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        mSearchNearbyBtn = (Button) v.findViewById(R.id.search_nearby_btn);
        mSearchNearbyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (!hasPermissions(HomeFragment.this.getContext(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION })) {
                        ActivityCompat.requestPermissions(HomeFragment.this.getActivity(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 101);
                        return;
                    } else {
                        Toast.makeText(HomeFragment.this.getContext(), "Able to access current location!", Toast.LENGTH_SHORT).show();
                        Location currentLocation = getLocation();
                        Log.d(TAG, "Current lat is: " + currentLocation.getLatitude() + " , current long is: " + currentLocation.getLongitude());
                        String sLocation = getLocationFromCoordinates(currentLocation);
                        Log.d(TAG, "Current city is " + sLocation);

                        Intent i = RestaurantListActivity.newIntent(HomeFragment.this.getContext(), mNormalRequest, sLocation);
                        startActivity(i);
                        HomeFragment.this.getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


        return v;
    }

    public static void hideKeyboardFrom(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    /*
     *	Function: hasPermissions(Context context, String... permissions)
     *	Description:
     *       The purpose of this function is to obtain permissions from the system for our application
     *	Parameter:  Context context         :  Activity Context
     *              String... permissions   :  Permission
     *	Return: boolean: true if successful, false otherwise
     */
    private static boolean hasPermissions(Context context, String... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * get current location (lat and long)
     * @return
     */
    public Location getLocation() {
        Location location = null;
        try {
            mLocationManager = (LocationManager) this.getContext().getSystemService(Context.LOCATION_SERVICE);

            // Getting GPS status
            boolean isGPSEnabled = mLocationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);

            // Getting network status
            boolean isNetworkEnabled = mLocationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isGPSEnabled && !isNetworkEnabled) {
                // No network provider is enabled
            } else {
                if (isNetworkEnabled) {
                    if (ActivityCompat.checkSelfPermission(this.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                            ActivityCompat.checkSelfPermission(this.getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        return null;
                    }
                    mLocationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

                    Log.d("Network", "Network");
                    if (mLocationManager != null) {
                        location = mLocationManager
                                .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    }
                }
                // If GPS enabled, get latitude/longitude using GPS Services
                if (isGPSEnabled) {
                    if (location == null) {
                        mLocationManager.requestLocationUpdates(
                                LocationManager.GPS_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                        Log.d("GPS Enabled", "GPS Enabled");
                        if (mLocationManager != null) {
                            location = mLocationManager
                                    .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return location;
    }

    /**
     * get location from coordinates
     * @param location
     * @return
     * @throws IOException
     */
    public String getLocationFromCoordinates(Location location) throws IOException {
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this.getContext(), Locale.getDefault());

        addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5

        String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
        String city = addresses.get(0).getLocality();
        String state = addresses.get(0).getAdminArea();
        String country = addresses.get(0).getCountryName();
        String postalCode = addresses.get(0).getPostalCode();
        String knownName = addresses.get(0).getFeatureName(); // Only if available else return NULL

        return city;
    }


    @Override
    public void onLocationChanged(@NonNull Location location) {

    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {

    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {

    }
}
