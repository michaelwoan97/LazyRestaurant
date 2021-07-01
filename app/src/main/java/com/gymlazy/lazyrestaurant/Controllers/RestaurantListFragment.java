package com.gymlazy.lazyrestaurant.Controllers;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.Rating;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.gymlazy.lazyrestaurant.Models.Database.FavResDAO;
import com.gymlazy.lazyrestaurant.Models.Database.FavResDatabase;
import com.gymlazy.lazyrestaurant.Models.Database.RestaurantEntity;
import com.gymlazy.lazyrestaurant.Models.Restaurant;
import com.gymlazy.lazyrestaurant.Models.RestaurantList;
import com.gymlazy.lazyrestaurant.R;
import com.gymlazy.lazyrestaurant.YelpAPI;

import org.json.JSONException;
import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Copyright: GymLazy All rights reserved
 *
 * @author michaelwoan97
 * Creation Date: 2021-06-26
 * Description: RestaurantListFragment
 */
public class RestaurantListFragment extends Fragment {
    private static final String REQUEST_CODE = "REQUEST_CODE";
    private static final String LOCATION_KEY = "LOCATION";
    private ImageView mImageView;
    private TextView mResName;
    private TextView mResTitle;
    private RatingBar mRatingBar;
    private TextView mReviewCount;
    private TextView mErrRes;
    private RecyclerView mRecyclerView;
    private LinearLayout mProgressIndicator;
    private RestaurantAdapter mRestaurantAdapter;
    private List<Restaurant> mRestaurantList;
    private TextView mNoFavResLabel;
    private static final String TAG = "RestaurantListFragment";
    private FavResDatabase mFavResDatabase;
    private String mLocation;
    private int mRequestCode;
    private int mNormalRequest = 0;
    private int mFavoriteRequest = 1;

    public int getRequestCode() {
        return mRequestCode;
    }

    public int getNormalRequest() {
        return mNormalRequest;
    }

    public int getFavoriteRequest() {
        return mFavoriteRequest;
    }

    public List<Restaurant> getRestaurantList() {
        return mRestaurantList;
    }

    public void setRestaurantList(List<Restaurant> restaurantList) {
        mRestaurantList = restaurantList;
    }

    private ThumbnailDownloader<RestaurantViewHolder> mThumbnailDownloader;

    public FavResDatabase getFavResDatabase() {
        return mFavResDatabase;
    }

    public static Fragment newInstance(int requestCode, String location){
        Bundle b = new Bundle();
        b.putSerializable(REQUEST_CODE, requestCode);
        b.putSerializable(LOCATION_KEY, location);
        RestaurantListFragment fragment = new RestaurantListFragment();
        fragment.setArguments(b);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRequestCode = (int) getArguments().getSerializable(REQUEST_CODE);
        mLocation = getArguments().getSerializable(LOCATION_KEY).toString();
        new YelpRequest(RestaurantListFragment.this.getContext()).execute(mLocation);

        Handler responseHandler = new Handler(); // attached to main thread
        mThumbnailDownloader = new ThumbnailDownloader<>(responseHandler);
        mThumbnailDownloader.setThumbnailDownloadListener(
                new ThumbnailDownloader.ThumbnailDownloadListener<RestaurantViewHolder>() {
                    @Override
                    public void onThumbnailDownloaded(RestaurantViewHolder target, Bitmap thumbnail) {
                        Drawable drawable = new BitmapDrawable(getResources(), thumbnail);
                        target.bindDrawable(drawable);
                    }
                }
        );
        mThumbnailDownloader.start();
        mThumbnailDownloader.getLooper();
        Log.i(TAG, "Background thread started");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mThumbnailDownloader.quit();
        mThumbnailDownloader.clearQueue();
        Log.i(TAG, "Background thread destroyed");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.restaurant_list_layout, container, false);

