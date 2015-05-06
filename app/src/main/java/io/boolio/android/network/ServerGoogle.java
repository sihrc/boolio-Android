package io.boolio.android.network;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import io.boolio.android.R;
import io.boolio.android.fragments.search.SearchImage;

/**
 * Created by Chris on 5/4/15.
 */
public class ServerGoogle extends BoolioServer {
    static ServerGoogle instance;

    public ServerGoogle(Context context) {
        super(context);
    }


    public static ServerGoogle getInstance(Context context) {
        if (instance == null)
            instance = new ServerGoogle(context);
        return instance;
    }


    public void getImages(String query, final NetworkCallback<List<SearchImage>> urls) {
        try {
            query = URLEncoder.encode(query, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < 2; i++ ) {
            makeRequest(Request.Method.GET,
                    "https://www.googleapis.com/customsearch/v1?" +
                            "rights=cc_publicdomain" +
                            "&searchType=image" +
                            "&safe=medium" +
                            "&start=" + (i * 10 + 1) +
                            "&cx=" + context.getString(R.string.google_cx_key) +
                            "&key=" + context.getString(R.string.google_api_key)
                            + "&q=" + query, new JSONObject(), new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {

                            List<SearchImage> results = new ArrayList<>();
                            try {
                                JSONArray items = response.getJSONArray("items");
                                JSONObject obj;
                                for (int i = 0; i < items.length(); i++) {
                                    obj = items.getJSONObject(i);
                                    results.add(new SearchImage(obj.getJSONObject("image")
                                            .getString("thumbnailLink"), obj.getString("link")));
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            if (urls != null)
                                urls.handle(results);
                        }
                    });
        }
    }
}
