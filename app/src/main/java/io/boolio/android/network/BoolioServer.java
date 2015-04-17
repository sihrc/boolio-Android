package io.boolio.android.network;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import io.boolio.android.helpers.BoolioUserHandler;
import io.boolio.android.models.User;
import io.boolio.android.network.parser.UserParser;

/**
 * Created by Chris on 4/16/15.
 */
public class BoolioServer {
    static BoolioServer instance;

    Context context;
    RequestQueue queue;

    public static BoolioServer getInstance(Context context) {
        if (instance == null) {
            instance = new BoolioServer(context);
        }

        return instance;
    }

    public BoolioServer(Context context) {
        this.context = context;
        this.queue = Volley.newRequestQueue(context);
    }

    public void getBoolioUserFromFacebook(JSONObject jsonObject) {
        JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, API.FACEBOOK_USER_ENDPOINT,
                jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                BoolioUserHandler.getInstance(context).setUser(UserParser.getInstance().parse(response));

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Boolio Server Error", "Getting User failed");
                error.printStackTrace();
            }
        });

        queue.add(req);
    }

}