        mRecyclerView = (RecyclerView) v.findViewById(R.id.restaurant_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setVisibility(View.GONE);
        mProgressIndicator = v.findViewById(R.id.progress_indicator);
        mNoFavResLabel = (TextView) v.findViewById(R.id.no_fav_res);
        mNoFavResLabel.setVisibility(View.GONE);
        mErrRes = (TextView) v.findViewById(R.id.err_restaurants);
        mErrRes.setVisibility(View.GONE);

        return v;
    }



    private class RestaurantViewHolder extends RecyclerView.ViewHolder{
        private ImageView mResImg;
        private TextView mResName;
        private TextView mResTitle;
        private RatingBar mResStar;
        private TextView mResViewCount;
        private ImageButton mFavBtn;

        public RestaurantViewHolder(LayoutInflater layoutInflater, ViewGroup parent) {
            super(layoutInflater.inflate(R.layout.restaurant_layout, parent, false));

            mResImg = itemView.findViewById(R.id.res_image);
            mResName = itemView.findViewById(R.id.res_name);
            mResTitle = itemView.findViewById(R.id.res_title);
            mResStar = itemView.findViewById(R.id.res_rating);
            mResViewCount = itemView.findViewById(R.id.res_review_count);
            mFavBtn = itemView.findViewById(R.id.res_fav_btn);


        }

        public void bind(Restaurant restaurant){
            mResName.setText(restaurant.getName());
            mResTitle.setText(restaurant.getTitle());
            mResStar.setRating(restaurant.getRating());
            mResViewCount.setText(getString(R.string.rating_label, restaurant.getRating(), restaurant.getReviewCount()));
            mFavBtn.setSelected(restaurant.isFavorite());
            mFavBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(v.isSelected()){
                        v.setSelected(false);
                        new RemoveFavoriteRequest().execute(restaurant);
                        Toast.makeText(itemView.getContext(), "Removed out of Favorite List", Toast.LENGTH_SHORT).show();
                    } else {
                        v.setSelected(true);
                        new AddFavoriteRequest().execute(restaurant);
                        Toast.makeText(itemView.getContext(), "Added to Favorite List", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        public void bindDrawable(Drawable drawable) {
            mResImg.setImageDrawable(drawable);
        }

        private class AddFavoriteRequest extends AsyncTask<Restaurant,Void,Void>{

            @Override
            protected Void doInBackground(Restaurant... restaurants) {
                FavResDAO favResDAO = getFavResDatabase().getFavResDAO();

                RestaurantEntity restaurantEntity = new RestaurantEntity();
                restaurantEntity.setId(restaurants[0].getId());
                restaurantEntity.setName(restaurants[0].getName());

                favResDAO.insert(restaurantEntity);
                return null;
            }
        }

        private class RemoveFavoriteRequest extends AsyncTask<Restaurant,Void,Restaurant>{

            @Override
            protected Restaurant doInBackground(Restaurant... restaurants) {
                FavResDAO favResDAO = getFavResDatabase().getFavResDAO();

                RestaurantEntity restaurantEntity = new RestaurantEntity();
                restaurantEntity.setId(restaurants[0].getId());
                restaurantEntity.setName(restaurants[0].getName());

                favResDAO.delete(restaurantEntity);
                return restaurants[0];
            }

            @Override
            protected void onPostExecute(Restaurant restaurant) {
                if(getRequestCode() == mFavoriteRequest){
                    List<Restaurant> favRestaurants = getRestaurantList();
                    int positionRemove = favRestaurants.indexOf(restaurant);
                    favRestaurants.remove(restaurant);
                    refreshAdapter(positionRemove);

                    // check whether the favorite restaurants is empty
                    if(getRequestCode() == getFavoriteRequest()){
                        if(mRestaurantList.isEmpty()){
                            mProgressIndicator.setVisibility(ViewGroup.GONE);
                            mRecyclerView.setVisibility(View.GONE);
                            mNoFavResLabel.setVisibility(View.VISIBLE);
                            return;
                        }
                    }
                }
            }
        }
    }

    private class RestaurantAdapter extends RecyclerView.Adapter<RestaurantViewHolder>{
        private List<Restaurant> mRestaurantList;

        public RestaurantAdapter(List<Restaurant> restaurantList) {
            mRestaurantList = restaurantList;
        }

        @NonNull
        @Override
        public RestaurantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());

            return new RestaurantViewHolder(inflater, parent);
        }

        @Override
        public void onBindViewHolder(@NonNull RestaurantViewHolder holder, int position) {
            Restaurant restaurant = mRestaurantList.get(position);
            holder.bind(restaurant);
            mThumbnailDownloader.queueThumbnail(holder, restaurant.getImgURL());
        }

        @Override
        public int getItemCount() {
            return mRestaurantList.size();
        }
    }

