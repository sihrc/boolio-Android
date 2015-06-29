package io.boolio.android;

import android.app.Application;

import com.crashlytics.android.Crashlytics;
import com.nostra13.universalimageloader.core.DisplayImageOptions;

import io.boolio.android.helpers.BoolioUserHandler;
import io.boolio.android.helpers.PrefsHelper;
import io.fabric.sdk.android.Fabric;

/**
 * Created by Chris on 6/16/15.
 */
public class Boolio extends Application {
    public static DisplayImageOptions ImageOptions;
    @Override
    public void onCreate() {
        super.onCreate();

        if (!BuildConfig.DEBUG)
            Fabric.with(this, new Crashlytics());

        PrefsHelper.init(this);
        BoolioUserHandler.init(this);


        // Setting up Image Loader Options
        ImageOptions = new DisplayImageOptions.Builder().cacheInMemory(true)
                .cacheOnDisk(true).resetViewBeforeLoading(true)
                .delayBeforeLoading(0)
                .showImageForEmptyUri(R.drawable.default_image)
                .showImageOnFail(R.drawable.default_image)
                .showImageOnLoading(R.drawable.default_image).build();
    }
}
