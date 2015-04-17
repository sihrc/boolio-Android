package io.boolio.android.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import io.boolio.android.R;
import io.boolio.android.helpers.BoolioUserHandler;
import io.boolio.android.network.BoolioServer;

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

    public static CreateQuestionFragment getInstance() {
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
        final EditText questionText = (EditText) rootView.findViewById(R.id.create_question_text);
        final EditText left = (EditText) rootView.findViewById(R.id.create_question_left_answer);
        final EditText right = (EditText) rootView.findViewById(R.id.create_question_right_answer);
        EditText tags = (EditText) rootView.findViewById(R.id.create_question_tag);
        Button submit = (Button) rootView.findViewById(R.id.create_question_submit);

        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("question", questionText.getText().toString());
            jsonObject.put("left ", left.getText().toString());
            jsonObject.put("right", right.getText().toString());
            jsonObject.put("tags", "");
            jsonObject.put("dateCreated", System.currentTimeMillis());
            jsonObject.put("creator", BoolioUserHandler.getInstance(context).getUser().userId);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BoolioServer.getInstance(context).createQuestion(jsonObject);
                questionText.setText("");
                left.setText("");
                right.setText("");

            }
        });

        return rootView;
    }
}
