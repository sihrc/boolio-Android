package io.boolio.android.helpers;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.concurrent.ExecutionException;

import io.boolio.android.R;
import io.boolio.android.network.helpers.BoolioCallback;

/**
 * Created by Chris on 6/29/15.
 */
public class Glider {
    static Context context;

    public static void init(Context _context) {
        context = _context;
    }

    public static void image(ImageView view, String image) {
        Glide.with(context)
            .load(image)
            .centerCrop()
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .thumbnail(0.25f)
            .crossFade(200)
            .placeholder(R.drawable.default_image)
            .into(view);
    }

    public static void profile(ImageView view, String image) {
        Glide.with(context)
            .load(image)
            .centerCrop()
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .crossFade(200)
            .placeholder(R.drawable.default_profile)
            .into(view);
    }

    public static void getBitmap(final String image, final int width, final int height, final BoolioCallback<Bitmap> bmCallback) {
        new AsyncTask<Void, Void, Void>() {
            Bitmap bitmap;

            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    bitmap = Glide.with(context)
                        .load(image)
                        .asBitmap()
                        .centerCrop()
                        .placeholder(R.drawable.default_image)
                        .into(width, height)
                        .get();
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                if (bitmap != null && bmCallback != null) {
                    bmCallback.handle(bitmap);
                }
            }
        }.execute();
    }
}
