package com.gymlazy.lazyrestaurant.Controllers;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.gymlazy.lazyrestaurant.Models.Restaurant;
import com.gymlazy.lazyrestaurant.R;
import com.gymlazy.lazyrestaurant.Utils.ImageDownloader;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Copyright: GymLazy All rights reserved
 *
 * @author michaelwoan97
 * Creation Date: 2021-07-03
 * Description: ViewAdapter
 */
public class ViewAdapter extends PagerAdapter {
    private Restaurant mRestaurant;
    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private String[] mImages;

    public ViewAdapter(Context packageContext, Restaurant restaurant){
        this.mContext = packageContext;
        mRestaurant = restaurant;

        // create array images
        ArrayList<String> resPhotos = restaurant.getPhotos();
        mImages = new String[resPhotos.size()];
        for(int i = 0, count = resPhotos.size(); i < count ; i++){
            mImages[i] = resPhotos.get(i);
        }
    }

    @Override
    public int getCount() {
        return mImages.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }


    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = mLayoutInflater.inflate(R.layout.res_item_img, null);
        ImageView imageView = view.findViewById(R.id.res_img_view);
        new DownloadImageRequest(imageView).execute(mImages[position]);
        ViewPager viewPager = (ViewPager) container;
        viewPager.addView(view, 0);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        ViewPager viewPager = (ViewPager) container;
        View view = (View) object;
        viewPager.removeView(view);
    }

    private class DownloadImageRequest extends AsyncTask<String, Void, byte[]>{
        private ImageView mView;

        public DownloadImageRequest(ImageView view){
            mView = view;
        }

        @Override
        protected byte[] doInBackground(String... strings) {
            byte[] barImg = null;
            try {
                barImg = ImageDownloader.downloadImgFromURL(strings[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return barImg;
        }

        @Override
        protected void onPostExecute(byte[] bytes) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            mView.setImageBitmap(bitmap);
        }
    }
}
