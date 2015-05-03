package io.boolio.android.gcm;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;

import io.boolio.android.R;
import io.boolio.android.helpers.BoolioUserHandler;
import io.boolio.android.helpers.PrefsHelper;
import io.boolio.android.models.User;
import io.boolio.android.network.BoolioServer;

/**
 * Created by Chris on 5/2/15.
 */
public class GCMHelper {
    final static private String PROPERTY_REG_ID = "gcm_reg_id";
    final static private String PROPERTY_APP_VERSION = "app_version";

    static GCMHelper instance;

    Context context;
    GoogleCloudMessaging gcm;

    public static GCMHelper getInstance(Context context) {
        if (instance == null)
            instance = new GCMHelper(context);
        return instance;
    }

    public GCMHelper(Context context) {
        gcm = GoogleCloudMessaging.getInstance(context);
        this.context = context;
    }

    public String getRegistrationId() {
        String registrationId = PrefsHelper.getInstance(context).getString(PROPERTY_REG_ID);

        // Check if app was updated; if so, it must clear the registration ID
        // since the existing registration ID is not guaranteed to work with
        // the new app version.
        int registeredVersion = PrefsHelper.getInstance(context).getInt(PROPERTY_APP_VERSION);
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion) {
            registerInBackground();
            PrefsHelper.getInstance(context).saveInt(PROPERTY_APP_VERSION, currentVersion);
        }
        return registrationId;
    }

    /**
     * @return Application's version code from the {@code PackageManager}.
     */
    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }


    /**
     * Registers the application with GCM servers asynchronously.
     * <p>
     * Stores the registration ID and app versionCode in the application's
     * shared preferences.
     */
    private void registerInBackground() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(context);
                    }
                    User user = BoolioUserHandler.getInstance(context).getUser();
                    String regId = gcm.register(context.getString(R.string.gcm_project_id));

                    // You should send the registration ID to your server over HTTP,
                    // so it can use GCM/HTTP or CCS to send messages to your app.
                    // The request to your server should be authenticated if your app
                    // is using accounts.
                    BoolioServer.getInstance(context).updateUserGCM(user.userId, regId);

                    // For this demo: we don't need to send it because the device
                    // will send upstream messages to a server that echo back the
                    // message using the 'from' address in the message.

                    // Persist the registration ID - no need to register again.
                    PrefsHelper.getInstance(context).saveString(PROPERTY_REG_ID, regId);
                    user.gcmId = getRegistrationId();
                } catch (IOException ex) {
                    // If there is an error, don't just keep trying to register.
                    // Require the user to click a button again, or perform
                    // exponential back-off.
                }

                return null;
            }
        }.execute(null, null, null);
    }
}
