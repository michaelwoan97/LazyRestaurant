package com.gymlazy.lazyrestaurant.Controllers;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.gymlazy.lazyrestaurant.Models.Restaurant;
import com.gymlazy.lazyrestaurant.Models.RestaurantList;
import com.gymlazy.lazyrestaurant.R;
import com.gymlazy.lazyrestaurant.YelpAPI;
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

/**
 * Copyright: GymLazy All rights reserved
 *
 * @author michaelwoan97
 * Creation Date: 2021-07-02
 * Description: ResDetailFragment
 */
public class ResDetailFragment extends Fragment {
    private static final String RES_ID_KEY = "resID";
    private DotsIndicator mDotsIndicator;
    private ViewAdapter mViewAdapter;
    private ViewPager mViewPager;
    private LinearLayout mProgressLayout;
    private Restaurant mRestaurant;

    public static Fragment newInstance(String sResID){
        Bundle b = new Bundle();
        b.putSerializable(RES_ID_KEY, sResID);

        Fragment fragment = new ResDetailFragment();
        fragment.setArguments(b);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String sResID = getArguments().getString(RES_ID_KEY).toString();
        new YelpRequest(this.getContext()).execute(sResID);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.restaurant_detail_layout, container, false);

        mViewPager = (ViewPager) v.findViewById(R.id.view_pager);
        mDotsIndicator = (DotsIndicator) v.findViewById(R.id.dots);
        mProgressLayout = (LinearLayout) v.findViewById(R.id.progress_indicator_detail);
        mViewPager.setVisibility(View.GONE);
        mDotsIndicator.setVisibility(View.GONE);

        return v;
    }

    public void setupAdapter() {
        mViewAdapter = new ViewAdapter(this.getContext(), mRestaurant);
        mViewPager.setAdapter(mViewAdapter);
        mDotsIndicator.setViewPager(mViewPager);
    }

    private class YelpRequest extends AsyncTask<String, Void, Restaurant>{
        private Context mContext;

        public YelpRequest(Context packageContext){
            mContext = packageContext;
        }

        @Override
        protected Restaurant doInBackground(String... strings) {
            String sResId = strings[0];
            RestaurantList restaurantList = RestaurantList.get(mContext);
            Restaurant restaurant = null;

            try {
                restaurant = restaurantList.fetchRestaurantDetail(sResId);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return restaurant;
        }

        @Override
        protected void onPostExecute(Restaurant restaurant) {
            mProgressLayout.setVisibility(View.GONE);
            mViewPager.setVisibility(View.VISIBLE);
            mDotsIndicator.setVisibility(View.VISIBLE);
            mRestaurant = restaurant;
            setupAdapter();
        }
    }
}
