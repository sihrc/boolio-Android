package io.boolio.android.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import io.boolio.android.MainActivity;

/**
 * Created by Chris on 4/15/15.
 */
public class BoolioFragment extends Fragment {
    public MainActivity activity;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = (MainActivity) activity;
    }

    @Override
    public void onResume() {
        super.onResume();
        clearFocus();
    }

    public void refreshPage() {}
    public void clearFocus() {}
}
