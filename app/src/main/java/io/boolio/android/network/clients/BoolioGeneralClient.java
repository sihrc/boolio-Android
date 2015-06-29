package io.boolio.android.network.clients;

import io.boolio.android.network.services.BoolioService;

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
}
