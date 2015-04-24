package io.boolio.android.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import io.boolio.android.R;

/**
 * Created by filippos on 4/23/15.
 */
public class CategoriesFragment extends BoolioFragment {
    static CategoriesFragment instance;
    Context context;

    public static CategoriesFragment getInstance() {
        if (instance == null) {
            instance = new CategoriesFragment();
        }
        return instance;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.context = activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_categories, container, false);

        return rootView;
    }
}