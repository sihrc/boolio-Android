package io.boolio.android.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.AbsListView;
import android.widget.ListView;

/**
 * Created by Chris on 4/24/15.
 */
public class ScrollingListView extends ListView {
    ScrollChangeListener scrollChangeListener;


    public ScrollingListView(Context context) {
        super(context);
        setOnScrollListener(null);
    }

    public ScrollingListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOnScrollListener(null);
    }

    public ScrollingListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setOnScrollListener(null);
    }

    public void setScrollChangeListener(ScrollChangeListener scrollChangeListener) {
        this.scrollChangeListener = scrollChangeListener;
    }

    public static interface ScrollChangeListener {
        public void onScroll(boolean isScrollingUp);
    }

    @Override
    public void setOnScrollListener(final OnScrollListener onScrollListener) {
        super.setOnScrollListener(new OnScrollListener() {
            private int mInitialScroll = 0;
            private boolean touching = false;

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (onScrollListener != null)
                    onScrollListener.onScrollStateChanged(view, scrollState);
                touching = scrollState == SCROLL_STATE_TOUCH_SCROLL;
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (onScrollListener != null)
                    onScrollListener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
                if (!touching) {
                    return;
                }
                int scrolledOffset = computeVerticalScrollOffset();
                if (scrolledOffset != mInitialScroll) {
                    //if scroll position changed
                    if (scrollChangeListener != null)
                        scrollChangeListener.onScroll((scrolledOffset - mInitialScroll) < 0);
                    mInitialScroll = scrolledOffset;
                }
            }
        });
    }
}
