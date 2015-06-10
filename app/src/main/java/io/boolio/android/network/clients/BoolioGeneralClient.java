package io.boolio.android.network.clients;

import com.google.gson.internal.LinkedTreeMap;

import java.util.Map;

import io.boolio.android.helpers.PrefsHelper;
import io.boolio.android.network.helpers.BoolioCallback;
import io.boolio.android.network.services.BoolioService;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Chris on 6/15/15.
 */
public class BoolioGeneralClient extends BoolioClient<BoolioService> {
    static BoolioService apiService;

    public static BoolioService api() {
        if (apiService == null) {
            apiService = new BoolioGeneralClient().api;
        }

        return apiService;
    }

    public BoolioGeneralClient() {
        super("/api", BoolioService.class);
    }


    public static void getConfigs(final BoolioCallback<?> runnable) {
        api().getConfigs(new Callback<LinkedTreeMap<String, String>>() {
            @Override
            public void success(LinkedTreeMap<String, String> o, Response res) {
                PrefsHelper prefsHelper = PrefsHelper.getInstance();

                for (Map.Entry<String, String> entry : o.entrySet())
                    prefsHelper.saveString(entry.getKey(), String.valueOf(entry.getValue()));

                if (runnable != null) {
                    runnable.handle(null);
                }
            }

            @Override
            public void failure(RetrofitError error) {
            }
        });
    }
}
