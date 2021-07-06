package com.gymlazy.lazyrestaurant.Models;

import java.util.ArrayList;

/**
 * Copyright: GymLazy All rights reserved
 *
 * @author michaelwoan97
 * Creation Date: 2021-06-25
 * Description: Restaurant
 */
public class Restaurant {
    private String mId;
    private String mName;
    private String mResWebURL;
    private String mImgURL;
    private String mTitle;
    private Float mRating;
    private int mReviewCount;
    private String[] mAddress = new String[2];
    private String mPhone;
    private double[] mCoordinates = new double[2];
    private double mDistance;
    private boolean mIsFavorite;

    private ArrayList<String> mPhotos;
    private String mPrice;
    private ArrayList<ResHours> mResHours;

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getResWebURL() {
        return mResWebURL;
    }

    public void setResWebURL(String resWebURL) {
        mResWebURL = resWebURL;
    }

    public String getImgURL() {
        return mImgURL;
    }

    public void setImgURL(String imgURL) {
        mImgURL = imgURL;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public Float getRating() {
        return mRating;
    }

    public void setRating(Float rating) {
        mRating = rating;
    }

    public int getReviewCount() {
        return mReviewCount;
    }

    public void setReviewCount(int reviewCount) {
        mReviewCount = reviewCount;
    }

    public String[] getAddress() {
        return mAddress;
    }

    public void setAddress(String[] address) {
        mAddress = address;
    }

    public String getPhone() {
        return mPhone;
    }

    public void setPhone(String phone) {
        mPhone = phone;
    }

    public double[] getCoordinates() {
        return mCoordinates;
    }

    public void setCoordinates(double[] coordinates) {
        mCoordinates = coordinates;
    }

    public double getDistance() {
        return mDistance;
    }

    public void setDistance(double distance) {
        mDistance = distance;
    }

    public boolean isFavorite() {
        return mIsFavorite;
    }

    public void setFavorite(boolean favorite) {
        mIsFavorite = favorite;
    }

    public ArrayList<String> getPhotos() {
        return mPhotos;
    }

    public void setPhotos(ArrayList<String> photos) {
        mPhotos = photos;
    }

    public String getPrice() {
        return mPrice;
    }

    public void setPrice(String price) {
        mPrice = price;
    }

    public ArrayList<ResHours> getResHours() {
        return mResHours;
    }

    public void setResHours(ArrayList<ResHours> resHours) {
        mResHours = resHours;
    }
}
