package io.boolio.android.network;

import android.content.Context;
import android.graphics.Bitmap;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collection;

import io.boolio.android.callbacks.QuestionsCallback;
import io.boolio.android.helpers.BoolioUserHandler;
import io.boolio.android.helpers.Utils;
import io.boolio.android.models.Question;
import io.boolio.android.network.parser.JSONArrayParser;
import io.boolio.android.network.parser.QuestionParser;

/**
 * Created by Chris on 4/16/15.
 */
public class ServerQuestion extends BoolioServer {
    static ServerQuestion instance;

    public ServerQuestion(Context context) {
        super(context);
    }

    public static ServerQuestion getInstance(Context context) {
        if (instance == null) {
            instance = new ServerQuestion(context);
        }

        return instance;
    }

    public void getQuestions(final Collection<String> questionIds, final QuestionsCallback callback) {
        makeRequest(Request.Method.POST, API.POST_GET_QUESTIONS, new JSONObject() {{
            try {
                put("ids", Utils.stringArrayToString.build(questionIds));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }}, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (callback != null)
                        callback.handleQuestions(new JSONArrayParser<Question>().toArray(response.getJSONArray("questions"), QuestionParser.getInstance(), true));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void postQuestion(Question question, final Bitmap bm, final Runnable runnable) {
        makeRequest(Request.Method.POST, API.POST_QUESTION,
                QuestionParser.getInstance().toJSON(question), new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Question returned = QuestionParser.getInstance().parse(response);
                        if (bm == null) {
                            if (runnable != null)
                                runnable.run();
                            return;
                        }
                        uploadImage(returned.questionId, bm, new NetworkCallback<Question>() {
                            @Override
                            public void handle(Question object) {
                                if (runnable != null)
                                    runnable.run();
                            }
                        });
                    }
                });
    }

    /**
     * POSTS *
     */
    public void uploadImage(final String questionId, Bitmap bm, final NetworkCallback<Question> callback) {
        queue.add(
                new MultiPartRequest(questionId + "image" + System.currentTimeMillis(), API.POST_UPLOAD_IMAGE, bm, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        JSONObject update = new JSONObject();
                        try {
                            update.put("id", questionId);
                            update.put("url", response.getString("url"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        queue.add(new JsonObjectRequest(Request.Method.POST, API.POST_UPDATE_QUESTION, update, new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                callback.handle(QuestionParser.getInstance().parse(response));
                            }
                        }, errorListener));
                    }
                }, errorListener)
        );
    }


    public void postAnswer(final String questionId, final String direction, final NetworkCallback<Question> questionNetworkCallback) {
        makeRequest(Request.Method.POST, API.POST_ANSWER,
                new JSONObject() {
                    {
                        try {
                            put("questionId", questionId);
                            put("answer", direction);
                            put("id", BoolioUserHandler.getInstance(context).getUser().userId);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if (questionNetworkCallback != null)
                            questionNetworkCallback.handle(QuestionParser.getInstance().parse(response));
                    }
                });
    }
}
