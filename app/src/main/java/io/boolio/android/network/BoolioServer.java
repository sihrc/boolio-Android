package io.boolio.android.network;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.util.LruCache;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collection;
import java.util.List;

import io.boolio.android.callbacks.QuestionsCallback;
import io.boolio.android.helpers.BoolioUserHandler;
import io.boolio.android.helpers.Utils;
import io.boolio.android.models.Question;
import io.boolio.android.models.User;
import io.boolio.android.network.parser.JSONArrayParser;
import io.boolio.android.network.parser.QuestionParser;
import io.boolio.android.network.parser.UserParser;

/**
 * Created by Chris on 4/16/15.
 */
public class BoolioServer {
    static BoolioServer instance;

    ImageLoader imageLoader;
    Context context;
    RequestQueue queue;
    Response.ErrorListener errorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            if (error.networkResponse != null && error.networkResponse.data != null)
                Log.e("Volley Error", new String(error.networkResponse.data));
            error.printStackTrace();
        }
    };

    public BoolioServer(Context context) {
        this.context = context;
        this.queue = Volley.newRequestQueue(context);
    }

    public static BoolioServer getInstance(Context context) {
        if (instance == null) {
            instance = new BoolioServer(context);
            NukeSSLCerts.nuke();
            instance.imageLoader = new ImageLoader(instance.queue, new ImageLoader.ImageCache() {
                LruCache<String, Bitmap> cache = new LruCache<>(40);

                @Override
                public Bitmap getBitmap(String url) {
                    return cache.get(url);
                }

                @Override
                public void putBitmap(String url, Bitmap bitmap) {
                    cache.put(url, bitmap);
                }
            });
        }

        return instance;
    }

    /**
     * GET *
     */

    public void getBoolioUserFromFacebook(User user, final NetworkCallback<User> callback) {
        JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, API.FACEBOOK_USER_ENDPOINT,
                UserParser.getInstance().toJSON(user), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (callback != null)
                    callback.handle(UserParser.getInstance().parse(response));

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

    public void getQuestionFeed(List<String> prevSeenQuestions, final QuestionsCallback callback) {
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
                            callback.handleQuestions(parser.toArray(response, QuestionParser.getInstance()));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, errorListener);
        queue.add(req);
    }

    public void getUserProfile(String userId, final NetworkCallback<User> callback) {
        JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET,
                API.GET_USER_ENDPOINT(userId),
                new JSONObject(), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                callback.handle(UserParser.getInstance().parse(response));
            }
        }, errorListener);


        queue.add(req);
    }

    public void getQuestions(Collection<String> questionIds, final QuestionsCallback callback) {
        JSONObject postPackage = new JSONObject();
        try {
            postPackage.put("ids", Utils.stringArrayToString.build(questionIds));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonArrayRequest req = new JsonArrayRequest(Request.Method.POST, API.POST_GET_QUESTIONS, postPackage, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    if (callback != null)
                        callback.handleQuestions(new JSONArrayParser<Question>().toArray(response, QuestionParser.getInstance(), true));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, errorListener);

        queue.add(req);
    }

    public void postQuestion(Question question, final Bitmap bm, final Runnable runnable) {
        JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, API.POST_QUESTION,
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
        }, errorListener);
        queue.add(req);
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

    public void searchQuestion(String search, final QuestionsCallback callback) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("searchParams", search);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonArrayRequest req = new JsonArrayRequest(Request.Method.POST,
                API.POST_SEARCH,
                jsonObject, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    callback.handleQuestions(new JSONArrayParser<Question>().toArray(response, QuestionParser.getInstance()));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, errorListener);

        queue.add(req);
    }


    public void postAnswer(String questionId, String direction, final NetworkCallback<Question> questionNetworkCallback) {
        JSONObject answer = new JSONObject();
        try {
            answer.put("questionId", questionId);
            answer.put("answer", direction);
            answer.put("id", BoolioUserHandler.getInstance(context).getUser().userId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, API.POST_ANSWER,
                answer, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (questionNetworkCallback != null)
                    questionNetworkCallback.handle(QuestionParser.getInstance().parse(response));
            }
        }, errorListener);

        queue.add(req);
    }

    public ImageLoader getImageLoader() {
        return imageLoader;
    }
}
