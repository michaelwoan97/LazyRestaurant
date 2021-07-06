package com.gymlazy.lazyrestaurant.Controllers;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.SweepGradient;
import android.graphics.drawable.Drawable;
import android.media.Rating;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.gymlazy.lazyrestaurant.MapsActivity;
import com.gymlazy.lazyrestaurant.Models.Database.FavResDAO;
import com.gymlazy.lazyrestaurant.Models.Database.RestaurantEntity;
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
    private static final String RES_URL = "restaurant_url";
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
    private FloatingActionButton mFavBtn;
    private FloatingActionButton mLocationBtn;

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
        setHasOptionsMenu(true);
        String sResID = getArguments().getString(RES_ID_KEY).toString();
        new YelpRequest(this.getContext()).execute(sResID);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        menu.clear();

        inflater.inflate(R.menu.res_detail_menu, menu);

        super.onCreateOptionsMenu(menu, inflater);
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
        mFavBtn = (FloatingActionButton) v.findViewById(R.id.res_favorite_detail_button);
        mLocationBtn = (FloatingActionButton) v.findViewById(R.id.res_location_detail_button);
        mFavBtn.hide();
        mLocationBtn.hide();

        return v;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.share_detail:
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, mRestaurant.getResWebURL());
                sendIntent.setType("text/plain");

                Intent shareIntent = Intent.createChooser(sendIntent, null);
                startActivity(shareIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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
            sWorkingHour = String.join("\n\n", arlWorkingHourString);
        } else {
            sWorkingHour = TextUtils.join("\n\n", arlWorkingHourString);
        }

        return sWorkingHour;
    }

    private class YelpRequest extends AsyncTask<String, Void, Restaurant>{
        private Context mContext;
        private RestaurantList restaurantList;
        public YelpRequest(Context packageContext){
            mContext = packageContext;
        }

        @Override
        protected Restaurant doInBackground(String... strings) {
            String sResId = strings[0];
            restaurantList = RestaurantList.get(mContext);
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
//            FavResDAO favResDAO = restaurantList.getDatabase().getFavResDAO();
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
            mFavBtn.setImageDrawable(mRestaurant.isFavorite() ? ContextCompat.getDrawable(mContext, R.drawable.ic_baseline_favorite_24) : ContextCompat.getDrawable(mContext,R.drawable.ic_baseline_favorite_border_24));
            mFavBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean isFav = !mRestaurant.isFavorite();
                    if(isFav){
                        mRestaurant.setFavorite(isFav);
                        new AddFavoriteRequest(mContext).execute(restaurant);
                        mFavBtn.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_baseline_favorite_24));
                    } else {
                        mRestaurant.setFavorite(false);
                        new RemoveFavoriteRequest(mContext).execute(restaurant);
                        mFavBtn.setImageDrawable(ContextCompat.getDrawable(mContext,R.drawable.ic_baseline_favorite_border_24));
                    }
                }
            });
            mLocationBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = MapsActivity.newIntent(mContext, mRestaurant.getCoordinates());
                    startActivity(i);
                    ResDetailFragment.this.getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }
            });
            mProgressLayout.setVisibility(View.GONE);
            mResNameDetailLayout.setVisibility(View.VISIBLE);
            mViewPager.setVisibility(View.VISIBLE);
            mDotsIndicator.setVisibility(View.VISIBLE);
            mResInfoDetail.setVisibility(View.VISIBLE);
            mFavBtn.show();
            mLocationBtn.show();

        }
    }

    private class AddFavoriteRequest extends AsyncTask<Restaurant,Void,Void>{
        private Context mContext;
        private RestaurantList restaurantList;
        public AddFavoriteRequest(Context packageContext){
            mContext = packageContext;
        }

        @Override
        protected Void doInBackground(Restaurant... restaurants) {
            restaurantList = RestaurantList.get(mContext);
            FavResDAO favResDAO = restaurantList.getDatabase().getFavResDAO();

            RestaurantEntity restaurantEntity = new RestaurantEntity();
            restaurantEntity.setId(restaurants[0].getId());
            restaurantEntity.setName(restaurants[0].getName());

            favResDAO.insert(restaurantEntity);
            restaurants[0].setFavorite(true);
            restaurantList.updateFavoriteRestaurants(restaurants[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Toast.makeText(mContext, "Added to Favorite List!", Toast.LENGTH_SHORT).show();
        }
    }

    private class RemoveFavoriteRequest extends AsyncTask<Restaurant,Void,Void>{
        private Context mContext;
        private RestaurantList restaurantList;
        public RemoveFavoriteRequest(Context packageContext){
            mContext = packageContext;
        }

        @Override
        protected Void doInBackground(Restaurant... restaurants) {
            restaurantList = RestaurantList.get(mContext);
            FavResDAO favResDAO = restaurantList.getDatabase().getFavResDAO();

            RestaurantEntity restaurantEntity = new RestaurantEntity();
            restaurantEntity.setId(restaurants[0].getId());
            restaurantEntity.setName(restaurants[0].getName());

            favResDAO.delete(restaurantEntity);
            restaurants[0].setFavorite(false);
            restaurantList.updateFavoriteRestaurants(restaurants[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Toast.makeText(mContext, "Removed out of Favorite List!", Toast.LENGTH_SHORT).show();
        }
    }
}
