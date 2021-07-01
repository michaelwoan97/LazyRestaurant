package com.gymlazy.lazyrestaurant.Controllers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.gymlazy.lazyrestaurant.Models.Restaurant;
import com.gymlazy.lazyrestaurant.Models.RestaurantList;
import com.gymlazy.lazyrestaurant.R;

import org.json.JSONException;

import java.io.IOException;
import java.util.List;

/**
 * Copyright: GymLazy All rights reserved
 *
 * @author michaelwoan97
 * Creation Date: 2021-06-07
 * Description: HomeFragment
 */
public class HomeFragment extends Fragment {
    private Button mSearchBtn;
    private EditText mDestination;
    private TextInputLayout mDestinationLayout;
    private static final String TAG = "HomeFragment";
    private int mNormalRequest = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.home_layout, container, false);

        mDestinationLayout = (TextInputLayout) v.findViewById(R.id.desination_layout);
        mDestination = (EditText) v.findViewById(R.id.destination);
        mDestination.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus){
                    hideKeyboardFrom(HomeFragment.this.getContext(), v);
                }
            }
        });
        mSearchBtn = (Button) v.findViewById(R.id.search_btn);
        mSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mDestination.getText().toString().isEmpty()){
                    mDestinationLayout.setError("Sorry, please provide a location!");
                    return;
                }
                mDestinationLayout.setError(null);
                Intent i = RestaurantListActivity.newIntent(HomeFragment.this.getContext(), mNormalRequest, mDestination.getText().toString());
                startActivity(i);
                HomeFragment.this.getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });


        return v;
    }

    public static void hideKeyboardFrom(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

}
