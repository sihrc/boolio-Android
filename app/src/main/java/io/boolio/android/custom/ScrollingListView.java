package io.boolio.android.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.AbsListView;

/**
 * Created by Chris on 4/24/15.
 */
public class ScrollingListView extends EnhancedListView {
    ScrollChangeListener scrollChangeListener;
    PullQuestionListener pullQuestionListener;
    final static private int QUESTION_THRESHOLD = 8;

    public ScrollingListView(Context context) {
        super(context);
        setOnScrollListener(null);
    }

    @Override
    public void setOnScrollListener(final OnScrollListener onScrollListener) {
        super.setOnScrollListener(new OnScrollListener() {
            private int mInitialScroll = 0;
            private boolean touching = false;
            private boolean stopQuestion = false;
            private int questionNum;

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

                if (Math.abs(scrolledOffset - mInitialScroll) > 15) {
                    //if scroll position changed
                    if (scrollChangeListener != null)
                        scrollChangeListener.onScroll((scrolledOffset - mInitialScroll) < 0);
                    mInitialScroll = scrolledOffset;
                }

                if (pullQuestionListener != null && !stopQuestion && (totalItemCount - firstVisibleItem < QUESTION_THRESHOLD)) {
                    pullQuestionListener.pullQuestion();
                    stopQuestion = true;
                } else if (questionNum != totalItemCount) {
                    stopQuestion = false;
                }   
                questionNum = totalItemCount;
            }
        });
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

    public void setPullQuestionListener(PullQuestionListener pullQuestionListener) {
        this.pullQuestionListener = pullQuestionListener;
    }

    public interface ScrollChangeListener {
        void onScroll(boolean isScrollingUp);
    }

    public interface PullQuestionListener {
        void pullQuestion();
    }
}
