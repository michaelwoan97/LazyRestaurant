package com.gymlazy.lazyrestaurant.Models.Database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

/**
 * Copyright: GymLazy All rights reserved
 *
 * @author michaelwoan97
 * Creation Date: 2021-06-29
 * Description: FavResDatabase
 */
@Database(entities = {RestaurantEntity.class}, version = 1)
public abstract class FavResDatabase extends RoomDatabase {
    public abstract FavResDAO getFavResDAO();
}
