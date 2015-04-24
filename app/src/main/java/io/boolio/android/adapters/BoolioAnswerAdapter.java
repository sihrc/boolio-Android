package io.boolio.android.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextSwitcher;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;

import io.boolio.android.R;
import io.boolio.android.animation.TextAnimation;
import io.boolio.android.models.Question;
import io.boolio.android.views.BoolioProfileImage;

/**
 * Created by james on 4/24/15.
 */
public class BoolioAnswerAdapter extends BoolioAdapter {

    public BoolioAnswerAdapter(Context context) {
        super(context);
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        QuestionHolder holder;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(resource, parent, false);
            holder = new QuestionHolder();

            //TextViews
            holder.question = (TextView) view.findViewById(R.id.question_text);
            holder.leftAnswer = (TextSwitcher) view.findViewById(R.id.question_left_answer);
            holder.rightAnswer = (TextSwitcher) view.findViewById(R.id.question_right_answer);
            holder.creator = (TextView) view.findViewById(R.id.question_creator);
            holder.date = (TextView) view.findViewById(R.id.question_date);

            TextAnimation.getInstance(context).FadeTextSwitcher(holder.leftAnswer, R.layout.text_answer_left);
            TextAnimation.getInstance(context).FadeTextSwitcher(holder.rightAnswer, R.layout.text_answer_right);

            //Image Views
            holder.creatorImage = (BoolioProfileImage) view.findViewById(R.id.question_creator_picture);
            holder.questionImage = (NetworkImageView) view.findViewById(R.id.question_image);

            view.setTag(holder);
        } else {
            holder = (QuestionHolder) view.getTag();
        }

        fillViews(holder, getItem(position));

        return view;
    }

    @Override
    public void fillQuestions(QuestionHolder holder, Question question) {

    }

    @Override
    public void fillAnswers(QuestionHolder holder, Question question) {
        holder.leftAnswer.setText(String.valueOf(question.leftCount));
        holder.rightAnswer.setText(String.valueOf(question.rightCount));
    }
}
