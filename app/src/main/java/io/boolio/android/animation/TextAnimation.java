package io.boolio.android.animation;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.TextSwitcher;

import io.boolio.android.R;

/**
 * Created by Chris on 4/22/15.
 */
public class TextAnimation {
    static TextAnimation instance;

    Context context;
    LayoutInflater inflater;

    public TextAnimation(Context context) {
        this.context = context;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public static TextAnimation getInstance(Context context) {
        if (instance == null) {
            instance = new TextAnimation(context);
        }
        return instance;
    }

    public TextSwitcher FadeTextSwitcher(TextSwitcher textSwitcher, int resource) {

        return getTextSwitcher(textSwitcher, R.anim.fade_in, R.anim.fade_out, resource);
    }

    public TextSwitcher getTextSwitcher(TextSwitcher textSwitcher, int in, int out, int resource) {
        textSwitcher.setInAnimation(context, in);
        textSwitcher.setOutAnimation(context, out);
        textSwitcher.addView(inflater.inflate(resource, null, false));
        textSwitcher.addView(inflater.inflate(resource, null, false));
        return textSwitcher;
    }
}
