package io.boolio.android.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import io.boolio.android.R;

/**
 * Created by james on 4/21/15.
 */
public class SearchFragment extends BoolioFragment {
    static SearchFragment instance;

    EditText searchBar;

    public static SearchFragment getInstance(){
        if (instance == null){
            instance = new SearchFragment();
        }
        return instance;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_search, container, false);

        searchBar = (EditText) rootView.findViewById(R.id.search_bar);
        searchBar.

        return rootView;
    }
}
