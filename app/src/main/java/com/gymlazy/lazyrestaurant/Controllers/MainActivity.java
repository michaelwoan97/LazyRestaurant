package com.gymlazy.lazyrestaurant.Controllers;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.widget.ViewPager2;

import android.graphics.Color;
import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;
import com.gymlazy.lazyrestaurant.Controllers.FragmentAdapter;
import com.gymlazy.lazyrestaurant.R;

/**
 * Copyright: GymLazy All rights reserved
 *
 * @author michaelwoan97
 * Creation Date: 2021-06-05
 * Description: MainActivity
 */
public class MainActivity extends AppCompatActivity {
    private TabLayout mTabLayout;
    private ViewPager2 mViewPager2;
    private FragmentAdapter mFragmentAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().setElevation(0f);

        mTabLayout = (TabLayout) findViewById(R.id.tab_layout);
        mViewPager2 = (ViewPager2) findViewById(R.id.view_pager2);

        FragmentManager fm = getSupportFragmentManager();
        mFragmentAdapter = new FragmentAdapter(fm, getLifecycle());
        mViewPager2.setAdapter(mFragmentAdapter);

        mTabLayout.addTab(mTabLayout.newTab().setText("HOME").setIcon(R.drawable.ic_baseline_home_24));
        mTabLayout.addTab(mTabLayout.newTab().setText("FAVOURITES").setIcon(R.drawable.ic_baseline_favorite_24));
        mTabLayout.setSelectedTabIndicatorColor(Color.parseColor("#eefbfe"));
        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mViewPager2.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        mViewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                mTabLayout.selectTab(mTabLayout.getTabAt(position));
            }
        });


    }


}