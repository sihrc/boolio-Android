package io.boolio.android.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

import io.boolio.android.R;
import io.boolio.android.helpers.BoolioUserHandler;
import io.boolio.android.network.BoolioServer;

/**
 * Created by james on 4/17/15.
 */
public class CreateQuestionFragment extends BoolioFragment {
    static CreateQuestionFragment instance;
    Context context;
    EditText questionText, left, right, tags;

    public static CreateQuestionFragment getInstance() {
        if (instance == null) {
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
        questionText = (EditText) rootView.findViewById(R.id.create_question_text);
        left = (EditText) rootView.findViewById(R.id.create_question_left_answer);
        right = (EditText) rootView.findViewById(R.id.create_question_right_answer);
        tags = (EditText) rootView.findViewById(R.id.create_question_tag);
        Button submit = (Button) rootView.findViewById(R.id.create_question_submit);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitOnClickSetup(v);
            }
        });

        return rootView;
    }

    private void submitOnClickSetup(View view) {
        JSONObject jsonObject = new JSONObject();
        if (questionText.getText().toString().length() == 0) {
            Toast.makeText(context, "Please enter a question", Toast.LENGTH_SHORT).show();
        } else {
            try {
                jsonObject.put("question", questionText.getText().toString());
                jsonObject.put("left", left.getText().toString().equals("") ? "No" : left.getText().toString());
                jsonObject.put("right", right.getText().toString().equals("") ? "Yes" : right.getText().toString());
                JSONArray array = new JSONArray(Arrays.asList(tags.getText().toString().split(", ")));
                jsonObject.put("tags", array);
                jsonObject.put("dateCreated", System.currentTimeMillis());
                jsonObject.put("creator", BoolioUserHandler.getInstance(context).getUser().userId);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            BoolioServer.getInstance(context).createQuestion(jsonObject);
            questionText.setText("");
            left.setText("");
            right.setText("");
            tags.setText("");
        }
    }
}
