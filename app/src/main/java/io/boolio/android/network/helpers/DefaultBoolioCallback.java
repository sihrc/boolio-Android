package io.boolio.android.network.helpers;

import io.boolio.android.helpers.Debugger;

/**
 * Created by Chris on 6/17/15.
 */
public class DefaultBoolioCallback extends BoolioCallback {
    @Override
    public void handle(Object resObj) {
        Debugger.log(BoolioCallback.class, resObj.toString());
    }
}
