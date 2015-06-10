package io.boolio.android.network.clients;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import io.boolio.android.network.helpers.ItemTypeAdapterFactory;
import io.boolio.android.network.helpers.SessionRequestInterceptor;
import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;

/**
 * Created by Chris on 6/10/15.
 */
public abstract class BoolioClient<T> {
    //    final static private String ADDRESS = "https://beta.boolio.io";
    final static private String ADDRESS = "http://192.168.1.126:3000";
    T api;

    public BoolioClient(String endpoint, Class<T> classType) {
        Gson gson = new GsonBuilder()
                .registerTypeAdapterFactory(new ItemTypeAdapterFactory())
                .setDateFormat("yyyy'-'MM'-'dd'T'HH':'mm':'ss'.'SSS'Z'")
                .create();

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setEndpoint(ADDRESS + endpoint)
                .setConverter(new GsonConverter(gson))
                .setRequestInterceptor(new SessionRequestInterceptor())
                .build();

        api = restAdapter.create(classType);
    }
}
