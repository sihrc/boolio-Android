package io.boolio.android.adapters;

import android.content.Context;
import android.view.View;

import io.boolio.android.R;
import io.boolio.android.helpers.BoolioUserHandler;
import io.boolio.android.helpers.Dialogs;
import io.boolio.android.models.Question;
import io.boolio.android.network.ServerQuestion;

/**
 * Created by james on 4/24/15.
 */
public class BoolioAnswerAdapter extends BoolioAdapter {
    int rightColor, leftColor, white;

    public BoolioAnswerAdapter(Context context) {
        super(context);
        this.rightColor = context.getResources().getColor(R.color.darker_blue);
        this.leftColor = context.getResources().getColor(R.color.darker_blue);
        this.white = context.getResources().getColor(android.R.color.transparent);
    }

    @Override
    public void fillContent(QuestionHolder holder, final Question question) {
        String left = String.valueOf(question.left + " (" + question.leftCount + ")");
        String right = String.valueOf(question.right + " (" + question.rightCount + ")");
        holder.leftAnswer.setText(left);
        holder.rightAnswer.setText(right);
        holder.highLeft.setText(left);
        holder.highRight.setText(right);

        if (question.usersWhoLeft.contains(BoolioUserHandler.getInstance(context).getUser().userId)) {
            holder.highLeft.setVisibility(View.VISIBLE);
            holder.highRight.setVisibility(View.GONE);
        } else if (question.usersWhoRight.contains(BoolioUserHandler.getInstance(context).getUser().userId)) {
            holder.highRight.setVisibility(View.VISIBLE);
            holder.highLeft.setVisibility(View.GONE);
        } else {
            holder.highLeft.setVisibility(View.GONE);
            holder.highRight.setVisibility(View.GONE);
        }
    }
}
