package com.gymlazy.lazyrestaurant.Controllers;

import androidx.fragment.app.Fragment;

/**
 * Copyright: GymLazy All rights reserved
 *
 * @author michaelwoan97
 * Creation Date: 2021-06-26
 * Description: RestaurantListActivity
 */
public class RestaurantListActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        return new RestaurantListFragment();
    }
}
