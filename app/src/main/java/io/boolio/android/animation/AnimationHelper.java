package io.boolio.android.animation;

import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import io.boolio.android.R;

/**
 * Created by Chris on 4/24/15.
 */
public class AnimationHelper {
    static AnimationHelper instance;

    Animation bottomOut, bottomIn, topOut, topIn, rightIn, rightOut, leftIn, leftOut;

    Context context;

    public static AnimationHelper getInstance(Context context) {
        if (instance == null) {
            instance = new AnimationHelper(context);
        }
        return instance;
    }

    public AnimationHelper(Context context) {
        this.context = context;
        setupAll();
    }

    private void setupAll() {
        bottomIn = AnimationUtils.loadAnimation(context, R.anim.bottom_in);
        bottomOut = AnimationUtils.loadAnimation(context, R.anim.bottom_out);
        topIn = AnimationUtils.loadAnimation(context, R.anim.top_in);
        topOut = AnimationUtils.loadAnimation(context, R.anim.top_out);
        rightIn = AnimationUtils.loadAnimation(context, R.anim.right_in);
        rightOut = AnimationUtils.loadAnimation(context, R.anim.right_out);
        leftIn = AnimationUtils.loadAnimation(context, R.anim.left_in);
        leftOut = AnimationUtils.loadAnimation(context, R.anim.left_out);
    }

    public void animateViewBottomIn(View view) {
        in(view, bottomIn);
    }
    public void animateViewBottomOut(View view) {
        out(view, bottomOut);
    }
    public void animateViewTopIn(View view) {
        in(view, topIn);
    }
    public void animateViewTopOut(View view) {
        out(view, topOut);
    }
    public void animateViewRightIn(View view) {
        in(view, rightIn);
    }
    public void animateViewRightOut(View view) {
        out(view, rightOut);
    }
    public void animateViewLeftIn(View view) {
        in(view, leftIn);
    }
    public void animateViewLeftOut(View view) {
        out(view, leftOut);
    }

    private void in(View view, Animation animation) {
        if (view.getVisibility() != View.VISIBLE) {
            view.startAnimation(animation);
            view.setVisibility(View.VISIBLE);
        }
    }

    private void out(View view, Animation animation) {
        if (view.getVisibility() == View.VISIBLE) {
            view.startAnimation(animation);
            view.setVisibility(View.INVISIBLE);
        }
    }


}