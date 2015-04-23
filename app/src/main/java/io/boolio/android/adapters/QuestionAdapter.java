package io.boolio.android.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextSwitcher;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;

import io.boolio.android.R;
import io.boolio.android.animation.TextAnimation;
import io.boolio.android.helpers.Utils;
import io.boolio.android.models.Question;
import io.boolio.android.network.BoolioServer;
import io.boolio.android.network.NetworkCallback;
import io.boolio.android.views.BoolioProfileImage;

/**
 * Created by james on 4/17/15.
 */
public class QuestionAdapter extends ArrayAdapter<Question> {
    int resource;
    Context context;

    public QuestionAdapter(Context context) {
        super(context, R.layout.item_question);
        resource = R.layout.item_question;
        this.context = context;
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {
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

    private void fillViews(final QuestionHolder holder, final Question question) {
        holder.question.setText(question.question);
        holder.leftAnswer.setCurrentText(question.left);
        holder.rightAnswer.setCurrentText(question.right);
        holder.creator.setText(question.creatorName);
        holder.date.setText(Utils.formatTimeDifferences(question.dateCreated) + " ago");

        if (question.image.equals("")){
            holder.questionImage.setVisibility(View.GONE);
        }
        holder.questionImage.setImageUrl(question.image, BoolioServer.getInstance(context).getImageLoader());
        holder.creatorImage.setImageUrl(question.creatorImage, BoolioServer.getInstance(context).getImageLoader());

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

    private class QuestionHolder {
        TextView question, creator, date;
        TextSwitcher leftAnswer, rightAnswer;
        BoolioProfileImage creatorImage;
        NetworkImageView questionImage;
    }

}
