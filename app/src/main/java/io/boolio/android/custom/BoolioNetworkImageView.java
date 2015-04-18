package io.boolio.android.custom;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

/**
 * Created by Chris on 4/18/15.
 */
public class BoolioNetworkImageView extends NetworkImageView {
    private Bitmap mLocalBitmap;

    private boolean mShowLocal;

    public BoolioNetworkImageView(Context context) {
        this(context, null);
    }

    public BoolioNetworkImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BoolioNetworkImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setLocalImageBitmap(Bitmap bitmap) {
        if (bitmap != null) {
            mShowLocal = true;
        }
        this.mLocalBitmap = bitmap;
        requestLayout();
    }

    @Override
    public void setImageUrl(String url, ImageLoader imageLoader) {
        mShowLocal = false;
        super.setImageUrl(url, imageLoader);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {

        super.onLayout(changed, left, top, right, bottom);
        if (mShowLocal) {
            setImageBitmap(mLocalBitmap);
        }
    }
}
