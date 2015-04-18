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
public class ProfileAnsweredFragment extends BoolioFragment {
    static ProfileAnsweredFragment instance;
    Context context;
    ListView answered;
    QuestionAdapter answeredAdapter;

    public static ProfileAnsweredFragment getInstance(){
        if(instance == null){
            instance = new ProfileAnsweredFragment();
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
        View rootView = inflater.inflate(R.layout.fragment_profile_answered, container, false);

        answered = (ListView) rootView.findViewById(R.id.profile_answered_feed);
        answeredAdapter = new QuestionAdapter(context, R.layout.item_question);
        answered.setAdapter(answeredAdapter);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        pullQuestions();
    }

    private void pullQuestions() {
        BoolioServer.getInstance(context).getProfileFeed(answeredAdapter, BoolioUserHandler.getInstance(context).getUser().questionsAnswered);
    }
}
