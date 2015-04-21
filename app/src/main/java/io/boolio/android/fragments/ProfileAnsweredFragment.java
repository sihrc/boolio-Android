package io.boolio.android.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import io.boolio.android.R;
import io.boolio.android.adapters.QuestionAdapter;
import io.boolio.android.callbacks.ScrollViewCallback;
import io.boolio.android.helpers.BoolioUserHandler;
import io.boolio.android.helpers.DragListener;
import io.boolio.android.network.BoolioServer;

/**
 * Created by james on 4/18/15.
 */
public class ProfileAnsweredFragment extends BoolioFragment {
    static ProfileAnsweredFragment instance;
    Context context;
    ListView answeredListView;
    QuestionAdapter answeredAdapter;
    ScrollViewCallback callback;

    public void setCallback(ScrollViewCallback callback) {
        this.callback = callback;
    }

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

        answeredListView = (ListView) rootView.findViewById(R.id.profile_answered_feed);
        answeredAdapter = new QuestionAdapter(context, R.layout.item_question);
        answeredListView.setAdapter(answeredAdapter);


        final DragListener dragListener = new DragListener(context);
        answeredListView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                dragListener.onTouch(v, event);
                if(dragListener.getdy() != 0.0 && callback != null) {
                    callback.scroll(dragListener.getdy());
                    Log.i("DebugDebug", "here");
                }

                return false;
            }
        });


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
