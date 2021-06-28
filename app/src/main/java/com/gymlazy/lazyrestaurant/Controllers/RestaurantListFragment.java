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
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
    private ImageView mImageView;
    private TextView mResName;
    private TextView mResTitle;
    private RatingBar mRatingBar;
    private TextView mReviewCount;
    private RecyclerView mRecyclerView;
    private RestaurantAdapter mRestaurantAdapter;
    private List<Restaurant> mRestaurantList;
    private static final String TAG = "RestaurantListFragment";

    private ThumbnailDownloader<RestaurantViewHolder> mThumbnailDownloader;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new YelpRequest(RestaurantListFragment.this.getContext()).execute("Waterloo");

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

        return v;
    }

    private class RestaurantViewHolder extends RecyclerView.ViewHolder{
        private ImageView mResImg;
        private TextView mResName;
        private TextView mResTitle;
        private RatingBar mResStar;
        private TextView mResViewCount;

        public RestaurantViewHolder(LayoutInflater layoutInflater, ViewGroup parent) {
            super(layoutInflater.inflate(R.layout.restaurant_layout, parent, false));

            mResImg = itemView.findViewById(R.id.res_image);
            mResName = itemView.findViewById(R.id.res_name);
            mResTitle = itemView.findViewById(R.id.res_title);
            mResStar = itemView.findViewById(R.id.res_rating);
            mResViewCount = itemView.findViewById(R.id.res_review_count);

        }

        public void bind(Restaurant restaurant){
            mResName.setText(restaurant.getName());
            mResTitle.setText(restaurant.getTitle());
            mResStar.setRating(restaurant.getRating());
            mResViewCount.setText(getString(R.string.rating_label, restaurant.getRating(), restaurant.getReviewCount()));
        }

        public void bindDrawable(Drawable drawable) {
            mResImg.setImageDrawable(drawable);
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

    private class YelpRequest extends AsyncTask<String, Void, List<Restaurant>> {
        private Context mContext;

        public YelpRequest(Context context) {
            mContext = context;
        }

        @Override
        protected List<Restaurant> doInBackground(String... strings) {
            RestaurantList restaurantList = RestaurantList.get(mContext);
            List<Restaurant> restaurants = null;

            try {
                restaurants = restaurantList.fetchRestaurants(strings[0],0,0);

                for(Restaurant res : restaurants){
                    Log.d(TAG, res.getName() + "\n");
                }


            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return restaurants;
        }

        @Override
        protected void onPostExecute(List<Restaurant> restaurants) {
            mRestaurantList = restaurants;
            setupAdapter();
        }
    }

}