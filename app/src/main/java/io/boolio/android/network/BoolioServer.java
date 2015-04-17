package io.boolio.android.network;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import io.boolio.android.adapters.QuestionAdapter;
import io.boolio.android.helpers.BoolioUserHandler;
import io.boolio.android.models.Question;
import io.boolio.android.network.parser.JSONArrayParser;
import io.boolio.android.network.parser.QuestionParser;
import io.boolio.android.network.parser.UserParser;

/**
 * Created by Chris on 4/16/15.
 */
public class BoolioServer {
    static BoolioServer instance;

    Context context;
    RequestQueue queue;

    public BoolioServer(Context context) {
        this.context = context;
        this.queue = Volley.newRequestQueue(context);
    }

    public static BoolioServer getInstance(Context context) {
        if (instance == null) {
            instance = new BoolioServer(context);
        }

        return instance;
    }

    public void getBoolioUserFromFacebook(JSONObject jsonObject) {
        JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, API.FACEBOOK_USER_ENDPOINT,
                jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.v("does dis work", response.toString());
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

    public void getQuestionFeed(final QuestionAdapter adapter, List<String> prevSeenQuestions) {
        JSONObject jsonObject = new JSONObject();
        try {
            JSONArray seenQuestionIds = new JSONArray();
            for (String id : prevSeenQuestions) {
                seenQuestionIds.put(id);
            }
            jsonObject.put("prevSeenQuestions", seenQuestionIds);
            jsonObject.put("id", BoolioUserHandler.getInstance(context).getUser().userId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonArrayRequest req = new JsonArrayRequest(Request.Method.POST, API.FEED_ENDPOINT, jsonObject,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        JSONArrayParser<Question> parser = new JSONArrayParser<>();
                        try {
                            adapter.addAll(parser.toArray(response, QuestionParser.getInstance()));
                            adapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Boolio Server Error", "Getting Question Feed Failed");
                error.printStackTrace();
            }
        });
        queue.add(req);
    }
}
