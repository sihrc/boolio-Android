package io.boolio.android.adapters;

import android.content.Context;

import io.boolio.android.models.Question;

/**
 * Created by james on 4/24/15.
 */
public class BoolioAnswerAdapter extends BoolioAdapter {

    public BoolioAnswerAdapter(Context context) {
        super(context);
    }


    @Override
    public void fillContent(QuestionHolder holder, Question question) {
        holder.leftAnswer.setText(String.valueOf(question.left + " (" + question.leftCount + ")"));
        holder.rightAnswer.setText(String.valueOf(question.right + " (" + question.rightCount + ")"));
    }
}
