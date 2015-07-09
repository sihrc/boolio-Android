package io.boolio.android.network.clients;

import io.boolio.android.network.services.BoolioUserService;

/**
 * Created by Chris on 6/10/15.
 */
public class BoolioUserClient extends BoolioClient<BoolioUserService> {
    static BoolioUserService apiService;

    public BoolioUserClient() {
        super("/api/users", BoolioUserService.class);
    }

    public static BoolioUserService api() {
        if (apiService == null) {
            apiService = new BoolioUserClient().api;
        }

        return apiService;
    }
}