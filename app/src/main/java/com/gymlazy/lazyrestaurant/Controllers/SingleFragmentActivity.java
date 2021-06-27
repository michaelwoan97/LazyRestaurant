/*
*	PROJECT: Trip Planner
*	FILE: SingleFragmentActivity.java
*	PROGRAMMER: Nghia Nguyen
*	FIRST VERSION: 2021/05/06
*	DESCRIPTION:
		This file contains the SingleFragmentActivity class which is the base class for almost of the classes in the Trip Planner application
*/

package com.gymlazy.lazyrestaurant.Controllers;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.gymlazy.lazyrestaurant.R;

public abstract class SingleFragmentActivity extends AppCompatActivity {

    /* abstract method to return an instance of the fragment that
    *  the activity is hosting on. when a class is inherited from this
    *  abstract class, it must override this method to return a different fragment
    * */
    protected abstract Fragment createFragment();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment);

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.fragment_container);

        if (fragment == null) {
            fragment = createFragment();
            fm.beginTransaction()
                    .add(R.id.fragment_container, fragment)
                    .commit();
        }
    }
}
