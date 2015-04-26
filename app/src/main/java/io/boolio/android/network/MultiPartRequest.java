package io.boolio.android.network;

import android.graphics.Bitmap;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;

import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class MultiPartRequest extends Request<String> {

    private final Response.Listener<JSONObject> mListener;
    Bitmap bitmap;
    String name;
    private MultipartEntity entity = new MultipartEntity();

    public MultiPartRequest(String name, String url, Bitmap bitmap, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
        super(Method.POST, url, errorListener);

        mListener = listener;
        this.bitmap = bitmap;
        this.name = name;

        setRetryPolicy(new DefaultRetryPolicy(20000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }


    @Override
    public String getBodyContentType() {
        return entity.getContentType().getValue();
    }

    @Override
    public byte[] getBody() throws AuthFailureError {
        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, bao);

        ByteArrayOutputStream bas = new ByteArrayOutputStream();
        entity.addPart("part", new ByteArrayBody(bao.toByteArray(), name + ".png"));
        try {
            entity.writeTo(bas);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bas.toByteArray();
    }

    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response) {
        return Response.success(new String(response.data), getCacheEntry());
    }

    @Override
    protected void deliverResponse(String response) {
        try {
            mListener.onResponse(new JSONObject(response));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}