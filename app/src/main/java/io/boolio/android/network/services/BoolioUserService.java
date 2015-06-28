package io.boolio.android.network.services;

import io.boolio.android.models.Question;
import io.boolio.android.models.User;
import io.boolio.android.network.BoolioData;
import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;

/**
 * Created by Chris on 6/9/15.
 */
public interface BoolioUserService {
    @POST("/gcm")
    void updateUserGCM(@Body User user, Callback<?> callback);

    @POST("/skip")
    void skipQuestion(@Body BoolioData question, Callback<?> callback);

    @POST("/unskip")
    void unskipQuestion(@Body Question question, Callback<?> callback);

    @GET("/{user}")
    void getUserProfile(@Path("user") String userId, Callback<User> callback);

    @POST("/facebook")
    void getBoolioUserFromFacebook(@Body User user, Callback<User> callback);
}
