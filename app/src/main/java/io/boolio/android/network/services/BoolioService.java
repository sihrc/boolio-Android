package io.boolio.android.network.services;

import io.boolio.android.network.helpers.BoolioCallback;
import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.GET;

/**
 * Created by Chris on 6/15/15.
 */
public interface BoolioService {
    @GET("/tests")
    void getABTests(Callback callback);

    @GET("/configs/android/version")
    void getAndroidVersion(@Body int version, BoolioCallback<Integer> callback);
}
