package io.boolio.android.network;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

import io.boolio.android.helpers.BoolioUserHandler;
import io.boolio.android.helpers.PrefsHelper;
import io.boolio.android.helpers.ABTestHelper;
import io.boolio.android.models.Question;
import io.boolio.android.models.User;
import io.boolio.android.network.parser.UserParser;

/**
 * Created by Chris on 4/16/15.
 */
public class ServerUser extends BoolioServer {
    static ServerUser instance;

    public ServerUser(Context context) {
        super(context);
    }

    public static ServerUser getInstance(Context context) {
        if (instance == null) {
            instance = new ServerUser(context);
        }

        return instance;
    }

    /**
     * GET *
     */

    public void getBoolioUserFromFacebook(User user, final NetworkCallback<User> callback) {
        makeRequest(Request.Method.POST, API.FACEBOOK_USER_ENDPOINT,
                UserParser.getInstance().toJSON(user), new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if (callback != null)
                            callback.handle(UserParser.getInstance().parse(response));

                    }
                });
    }

    public void getUserProfile(String userId, final NetworkCallback<User> callback) {
        makeRequest(Request.Method.GET,
                API.GET_USER_ENDPOINT(userId),
                new JSONObject(), new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        callback.handle(UserParser.getInstance().parse(response));
                    }
                });
    }

    public void getABTests() {
        final PrefsHelper helper = PrefsHelper.getInstance(context);
        makeRequest(Request.Method.GET,
                API.GET_TESTS,
                new JSONObject(),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray tests = response.getJSONArray("tests");
                            for (int i = 0; i < tests.length(); i++) {
                                String test = tests.getString(i);
                                int value = helper.getInt(test);
                                if (value == -1) {
                                    getABTest(test);
                                } else {
                                    ABTestHelper.getInstance(context).addTest(test, value);
                                }
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    private void getABTest(final String name) {
        makeRequest(Request.Method.POST, API.GET_TESTS, new JSONObject() {{
            try {
                put("name", name);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }}, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    int value = response.getInt("value");
                    PrefsHelper.getInstance(context).saveInt(name, value);
                    ABTestHelper.getInstance(context).addTest(name, value);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void getConfigs(final Runnable runnable) {
        makeRequest(Request.Method.GET,
                API.GET_VERSION,
                new JSONObject(),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        PrefsHelper prefsHelper = PrefsHelper.getInstance(context);
                        Iterator<String> keys = response.keys();

                        String tag;
                        while (keys.hasNext()) {
                            tag = keys.next();
                            try {
                                prefsHelper.saveString(tag, response.getString(tag));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        if (runnable != null) {
                            runnable.run();
                        }
                    }
                });
    }

    /**
     * POST
     */
    public void updateUserGCM(String userId, String gcmId) {
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("id", userId);
            jsonObject.put("gcm", gcmId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, API.POST_USER_GCMID,
                jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
            }
        }, errorListener);

        queue.add(req);
    }


    public void skipQuestion(final Question question) {
        makeRequest(Request.Method.POST, API.SKIP_QUESTION, new JSONObject() {{
            try {
                put("userId", BoolioUserHandler.getInstance(context).getUser().userId);
                put("id", question.questionId);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }}, null);
    }

    public void unskipQuestion(final Question question) {
        makeRequest(Request.Method.POST, API.UNSKIP_QUESTION, new JSONObject() {{
            try {
                put("userId", BoolioUserHandler.getInstance(context).getUser().userId);
                put("id", question.questionId);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }}, null);
    }
}
