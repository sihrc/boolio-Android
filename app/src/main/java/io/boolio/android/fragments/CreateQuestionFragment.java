package io.boolio.android.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import io.boolio.android.R;
import io.boolio.android.helpers.BoolioUserHandler;

import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by james on 4/17/15.
 */
public class CreateQuestionFragment extends BoolioFragment {
    static CreateQuestionFragment instance;
    Context context;

    public static CreateQuestionFragment newInstance() {
        if (instance == null){
            instance = new CreateQuestionFragment();
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
        View rootView = inflater.inflate(R.layout.fragment_create_question, container, false);

        // Find Views and Buttons
        EditText questionText = (EditText) rootView.findViewById(R.id.create_question_text);
        EditText left = (EditText) rootView.findViewById(R.id.create_question_left_answer);
        EditText right = (EditText) rootView.findViewById(R.id.create_question_right_answer);

        JSONObject obj = new JSONObject();
        try {
            obj.put("question", questionText.getText().toString());
            obj.put("left ", left.getText().toString());
            obj.put("right", right.getText().toString());
            obj.put("tags", "");
            obj.put("dateCreated", System.currentTimeMillis());
            obj.put("creator", BoolioUserHandler.getInstance(context).getUser().userId);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        return rootView;
    }
}
