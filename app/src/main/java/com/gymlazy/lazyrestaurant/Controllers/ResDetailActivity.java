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
 * Creation Date: 2021-07-02
 * Description: ResDetailActivity
 */
public class ResDetailActivity extends SingleFragmentActivity {
    private static final String EXTRA_RES_ID = "com.gymlazy.lazyrestaurant.Controllers.res_id";

    @Override
    protected Fragment createFragment() {
        String sResId = getIntent().getSerializableExtra(EXTRA_RES_ID).toString();
        return ResDetailFragment.newInstance(sResId);
    }

    public static Intent newIntent(Context packakgeContext, String sResID) {
        Intent i = new Intent(packakgeContext, ResDetailActivity.class);
        i.putExtra(EXTRA_RES_ID, sResID);
        return i;
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
}
