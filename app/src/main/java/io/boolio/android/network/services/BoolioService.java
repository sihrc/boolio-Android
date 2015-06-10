package io.boolio.android.network.services;

import retrofit.Callback;
import retrofit.http.GET;

/**
 * Created by Chris on 6/15/15.
 */
public interface BoolioService {
    @GET("/tests")
    void getABTests(Callback callback);

    @GET("/configs/android")
    void getConfigs(Callback<?> callback);
}
