package com.pavel.databaseapp.services;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCaller;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import javax.annotation.Nullable;

public class ImagePickerService{
    private ActivityResultLauncher<Intent> loadImageLauncher;
    private Bitmap bitmap;
    private Uri imageUri;
    private MutableLiveData<Bitmap> bitmapLiveData;
    private Context context;
    public ImagePickerService(Context context, ActivityResultCaller caller){
        this.context = context;
        this.bitmapLiveData = new MutableLiveData<>();
        this.loadImageLauncher = caller.registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if(result.getResultCode() == Activity.RESULT_OK){
                        try{
                            imageUri = result.getData().getData();
                            if(imageUri == null) return;
                            bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(),imageUri);
                            if(bitmapLiveData != null)
                                bitmapLiveData.postValue(bitmap);
                        }catch (Exception e){
                            Toast.makeText(context,
                                    "Cant upload Image => " + e, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    public void launchImage(Intent intent){
        this.loadImageLauncher.launch(intent);
    }

    public MutableLiveData<Bitmap> getBitmapLiveData() {
        return bitmapLiveData;
    }

    public Uri getImageUri() {
        return imageUri;
    }

    public void setImageUri(Uri imageUri) {
        this.imageUri = imageUri;
    }

    public Context getContext() {
        return context;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

}
