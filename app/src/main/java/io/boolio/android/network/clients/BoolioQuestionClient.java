package io.boolio.android.network.clients;

import io.boolio.android.network.services.BoolioQuestionService;

/**
 * Created by Chris on 6/10/15.
 */
public class BoolioQuestionClient extends BoolioClient<BoolioQuestionService> {
    static BoolioQuestionService apiService;

    public static BoolioQuestionService api() {
        if (apiService == null) {
            apiService = new BoolioQuestionClient().api;
        }

        return apiService;
    }

    public BoolioQuestionClient() {
        super("/api/questions", BoolioQuestionService.class);
    }
}
