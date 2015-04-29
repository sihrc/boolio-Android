package io.boolio.android.fragments;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.util.Log;

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

    public void refreshPage() {

    }
}
