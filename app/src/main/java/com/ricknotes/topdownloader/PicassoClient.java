package com.ricknotes.topdownloader;

import android.content.Context;
import android.util.Log;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import static android.content.ContentValues.TAG;

public class PicassoClient {
    public static void downloadImage (String url, ImageView tvImage){
        if(url != null && url.length()>0){
            Picasso.get().load(url).into(tvImage);
        }else {
            Log.d(TAG, "downloadImage: Failed to load image");
        }
    }
}
