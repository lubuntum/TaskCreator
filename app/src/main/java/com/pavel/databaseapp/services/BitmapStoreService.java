package com.pavel.databaseapp.services;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import com.pavel.databaseapp.settings.SettingsViewModel;

import java.io.ByteArrayOutputStream;

public class BitmapStoreService {
    SharedPreferences preferences;

    public BitmapStoreService(Context context){
        preferences =
                context.getSharedPreferences(SettingsViewModel.SETTINGS_STORAGE,Context.MODE_PRIVATE);
    }
    public void saveBitmap(String key, Bitmap bitmap){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,baos);
        byte [] byteArray = baos.toByteArray();
        String encoded = encodeBitmap(bitmap);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key,encoded);
        editor.apply();
    }
    public Bitmap getBitMapByKey(String key){
        String encoded = preferences.getString(key,null);
        if(encoded == null) return null;
        return decodeBitmap(encoded);
    }
    /*Перевод изображение в строковый вид для передачи*/
    public static String encodeBitmap(Bitmap bitmap){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,baos);
        byte[] byteArray = baos.toByteArray();
        return Base64.encodeToString(byteArray,Base64.DEFAULT);
    }
    /*Обратное декодирование строки в изображение*/
    public static Bitmap decodeBitmap(String encode){
        byte[] byteArray = Base64.decode(encode,Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(byteArray,0,byteArray.length);
    }
}
