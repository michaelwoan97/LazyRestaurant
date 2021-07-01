package com.gymlazy.lazyrestaurant.Controllers;

import android.content.Context;
import android.content.Intent;
import android.view.MenuItem;

import androidx.fragment.app.Fragment;

import com.gymlazy.lazyrestaurant.R;

/**
 * Copyright: GymLazy All rights reserved
 *
 * @author michaelwoan97
 * Creation Date: 2021-06-26
 * Description: RestaurantListActivity
 */
public class RestaurantListActivity extends SingleFragmentActivity {
    private static final String EXTRA_REQUEST_CODE = "com.gymlazy.lazyrestaurant.Controllers.RestauratnListActivity.request_code";

    @Override
    protected Fragment createFragment() {
        int requestCode = (int) getIntent().getSerializableExtra(EXTRA_REQUEST_CODE);
        return RestaurantListFragment.newInstance(requestCode);
    }

    @Override
    public void finish() {
        super.finish();
        this.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // Respond to the action bar's Up/Home button
                super.onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    public static Intent newIntent(Context packageContext, int requestCode){
        Intent i = new Intent(packageContext, RestaurantListActivity.class);
        i.putExtra(EXTRA_REQUEST_CODE, requestCode);
        return i;
    }

}
