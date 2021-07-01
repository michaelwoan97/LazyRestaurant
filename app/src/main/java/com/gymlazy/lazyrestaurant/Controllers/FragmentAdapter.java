package com.gymlazy.lazyrestaurant.Controllers;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

/**
 * Copyright: GymLazy All rights reserved
 *
 * @author michaelwoan97
 * Creation Date: 2021-06-07
 * Description: FragmentAdapter
 */
public class FragmentAdapter extends FragmentStateAdapter {
    private static final int mFavCode = 1;
    public FragmentAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {

        if(position == 1){
            return RestaurantListFragment.newInstance(mFavCode);
        }

        return new HomeFragment();
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
