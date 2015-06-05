package io.boolio.android.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextSwitcher;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;

import io.boolio.android.R;
import io.boolio.android.animation.TextAnimation;
import io.boolio.android.custom.BoolioNetworkImageView;
import io.boolio.android.custom.BoolioProfileImage;
import io.boolio.android.helpers.BoolioUserHandler;
import io.boolio.android.helpers.Dialogs;
import io.boolio.android.helpers.Utils;
import io.boolio.android.helpers.tracking.EventTracker;
import io.boolio.android.helpers.tracking.TrackEvent;
import io.boolio.android.models.Question;
import io.boolio.android.network.BoolioServer;
import io.boolio.android.network.NetworkCallback;
import io.boolio.android.network.ServerFeed;
import io.boolio.android.network.ServerQuestion;
import io.boolio.android.network.ServerUser;

/**
 * Created by james on 4/24/15.
 */
public abstract class BoolioAdapter extends ArrayAdapter<Question> {
    Context context;
    int resource;

    Runnable onEmpty;

    public BoolioAdapter(Context context) {
        super(context, R.layout.item_question);
        resource = R.layout.item_question;
        this.context = context;
    }

    public Question remove(int position) {
        Question question = getItem(position);
        remove(question);
        return question;
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        final QuestionHolder holder;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            holder = new QuestionHolder();
            view = inflater.inflate(resource, parent, false);
            holder.view = view;

            //TextViews
            holder.question = (TextView) view.findViewById(R.id.question_text);
            holder.leftAnswer = (TextSwitcher) view.findViewById(R.id.question_left_answer);
            holder.rightAnswer = (TextSwitcher) view.findViewById(R.id.question_right_answer);
            holder.highLeft = (TextView) view.findViewById(R.id.highlighted_left);
            holder.highRight = (TextView) view.findViewById(R.id.highlighted_right);
            holder.creator = (TextView) view.findViewById(R.id.question_creator);
            holder.date = (TextView) view.findViewById(R.id.question_date);
            holder.report = view.findViewById(R.id.report_button);
            holder.delete = view.findViewById(R.id.delete_button);

            TextAnimation.getInstance(context).FadeTextSwitcher(holder.leftAnswer, R.layout.text_answer_left);
            TextAnimation.getInstance(context).FadeTextSwitcher(holder.rightAnswer, R.layout.text_answer_right);

            //Image Views
            holder.creatorImage = (BoolioProfileImage) view.findViewById(R.id.question_creator_picture);
            holder.questionImage = (BoolioNetworkImageView) view.findViewById(R.id.question_image);

            view.setTag(holder);
        } else {
            holder = (QuestionHolder) view.getTag();
        }

        fillViews(holder, getItem(position));

        return view;
    }

    public void fillViews(final QuestionHolder holder, final Question question) {
        holder.question.setText(question.question);
        holder.leftAnswer.setCurrentText(question.left);
        holder.rightAnswer.setCurrentText(question.right);
        holder.creator.setText(question.creatorName);
        holder.date.setText(Utils.formatTimeDifferences(question.dateCreated) + " ago");

        // Setup Creator Image and Name
        if (question.creatorId != null && question.creatorId.equals(BoolioUserHandler.getInstance(context).getUser().userId)){
            holder.delete.setVisibility(View.VISIBLE);
            holder.report.setVisibility(View.GONE);
        } else {
            holder.delete.setVisibility(View.GONE);
            holder.report.setVisibility(View.VISIBLE);
        }

        // Setup Question Image
        if (question.image.equals("")) {
            holder.questionImage.setVisibility(View.GONE);
        } else {
            holder.questionImage.setVisibility(View.VISIBLE);
        }

        holder.report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventTracker.getInstance(context).trackQuestion(TrackEvent.DELETE_QUESTION, question, null);
                Dialogs.messageDialog(context, R.string.report_title, R.string.report_message, new Runnable() {
                    @Override
                    public void run() {
                        ServerQuestion.getInstance(context).reportQuestion(question.questionId);
                        remove(question);
                        notifyDataSetChanged();
                    }
                });
            }
        });

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventTracker.getInstance(context).trackQuestion(TrackEvent.DELETE_QUESTION, question, null);
                Dialogs.messageDialog(context, R.string.delete_title, R.string.delete_message, new Runnable() {
                    @Override
                    public void run() {
                        ServerQuestion.getInstance(context).deleteQuestion(question.questionId);
                        remove(question);
                        notifyDataSetChanged();
                    }
                });
            }
        });

        fillContent(holder, question);

        holder.questionImage.setDefaultImageResId(R.drawable.default_image);

        if (question.image.equals("")) {
            holder.questionImage.setVisibility(View.GONE);
        } else {
            holder.questionImage.setVisibility(View.VISIBLE);
            holder.questionImage.setImageUrl(question.image, ServerFeed.getInstance(context).getImageLoader());
        }

        holder.creatorImage.setImageUrl(question.creatorImage, ServerUser.getInstance(context).getImageLoader());
    }

    public abstract void fillContent(QuestionHolder holder, Question question);

    public class QuestionHolder {
        View view, report, delete;
        TextView question, creator, date, highLeft, highRight;
        TextSwitcher leftAnswer, rightAnswer;
        BoolioProfileImage creatorImage;
        BoolioNetworkImageView questionImage;
    }

    public void setOnEmpty(Runnable onEmpty) {
        this.onEmpty = onEmpty;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        if (getCount() == 0 && onEmpty != null) {
            onEmpty.run();
        }
    }
}
