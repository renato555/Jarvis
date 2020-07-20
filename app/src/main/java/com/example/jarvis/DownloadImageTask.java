package com.example.jarvis;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;

public class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {

    private ImageView imageView;

    public DownloadImageTask(ImageView image){
        imageView = image;
    }

    @Override
    protected Bitmap doInBackground(String... strings) {
        String url = strings[0];
        Bitmap mIcon11 = null;
        try{
            InputStream is = new java.net.URL(url).openStream();
            mIcon11 = BitmapFactory.decodeStream(is);
        }catch(Exception e){
            Log.i("Error", e.getMessage());
            e.printStackTrace();
        }
        return mIcon11;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        imageView.setImageBitmap(bitmap);
    }
}
