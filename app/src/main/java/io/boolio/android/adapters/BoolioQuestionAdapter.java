package io.boolio.android.adapters;

import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import io.boolio.android.R;
import io.boolio.android.helpers.tracking.EventTracker;
import io.boolio.android.helpers.tracking.TrackEvent;
import io.boolio.android.models.Question;
import io.boolio.android.network.clients.BoolioQuestionClient;
import io.boolio.android.network.helpers.BoolioCallback;
import io.boolio.android.network.BoolioData;

/**
 * Created by james on 4/24/15.
 * Questions Adapter for news feed.
 */
public class BoolioQuestionAdapter extends BoolioAdapter {
    final private static int ANIMATION_DELAY = 1000;
    Context context;
    List<Question> questions;

    public BoolioQuestionAdapter(Context context) {
        super(context);
        this.context = context;
        this.questions = new ArrayList<>();
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

    @Override
    public Question getItem(int position) {
        return questions.get(position);
    }

    @Override
    public int getCount() {
        return questions.size();
    }

    @Override
    public void addAll(Collection<? extends Question> collection) {
        questions.addAll(collection);
    }

    @Override
    public void add(Question object) {
        questions.add(object);
    }

    @Override
    public void insert(Question object, int index) {
        questions.add(index, object);
    }

    @Override
    public void clear() {
        questions.clear();
    }

    @Override
    public void sort(Comparator<? super Question> comparator) {
        Collections.sort(questions, comparator);
    }

    @Override
    public void remove(Question object) {
        questions.remove(object);
    }

    public List<Question> getList() {
        return questions;
    }
    public List<String> getQuestionIds() {
        List<String> questionIds = new ArrayList<>(questions.size());
        for (Question question : questions) {
            questionIds.add(question._id);
        }

        return questionIds;
    }
}
