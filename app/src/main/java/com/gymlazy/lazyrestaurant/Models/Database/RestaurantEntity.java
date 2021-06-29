package com.gymlazy.lazyrestaurant.Models.Database;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Copyright: GymLazy All rights reserved
 *
 * @author michaelwoan97
 * Creation Date: 2021-06-29
 * Description: RestaurantEntity
 */
@Entity(tableName = "FavoriteRestaurants")
public class RestaurantEntity {
    @PrimaryKey
    @NonNull
    private String id;
    private String name;

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
