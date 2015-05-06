package io.boolio.android.network;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

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
}
