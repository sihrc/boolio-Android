package io.boolio.android.network;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.util.LruCache;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import io.boolio.android.helpers.Debugger;

/**
 * Created by Chris on 5/4/15.
 */
public class BoolioServer {
    final private static boolean DEBUG = true;

    ImageLoader imageLoader;
    Context context;
    RequestQueue queue;
    // Default Error Listener
    Response.ErrorListener errorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            if (error.networkResponse != null && error.networkResponse.data != null)
                Log.e("Volley Error", new String(error.networkResponse.data));
            error.printStackTrace();
        }
    };


    public BoolioServer(Context context) {
        this.queue = Volley.newRequestQueue(context);
        this.imageLoader = new ImageLoader(queue, new ImageLoader.ImageCache() {
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

        // Forget Signing
        NukeSSLCerts.nuke();
    }

    public RequestQueue getQueue() {
        return queue;
    }

    public ImageLoader getImageLoader() {
        return imageLoader;
    }

    // Make HTTP Request
    void makeRequest(int method, final String url, JSONObject jsonObject, final Response.Listener<JSONObject> listener) {
        Debugger.log(BoolioServer.class, "Making request at " + url);
        queue.add(new JsonObjectRequest(
                method, url, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (DEBUG)
                    Debugger.log(BoolioServer.class, "Request at: " + url + " \n Returned with: \n" + response.toString());
                listener.onResponse(response);
            }
        }, errorListener));
    }
}
