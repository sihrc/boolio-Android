package io.boolio.android.network;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import io.boolio.android.callbacks.QuestionsCallback;
import io.boolio.android.helpers.PrefsHelper;
import io.boolio.android.models.Question;
import io.boolio.android.network.parser.JSONArrayParser;
import io.boolio.android.network.parser.QuestionParser;

/**
 * Created by Chris on 4/16/15.
 */
public class ServerFeed extends BoolioServer {
    static ServerFeed instance;

    public ServerFeed(Context context) {
        super(context);
    }

    public static ServerFeed getInstance(Context context) {
        if (instance == null) {
            instance = new ServerFeed(context);
        }

        return instance;
    }

    public void getQuestionFeed(final int questionLimit, final List<Question> prevSeenQuestions, final QuestionsCallback callback) {
        makeRequest(Request.Method.POST, API.FEED_ENDPOINT, new JSONObject() {{
                    try {
                        if (prevSeenQuestions == null){
                            put("prevSeenQuestions", new JSONArray());
                        } else
                            put("prevSeenQuestions", new JSONArray(prevSeenQuestions.toString()));
                        put("id", PrefsHelper.getInstance(context).getString("userId"));
                        put("count", questionLimit);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }},
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        JSONArrayParser<Question> parser = new JSONArrayParser<>();
                        try {
                            callback.handleQuestions(parser.toArray(response.getJSONArray("questions"), QuestionParser.getInstance()));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }


    /**
     * POSTS *
     */
    public void searchQuestion(final String search, final QuestionsCallback callback) {
        makeRequest(Request.Method.POST,
                API.POST_SEARCH,
                new JSONObject() {{
                    try {
                        put("searchParams", search);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }}, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            callback.handleQuestions(new JSONArrayParser<Question>().toArray(response.getJSONArray("questions"), QuestionParser.getInstance()));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }
}
