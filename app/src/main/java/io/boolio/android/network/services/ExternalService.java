package io.boolio.android.network.services;

import io.boolio.android.network.BoolioData;
import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Query;

/**
 * Created by Chris on 6/10/15.
 */
public interface ExternalService {
    @GET("/androidquery.appspot.com/api/market?app=io.boolio.android")
    void getAPPVersion(Callback<String> callback);

    @GET("/www.googleapis.com/customsearch/v1?rights=cc_publicdomain&searchType=image&safe=medium")
    void getImages(
              @Query("start") int start
            , @Query("cx") String cx_key
            , @Query("key") String api_key
            , @Query("q") String query
            , Callback<BoolioData> callback);
}
