package com.cenah.buymeserver.models;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;

import com.cenah.buymeserver.retrofit.IGeoCoordinates;
import com.cenah.buymeserver.retrofit.RetrofitClient;

public class Common {

    public static User currnetUser;
    public static Request currentRquest;


    public static final String UPDATE = "Update";
    public static final String DELETE = "Delete";

    public static final String baseUrl = "https://maps.googleapis.com";

    public static IGeoCoordinates getGeoCodeService() {
        return RetrofitClient.getClient(baseUrl).create(IGeoCoordinates.class);
    }

    public static Bitmap scaleBitmap(Bitmap bitmap, int newWidth, int newHeight) {
        Bitmap scaledBitmap = Bitmap.createBitmap(newWidth, newHeight, Bitmap.Config.ARGB_8888);

        float scaleX = newWidth / (float) bitmap.getWidth();
        float scaleY = newHeight / (float) bitmap.getHeight();
        float pivotX = 0,pivotY=0;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(scaleX,scaleY,pivotX,pivotY);

        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bitmap,0,0,new Paint(Paint.FILTER_BITMAP_FLAG));


        return scaledBitmap;
    }




}
