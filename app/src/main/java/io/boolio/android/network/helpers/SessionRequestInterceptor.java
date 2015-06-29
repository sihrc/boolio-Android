package io.boolio.android.network.helpers;

import io.boolio.android.helpers.BoolioUserHandler;
import retrofit.RequestInterceptor;

/**
 * Created by Chris on 6/10/15.
 */
public class SessionRequestInterceptor implements RequestInterceptor {
    @Override
    public void intercept(RequestFacade request) {
        String userId = BoolioUserHandler.getInstance().getUserId();
        if (userId != null) {
            request.addQueryParam("id", userId);
        }
    }
}