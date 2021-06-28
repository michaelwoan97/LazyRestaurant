/*
*	PROJECT: Trip Planner
*	FILE: ImageDownloader.java
*	PROGRAMMER: Nghia Nguyen
*	FIRST VERSION: 2021/05/06
*	DESCRIPTION:
		This file contains the ImageDownloader class which is a util class used for downloading the images from the URLs
*/

package com.gymlazy.lazyrestaurant.Utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class ImageDownloader {

    /**
     * The purpose of this function is used for downloading a image from the given url
     * @param imgURL
     * @return
     * @throws IOException
     */
    public static byte[] downloadImgFromURL(String imgURL) throws IOException {
        URL url = new URL(imgURL);
        Bitmap bmp = null;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] bOutImg = null;

        // connect to the url and download the image
        HttpsURLConnection connection = (HttpsURLConnection)url.openConnection();
        try{
            InputStream in = connection.getInputStream();

            // check whether the response is OK
            if(connection.getResponseCode() != HttpURLConnection.HTTP_OK){
                throw new IOException(connection.getResponseMessage() +
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
            connection.disconnect();
        }

        // convert byte array to bitmap
        bOutImg = out.toByteArray();
        return bOutImg;
    }

    /**
     * get the image name from the url
     * @param imgURL
     * @param root
     * @return
     */
    public static String getImageNameFromURL(String imgURL, String root) {
        // get the filename
        int iLastIndexOfDelim = imgURL.lastIndexOf('/');
        String sFileName = imgURL.substring(iLastIndexOfDelim);
        return root + "/" + sFileName;
    }

}
