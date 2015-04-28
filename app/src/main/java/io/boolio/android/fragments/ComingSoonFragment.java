package io.boolio.android.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import io.boolio.android.R;

/**
 * Created by james on 4/28/15.
 */
public class ComingSoonFragment extends BoolioListFragment {
    static ComingSoonFragment instance;
    String setTemp;

    public static ComingSoonFragment newInstance(String text){
        instance = new ComingSoonFragment();
        instance.setTemp = text;
        return instance;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_comingsoon, container, false);

        TextView temp = (TextView) rootView.findViewById(R.id.comingsoon_text);
        temp.setText(setTemp);

        return rootView;
    }
}
