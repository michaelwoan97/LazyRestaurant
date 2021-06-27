package com.gymlazy.lazyrestaurant;

import android.net.Uri;
import android.renderscript.ScriptGroup;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Copyright: GymLazy All rights reserved
 *
 * @author michaelwoan97
 * Creation Date: 2021-06-24
 * Description: YelpAPI
 */
public class YelpAPI {
    public static final String SEARCH_BUSINESS_URL = "https://api.yelp.com/v3/businesses/search";
    public static final String TERM_PARAM = "term";
    public static final String LOCATION_PARAM = "location";
    public static final String LATITUDE_PARAM = "latitude";
    public static final String LONGITUDE_PARAM = "longitude";

    /**
     * create url for searching businesses
     * @param location
     * @param latitude
     * @param longitude
     * @return
     */
    public static String createURL(String location, double latitude, double longitude){
        Uri builtUri = null;

        // check whether coordinate is provided
        if(latitude != 0 && longitude != 0 ){
            builtUri = Uri.parse(SEARCH_BUSINESS_URL)
                    .buildUpon()
                    .appendQueryParameter(LOCATION_PARAM, location)
                    .appendQueryParameter(LATITUDE_PARAM, Double.toString(latitude))
                    .appendQueryParameter(LONGITUDE_PARAM, Double.toString(longitude))
                    .build();
        } else {
            builtUri = Uri.parse(SEARCH_BUSINESS_URL)
                    .buildUpon()
                    .appendQueryParameter(LOCATION_PARAM, location)
                    .build();
        }


        return builtUri.toString();
    }



    public static JSONObject searchBusinesses(String sUrl) throws IOException, JSONException {
        URL url = new URL(sUrl);
        JSONObject jsonResponse = null;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

        // set up headers
        String basicAuth = "Bearer " + "syU_6A3nvWzH0lnyht-O7UwpqX74r02Acrfn21u6HrngunUScHZdVd324NU-rWb8QjDkpjtxO0jQGJ16KkH9T62UXDAOcg2sceQk243e_nELj3u0rFFeJJO9lrG3YHYx";
        urlConnection.setRequestProperty ("Authorization", basicAuth);
        urlConnection.setRequestMethod("GET");

        try {
            InputStream in = urlConnection.getInputStream();

            // check whether the response is OK
            if(urlConnection.getResponseCode() != HttpURLConnection.HTTP_OK){
                throw new IOException(urlConnection.getResponseMessage() +
                        ": with " +
                        url);
            }

            // loop through the stream of byte and write to the output stream
            int byteRead = 0;
            byte[] buffer = new byte[1024];
            while((byteRead = in.read(buffer)) > 0){
                out.write(buffer, 0, byteRead);
            }
            out.close();

        } finally {
            urlConnection.disconnect();
        }

        return (jsonResponse = new JSONObject(out.toString()));
    }

    // parse items

}
