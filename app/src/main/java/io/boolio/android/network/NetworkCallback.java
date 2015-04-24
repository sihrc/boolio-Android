package io.boolio.android.network;

/**
 * Created by Chris on 4/22/15.
 */
public interface NetworkCallback<T> {
    public void handle(T object);
}
