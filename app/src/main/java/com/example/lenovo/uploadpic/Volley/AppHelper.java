package com.example.lenovo.uploadpic.Volley;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;

/**
 * Sketch Project Studio
 * Created by Angga on 12/04/2016 14.27.
 */
public class AppHelper {    
    
    /**
     * Turn drawable resource into byte array.
     *
     * @param context parent context
     * @param id      drawable resource id
     * @return byte array
     */
    public static byte[] getFileDataFromDrawable(Context context, int id) {
        Drawable drawable = ContextCompat.getDrawable(context, id);
        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    /**
     * Turn drawable into byte array.
     *
     * @param drawable data
     * @return byte array
     */
    public static byte[] getFileDataFromDrawable(Context context, Drawable drawable) {
        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    /**
     * Turn videopath to byte array
     * @return byte array
     */

    public  static  byte[] getFileDataFromDrawable(String getpath){
        ByteArrayOutputStream byteArrayOutputStream=null;
        FileInputStream fileInputStream;
        try{
            byteArrayOutputStream=new ByteArrayOutputStream();
            fileInputStream=new FileInputStream(new File(getpath));
            byte[] buff=new byte[4078*1024];
            int n;
            while(-1!=(n= fileInputStream.read(buff))){
                byteArrayOutputStream.write(buff,0,n);
            }

        }catch (Exception e){
            Log.e("Apphelper","Excption Occured");
        }

        return byteArrayOutputStream.toByteArray();
    }
}