package io.boolio.android;

import android.app.Application;

import com.crashlytics.android.Crashlytics;

import io.boolio.android.helpers.BoolioUserHandler;
import io.boolio.android.helpers.Glider;
import io.boolio.android.helpers.PrefsHelper;
import io.fabric.sdk.android.Fabric;

/**
 * Created by Chris on 6/16/15.
 */
public class Boolio extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        if (!BuildConfig.DEBUG)
            Fabric.with(this, new Crashlytics());

        PrefsHelper.init(this);
        BoolioUserHandler.init(this);
        Glider.init(this);
    }
}
