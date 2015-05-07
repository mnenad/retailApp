package com.pivotal.bootcamp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import java.io.InputStream;

/**
 * Created by administrator on 2015-05-07.
 */
public class ImageDownloader extends AsyncTask<String, Void, Bitmap> {
    ImageView imageView;

    public ImageDownloader(ImageView imageView) {
        this.imageView = imageView;
    }

    @Override
    protected Bitmap doInBackground(String... urls) {
        String url = urls[0];
        Bitmap thumb = null;
        try {
            InputStream in = new java.net.URL(url).openStream();
            thumb = BitmapFactory.decodeStream(in);
        } catch (Exception e) {
            Log.d("retail app", e.toString());
        }

        return thumb;
    }

    protected void onPostExecute(Bitmap result) {
        imageView.setImageBitmap(result);
    }
}
