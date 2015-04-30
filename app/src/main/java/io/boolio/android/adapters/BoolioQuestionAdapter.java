package io.boolio.android.adapters;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import io.boolio.android.R;
import io.boolio.android.animation.AnimationHelper;
import io.boolio.android.models.Question;
import io.boolio.android.network.BoolioServer;
import io.boolio.android.network.NetworkCallback;

/**
 * Created by james on 4/24/15.
 */
public class BoolioQuestionAdapter extends BoolioAdapter {
    final private static int ANIMATION_DELAY = 1000;

    public BoolioQuestionAdapter(Context context) {
        super(context);
    }

    @Override
    public void fillContent(final QuestionHolder holder, final Question question) {
        holder.leftAnswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.leftAnswer.setEnabled(false);
                BoolioServer.getInstance(context).postAnswer(question.questionId, "left", getNewNetworkCallback(holder, question));

            }
        });
        holder.rightAnswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.rightAnswer.setEnabled(false);
                BoolioServer.getInstance(context).postAnswer(question.questionId, "right",
                        getNewNetworkCallback(holder, question));
            }
        });
    }

    private NetworkCallback<Question> getNewNetworkCallback(final QuestionHolder holder, final Question question){
        return new NetworkCallback<Question>() {
            @Override
            public void handle(Question object) {
                holder.leftAnswer.setText(String.valueOf(object.leftCount));
                holder.rightAnswer.setText(String.valueOf(object.rightCount));
                holder.view.startAnimation(getAnimation(question));
            }
        };
    }

    private Animation getAnimation(final Question question) {
        Animation animation = AnimationUtils.loadAnimation(context, R.anim.right_out);
        animation.setStartOffset(ANIMATION_DELAY);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                remove(question);
                notifyDataSetChanged();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        return animation;
    }

}
