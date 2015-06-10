package io.boolio.android.network.clients;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import io.boolio.android.network.endpoints.ExternalEndpoint;
import io.boolio.android.network.services.ExternalService;
import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;

/**
 * Created by Chris on 6/10/15.
 */
public class ExternalClient {
    private static ExternalService apiService;

    public static void init() {
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy'-'MM'-'dd'T'HH':'mm':'ss'.'SSS'Z'")
                .create();

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setEndpoint(new ExternalEndpoint("https://"))
                .setConverter(new GsonConverter(gson))
                .build();

        apiService = restAdapter.create(ExternalService.class);
    }

    public static ExternalService api() {
        if (apiService == null) {
            init();
        }
        return apiService;
    }
}