    private void setupAdapter() {
        //This confirms that the fragment has been attached to an activity, and in turn that getActivity() will not be null.
        if (isAdded()) {
            mRestaurantAdapter = new RestaurantAdapter(mRestaurantList);
            mRecyclerView.setAdapter(mRestaurantAdapter);
            mRestaurantAdapter.notifyDataSetChanged();
        }
    }

    private void refreshAdapter(int position){
        if(mRestaurantAdapter != null){
            mRestaurantAdapter.notifyItemRemoved(position);
        }
    }

    private class YelpRequest extends AsyncTask<String, Void, List<Restaurant>> {
        private Context mContext;
        private FavResDatabase mDatabase;

        public YelpRequest(Context context) {
            mContext = context;
        }

        @Override
        protected List<Restaurant> doInBackground(String... strings) {
            mDatabase = Room.databaseBuilder(mContext, FavResDatabase.class, "FavResDB")
                    .build();

            RestaurantList restaurantList = RestaurantList.get(mContext);
            List<Restaurant> restaurants = null;

            // check whether fetching restaurants for favorite fragment or restaurant list fragment
            if(getRequestCode() == getNormalRequest()){
                try {
                    restaurants = restaurantList.fetchRestaurants(strings[0],0,0);

                    // check whether the restaurant is empty
                    if(restaurants.isEmpty() || restaurants == null){
                        return null;
                    }
                    for(Restaurant res : restaurants){
                        Log.d(TAG, res.getName() + "\n");
                    }


                } catch (IOException e) {
                    e.printStackTrace();
                    return null;

                } catch (JSONException e) {
                    e.printStackTrace();
                    return null;
                }

                // check whether there are favorite restaurants
                List<RestaurantEntity> favRestaurants = restaurantList.fetchFavoriteRestaurants(mDatabase);
                for(Restaurant res : restaurants){
                    for(RestaurantEntity en : favRestaurants){
                        String sEnId = en.getId();
                        String sResId = res.getId();
                        if(sResId.equals(sEnId)){
                            res.setFavorite(true);
                        }
                    }
                }
            } else {
                List<RestaurantEntity> favRestaurants = restaurantList.fetchFavoriteRestaurants(mDatabase);
                try {
                    restaurants = restaurantList.fetchFavoriteRestaurants(favRestaurants);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            return restaurants;
        }

        @Override
        protected void onPostExecute(List<Restaurant> restaurants) {
            if(restaurants == null){
                mProgressIndicator.setVisibility(View.GONE);
                mErrRes.setVisibility(View.VISIBLE);
                return;
            }
            mRestaurantList = restaurants;
            mFavResDatabase = mDatabase;

            if(getRequestCode() == getFavoriteRequest()){
                if(mRestaurantList.isEmpty()){
                    mProgressIndicator.setVisibility(ViewGroup.GONE);
                    mNoFavResLabel.setVisibility(View.VISIBLE);
                    return;
                }
            }

            setupAdapter();

            // hide progress bar and display hotel data
            mProgressIndicator.setVisibility(ViewGroup.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);
        }
    }

}
