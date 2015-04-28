package io.boolio.android.fragments;

import android.app.Activity;
import android.support.v4.app.Fragment;

import io.boolio.android.MainActivity;

/**
 * Created by Chris on 4/15/15.
 */
public class BoolioFragment extends Fragment {
    MainActivity activity;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = (MainActivity) activity;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (activity != null)
            activity.isTransitioning = false;
    }

    @Override
    public void onPause() {
        super.onPause();
        if (activity != null)
            activity.isTransitioning = true;
    }
}
