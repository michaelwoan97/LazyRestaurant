package com.gymlazy.lazyrestaurant.Controllers;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

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
    private static final String TAG = "HomeFragment";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.home_layout, container, false);

        mSearchBtn = (Button) v.findViewById(R.id.search_btn);
        mSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(HomeFragment.this.getContext(), RestaurantListActivity.class);
                startActivity(i);

            }
        });

        return v;
    }

}
