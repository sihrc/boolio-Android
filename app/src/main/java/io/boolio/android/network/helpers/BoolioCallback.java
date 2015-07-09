package io.boolio.android.network.helpers;

import android.util.Log;

import io.boolio.android.helpers.Debugger;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.converter.ConversionException;

/**
 * Created by Chris on 6/13/15.
 */
public abstract class BoolioCallback<T> implements Callback<T> {
    @Override
    public void success(T t, Response response) {
        Debugger.log(BoolioCallback.class, response != null ? response.getBody().toString() : "WARNING: Response was null");
        handle(t);
    }

    @Override
    public void failure(RetrofitError error) {
        try {
            Log.e("Retrofit Failed", error.getUrl());
            Log.e("         Failed", "\t" + (error.getBody() != null ? error.getBody().toString() : " body was null"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        error.printStackTrace();
    }

    public abstract void handle(T resObj);
}
