package io.boolio.android.callbacks;

import io.boolio.android.custom.ObservableScrollView;

/**
 * Created by Chris on 4/21/15.
 */
public interface ScrollViewListener {
    void onScrollChanged(ObservableScrollView scrollView, int x, int y, int oldx, int oldy);
}
