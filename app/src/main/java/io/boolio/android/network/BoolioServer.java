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

import java.util.List;

import io.boolio.android.callbacks.QuestionsCallback;
import io.boolio.android.callbacks.UserCallback;
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

    ImageLoader imageLoader;
    Context context;
    RequestQueue queue;

    public BoolioServer(Context context) {
        this.context = context;
        this.queue = Volley.newRequestQueue(context);
    }

    public static BoolioServer getInstance(Context context) {
        if (instance == null) {
            instance = new BoolioServer(context);
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
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Boolio Server Error", "Getting Question Feed Failed");
                error.printStackTrace();
            }
        });
        queue.add(req);
    }

    public void getUserProfile(String userId, final UserCallback callback) {
        JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET,
                API.GET_USER_ENDPOINT(userId),
                new JSONObject(), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                callback.handleUser(UserParser.getInstance().parse(response));

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.e("GetUserProfile", "We done fucked it, " + error.getMessage());
            }
        });


        queue.add(req);
    }

    public void getUserAsked(String userId, final QuestionsCallback callback) {
        JsonArrayRequest req = new JsonArrayRequest(Request.Method.GET,
                API.GET_USER_ASKED(userId),
                new JSONObject(), new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    callback.handleQuestions(new JSONArrayParser<Question>().toArray(response, QuestionParser.getInstance()));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("getUserAsked", "We done fucked it, " + error.getMessage());
                error.printStackTrace();
            }
        });

        queue.add(req);
    }

    public void getUserAnswered(String userId, final QuestionsCallback callback) {
        JsonArrayRequest req = new JsonArrayRequest(Request.Method.GET,
                API.GET_USER_ANSWERED(userId),
                new JSONObject(), new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    callback.handleQuestions(new JSONArrayParser<Question>().toArray(response, QuestionParser.getInstance()));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("getUserAnswered", "We done fucked it, " + error.getMessage());
                error.printStackTrace();
            }
        });

        queue.add(req);
    }

    /**
     * POSTS *
     */

    public void postQuestion(JSONObject jsonObject, final Runnable runnable) {
        JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, API.CREATE_QUESTION_ENDPOINT,
                jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("Boolio Server stuff", response.toString());
                if (runnable != null)
                    runnable.run();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Boolio Server Error", "Posting Question Failed");
                error.printStackTrace();
            }
        });
        queue.add(req);
    }

    public void searchQuestion(String search, final QuestionsCallback callback) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("searchParams", search);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonArrayRequest req = new JsonArrayRequest(Request.Method.POST,
                API.SEARCH_ENDPOINT,
                jsonObject, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    callback.handleQuestions(new JSONArrayParser<Question>().toArray(response, QuestionParser.getInstance()));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("searchQuestion", "error " + error.getMessage());
                error.printStackTrace();
            }
        });

        queue.add(req);
    }


    public void postAnswer(JSONObject jsonObject) {
        JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, API.POST_ANSWER_ENDPOINT,
                jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("Boolio Server Success", "Success" + response.toString());

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Boolio Server Error", "Posting Answer failed");
                error.printStackTrace();
            }
        });

        queue.add(req);
    }


    public ImageLoader getImageLoader() {
        return imageLoader;
    }
}
