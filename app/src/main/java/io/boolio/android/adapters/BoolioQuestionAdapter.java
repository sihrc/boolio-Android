package io.boolio.android.adapters;

import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import io.boolio.android.R;
import io.boolio.android.helpers.tracking.EventTracker;
import io.boolio.android.helpers.tracking.TrackEvent;
import io.boolio.android.models.Question;
import io.boolio.android.network.clients.BoolioQuestionClient;
import io.boolio.android.network.helpers.BoolioCallback;
import io.boolio.android.network.models.Answer;
import io.boolio.android.network.models.BoolioData;

/**
 * Created by james on 4/24/15.
 * Questions Adapter for news feed.
 */
public class BoolioQuestionAdapter extends BoolioAdapter {
    final private static int ANIMATION_DELAY = 1000;
    Context context;

    public BoolioQuestionAdapter(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    public void fillContent(final QuestionHolder holder, final Question question) {
        holder.leftAnswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.leftAnswer.setEnabled(false);
                holder.rightAnswer.setEnabled(false);
                EventTracker.getInstance(context).trackQuestion(TrackEvent.ANSWER_QUESTION, question, "left");
                BoolioQuestionClient.api().postAnswer(BoolioData.keys("questionId", "direction").values(question._id, "left"), getNewNetworkCallback(holder, question));
            }
        });
        holder.rightAnswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.leftAnswer.setEnabled(false);
                holder.rightAnswer.setEnabled(false);
                EventTracker.getInstance(context).trackQuestion(TrackEvent.ANSWER_QUESTION, question, "right");
                BoolioQuestionClient.api().postAnswer(BoolioData.keys("questionId", "direction").values(question._id, "right"),
                        getNewNetworkCallback(holder, question));
            }
        });
    }

    private BoolioCallback<Question> getNewNetworkCallback(final QuestionHolder holder, final Question question){
        return new BoolioCallback<Question>() {
            @Override
            public void handle(Question object) {
                holder.leftAnswer.setText(String.valueOf(object.leftCount));
                holder.rightAnswer.setText(String.valueOf(object.rightCount));
                holder.view.startAnimation(getAnimation(holder, question));
            }
        };
    }

    private Animation getAnimation(final QuestionHolder holder, final Question question) {
        Animation animation = AnimationUtils.loadAnimation(context, R.anim.right_out);
        animation.setStartOffset(ANIMATION_DELAY);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                remove(question);
                onDataSetChanged();
                holder.leftAnswer.setEnabled(true);
                holder.rightAnswer.setEnabled(true);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        return animation;
    }

}
