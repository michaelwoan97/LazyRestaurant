package com.gymlazy.lazyrestaurant.Controllers;

import android.content.Context;
import android.media.Rating;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.gymlazy.lazyrestaurant.Models.ResHours;
import com.gymlazy.lazyrestaurant.Models.Restaurant;
import com.gymlazy.lazyrestaurant.Models.RestaurantList;
import com.gymlazy.lazyrestaurant.R;
import com.gymlazy.lazyrestaurant.YelpAPI;
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
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
    private LinearLayout mResNameDetailLayout;
    private Restaurant mRestaurant;
    private TextView mResName;
    private TextView mResAddress;
    private RatingBar mFoodRating;
    private TextView mPriceRating;
    private TextView mWorkingHours;
    private LinearLayout mResInfoDetail;


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

        mResName = (TextView) v.findViewById(R.id.res_name_detail);
        mResAddress = (TextView) v.findViewById(R.id.res_address_detail);
        mFoodRating = (RatingBar) v.findViewById(R.id.res_rating_detail);
        mPriceRating = (TextView) v.findViewById(R.id.res_price_rating_detail);
        mWorkingHours = (TextView) v.findViewById(R.id.res_working_hours_detail);
        mResInfoDetail = (LinearLayout) v.findViewById(R.id.res_info_detail);
        mResInfoDetail.setVisibility(View.GONE);
        mResNameDetailLayout = (LinearLayout) v.findViewById(R.id.res_name_detail_layout);
        mResNameDetailLayout.setVisibility(View.GONE);

        return v;
    }

    public void setupAdapter() {
        mViewAdapter = new ViewAdapter(this.getContext(), mRestaurant);
        mViewPager.setAdapter(mViewAdapter);
        mDotsIndicator.setViewPager(mViewPager);
    }

    /**
     * get working hours of a restaurant
     * @param arlWorkingHours
     * @return
     * @throws ParseException
     */
    public String getWorkingHourSchedule(ArrayList<ResHours> arlWorkingHours) throws ParseException {
        ArrayList<String> arlWorkingHourString = new ArrayList<>();
        String sWorkingHour = "";

        for(ResHours t : arlWorkingHours){
            arlWorkingHourString.add(t.getWorkingHours());
        }

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            sWorkingHour = String.join("\n", arlWorkingHourString);
        } else {
            sWorkingHour = TextUtils.join("\n", arlWorkingHourString);
        }

        return sWorkingHour;
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
            mRestaurant = restaurant;
            setupAdapter();
            mResName.setText(restaurant.getName());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                mResAddress.setText(String.join(", ", restaurant.getAddress()));
            } else {
                String[] resAddress = restaurant.getAddress();
                String sResAddress = TextUtils.join(", ", resAddress);
                mResAddress.setText(sResAddress);
            }
            try {
                mWorkingHours.setText(getWorkingHourSchedule(restaurant.getResHours()));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            mFoodRating.setRating(restaurant.getRating());
            mPriceRating.setText(restaurant.getPrice());
            mProgressLayout.setVisibility(View.GONE);
            mResNameDetailLayout.setVisibility(View.VISIBLE);
            mViewPager.setVisibility(View.VISIBLE);
            mDotsIndicator.setVisibility(View.VISIBLE);
            mResInfoDetail.setVisibility(View.VISIBLE);


        }
    }
}
