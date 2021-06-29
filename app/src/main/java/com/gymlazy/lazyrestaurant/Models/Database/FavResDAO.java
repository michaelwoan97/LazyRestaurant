package com.gymlazy.lazyrestaurant.Models.Database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.gymlazy.lazyrestaurant.Models.Restaurant;

import java.util.List;

/**
 * Copyright: GymLazy All rights reserved
 *
 * @author michaelwoan97
 * Creation Date: 2021-06-29
 * Description: FavResDAO
 */
@Dao
public interface FavResDAO {
    @Insert
    public void insert(RestaurantEntity... restaurants);

    @Update
    public void update(RestaurantEntity... restaurants);

    @Delete
    public void delete(RestaurantEntity restaurant);

    @Query("SELECT * FROM FavoriteRestaurants")
    public List<RestaurantEntity> getRestaurants();

    @Query("SELECT * FROM FavoriteRestaurants WHERE id = :resID")
    public RestaurantEntity getRestaurantWithId(String resID);
}
