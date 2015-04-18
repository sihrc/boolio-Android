package io.boolio.android.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import io.boolio.android.R;
import io.boolio.android.adapters.QuestionAdapter;
import io.boolio.android.helpers.BoolioUserHandler;
import io.boolio.android.models.Question;
import io.boolio.android.network.BoolioServer;

/**
 * Created by james on 4/18/15.
 */
public class ProfileAskedFragment extends BoolioFragment {
    static ProfileAskedFragment instance;
    Context context;
    ListView askedListView;
    QuestionAdapter askedAdapter;

    public static ProfileAskedFragment getInstance(){
        if(instance == null){
            instance = new ProfileAskedFragment();
        }
        return instance;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        context = activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_profile_asked, container, false);
        askedListView = (ListView) rootView.findViewById(R.id.profile_asked_feed);

        askedAdapter = new QuestionAdapter(context, R.layout.item_question);
        askedListView.setAdapter(askedAdapter);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        pullQuestions();
    }

    private void pullQuestions() {
        BoolioServer.getInstance(context).getProfileFeed(askedAdapter, BoolioUserHandler.getInstance(context).getUser().questionsAsked);
    }
}
