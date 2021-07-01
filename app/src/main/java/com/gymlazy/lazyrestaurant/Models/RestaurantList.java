package com.gymlazy.lazyrestaurant.Models;

import android.content.Context;

import androidx.room.Room;

import com.gymlazy.lazyrestaurant.Models.Database.FavResDAO;
import com.gymlazy.lazyrestaurant.Models.Database.FavResDatabase;
import com.gymlazy.lazyrestaurant.Models.Database.RestaurantEntity;
import com.gymlazy.lazyrestaurant.YelpAPI;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Copyright: GymLazy All rights reserved
 *
 * @author michaelwoan97
 * Creation Date: 2021-06-25
 * Description: RestaurantList
 */
public class RestaurantList {
    private static RestaurantList sRestaurantList;
    private List<Restaurant> mRestaurants;
    private List<Restaurant> mFavRestaurants;
    private Context mContext;

    public static RestaurantList get(Context context){
        // check whether the singleton is exits
        if(sRestaurantList == null){
            sRestaurantList = new RestaurantList(context);
        }

        return sRestaurantList;
    }

    private RestaurantList(Context context) {
        mContext = context;
    }

    public List<Restaurant> getRestaurants() {
        return mRestaurants;
    }

    public void setRestaurants(List<Restaurant> restaurants) {
        mRestaurants = restaurants;
    }

    /**
     * fetch restaurants from yelp api
     * @param sLocation
     * @param latitude
     * @param longitude
     * @return
     * @throws IOException
     * @throws JSONException
     */
    public List<Restaurant> fetchRestaurants(String sLocation, double latitude, double longitude) throws IOException, JSONException {
        List<Restaurant> restaurants = new ArrayList<Restaurant>();
        String sUrl = YelpAPI.createURL(sLocation, latitude, longitude);
        JSONObject response = YelpAPI.searchBusinesses(sUrl);
        parseItems(restaurants, response);
        return (mRestaurants = restaurants);
    }


    /**
     * fetch all favorite restaurants from database (can only use in background thread)
     * @param database
     * @return
     */
    public List<RestaurantEntity> fetchFavoriteRestaurants(FavResDatabase database){
        FavResDAO favResDAO = database.getFavResDAO();

        // fetch all the favorite restaurants
        List<RestaurantEntity> restaurantEntities = favResDAO.getRestaurants();
        return restaurantEntities;
    }

    /**
     * fetch favorite restaurants with more info
     * @param favRestaurantEntities
     * @return
     * @throws IOException
     * @throws JSONException
     */
    public List<Restaurant> fetchFavoriteRestaurants(List<RestaurantEntity> favRestaurantEntities) throws IOException, JSONException {
        List<Restaurant> favRestaurants = new ArrayList<>();
        for(RestaurantEntity res : favRestaurantEntities){
            String url = YelpAPI.createURLBusinessDetail(res.getId());
            JSONObject joResponse = YelpAPI.searchBusinesses(url);

            Restaurant restaurant = new Restaurant();
            restaurant.setId(joResponse.getString("id"));
            restaurant.setName(joResponse.getString("name"));
            restaurant.setImgURL(joResponse.getString("image_url"));
            restaurant.setTitle(joResponse.getJSONArray("categories").getJSONObject(0).getString("title"));
            restaurant.setRating((float) joResponse.getDouble("rating"));
            restaurant.setReviewCount(joResponse.getInt("review_count"));
            restaurant.setAddress(new String[]{joResponse.getJSONObject("location").getJSONArray("display_address").getString(0),joResponse.getJSONObject("location").getJSONArray("display_address").getString(1)});
            restaurant.setPhone(joResponse.getString("display_phone"));
            restaurant.setCoordinates(new double[]{joResponse.getJSONObject("coordinates").getDouble("latitude"), joResponse.getJSONObject("coordinates").getDouble("longitude")});

            restaurant.setFavorite(true);

            favRestaurants.add(restaurant);
        }

        return (mFavRestaurants = favRestaurants);
    }

    public void parseItems(List<Restaurant> lRestaurant, JSONObject jsonBody) throws JSONException {
        JSONArray jaBusinesses = jsonBody.getJSONArray("businesses");

        for(int i = 0; i < jaBusinesses.length(); i++){
            JSONObject joRestaurant = jaBusinesses.getJSONObject(i);

            Restaurant restaurant = new Restaurant();
            restaurant.setId(joRestaurant.getString("id"));
            restaurant.setName(joRestaurant.getString("name"));
            restaurant.setImgURL(joRestaurant.getString("image_url"));
            restaurant.setTitle(joRestaurant.getJSONArray("categories").getJSONObject(0).getString("title"));
            restaurant.setRating((float) joRestaurant.getDouble("rating"));
            restaurant.setReviewCount(joRestaurant.getInt("review_count"));
            restaurant.setAddress(new String[]{joRestaurant.getJSONObject("location").getJSONArray("display_address").getString(0),joRestaurant.getJSONObject("location").getJSONArray("display_address").getString(1)});
            restaurant.setPhone(joRestaurant.getString("display_phone"));
            restaurant.setCoordinates(new double[]{joRestaurant.getJSONObject("coordinates").getDouble("latitude"), joRestaurant.getJSONObject("coordinates").getDouble("longitude")});
            restaurant.setDistance(joRestaurant.getDouble("distance"));

            // check whether the object has favorite field
            if(!joRestaurant.has("is_favorite")){
                restaurant.setFavorite(false);
            }

            lRestaurant.add(restaurant);
        }

    }
}
