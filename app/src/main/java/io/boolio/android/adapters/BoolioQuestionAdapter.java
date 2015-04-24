package io.boolio.android.adapters;

import android.content.Context;
import android.view.View;

import io.boolio.android.models.Question;
import io.boolio.android.network.BoolioServer;
import io.boolio.android.network.NetworkCallback;

/**
 * Created by james on 4/24/15.
 */
public class BoolioQuestionAdapter extends BoolioAdapter {

    public BoolioQuestionAdapter(Context context) {
        super(context);
    }

    @Override
    public void fillContent(final QuestionHolder holder, final Question question) {
        final NetworkCallback<Question> questionNetworkCallback = new NetworkCallback<Question>() {
            @Override
            public void handle(Question object) {
                holder.leftAnswer.setText(String.valueOf(object.leftCount));
                holder.rightAnswer.setText(String.valueOf(object.rightCount));
            }
        };
        holder.leftAnswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BoolioServer.getInstance(context).postAnswer(question.questionId, "left", questionNetworkCallback);

            }
        });
        holder.rightAnswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BoolioServer.getInstance(context).postAnswer(question.questionId, "right", questionNetworkCallback);
            }
        });
    }
}
