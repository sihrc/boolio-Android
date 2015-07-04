package io.boolio.android.network.endpoints;

import retrofit.Endpoint;

/**
 * Created by Chris on 6/10/15.
 */
public class ExternalEndpoint implements Endpoint {
    private String url;

    public ExternalEndpoint(String url) {
        this.url = url;
    }

    @Override
    public String getUrl() {
        if (url == null) throw new IllegalStateException("url not set.");
        return url;
    }

    @Override
    public String getName() {
        return "default";
    }
}