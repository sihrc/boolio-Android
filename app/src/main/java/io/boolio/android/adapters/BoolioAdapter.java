package io.boolio.android.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextSwitcher;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.boolio.android.R;
import io.boolio.android.animation.TextAnimation;
import io.boolio.android.custom.BoolioProfileImage;
import io.boolio.android.helpers.BoolioUserHandler;
import io.boolio.android.helpers.Dialogs;
import io.boolio.android.helpers.Glider;
import io.boolio.android.helpers.Utils;
import io.boolio.android.helpers.tracking.EventTracker;
import io.boolio.android.helpers.tracking.TrackEvent;
import io.boolio.android.models.Question;
import io.boolio.android.network.BoolioData;
import io.boolio.android.network.clients.BoolioQuestionClient;
import io.boolio.android.network.clients.BoolioUserClient;
import io.boolio.android.network.helpers.DefaultBoolioCallback;

/**
 * Created by james on 4/24/15.
 */
public abstract class BoolioAdapter extends ArrayAdapter<Question> {
    Context context;
    int resource;

    Runnable onDataSetChanged;

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
            view = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(resource, parent, false);
            holder = new QuestionHolder(view);

            TextAnimation.getInstance(context).FadeTextSwitcher(holder.leftAnswer, R.layout.text_answer_left);
            TextAnimation.getInstance(context).FadeTextSwitcher(holder.rightAnswer, R.layout.text_answer_right);

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
        if (question.creatorId != null && question.creatorId.equals(BoolioUserHandler.getInstance().getUserId())) {
            holder.delete.setVisibility(View.VISIBLE);
            holder.report.setVisibility(View.GONE);
        } else {
            holder.delete.setVisibility(View.GONE);
            holder.report.setVisibility(View.VISIBLE);
        }

        // Hide Question TextView if no question provided
        if (question.question == null || question.question.replace(" ", "").isEmpty())
            holder.question.setVisibility(View.GONE);
        else
            holder.question.setVisibility(View.VISIBLE);

        // Question Image
        if (Utils.exists(question.image)) {
            holder.questionImage.setVisibility(View.VISIBLE);
            Glider.image(holder.questionImage, question.image);
        } else {
            holder.questionImage.setVisibility(View.GONE);
        }

        // Deleting and Reporting click Listeners
        holder.report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventTracker.getInstance(context).trackQuestion(TrackEvent.DELETE_QUESTION, question, null);
                Dialogs.messageDialog(context, R.string.report_title, R.string.report_message, new Runnable() {
                    @Override
                    public void run() {
                        BoolioQuestionClient.api().reportQuestion(BoolioData.keys("questionId").values(question._id), new DefaultBoolioCallback());

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
                        BoolioQuestionClient.api().deleteQuestion(BoolioData.keys("questionId").values(question._id), new DefaultBoolioCallback());
                        remove(question);
                        notifyDataSetChanged();
                    }
                });
            }
        });

        fillContent(holder, question);

        Glider.image(holder.creatorImage, question.creatorPic);
    }

    public abstract void fillContent(QuestionHolder holder, Question question);

    public void setOnDataSetChanged(Runnable onDataSetChanged) {
        this.onDataSetChanged = onDataSetChanged;
    }

    public void onDataSetChanged() {
        notifyDataSetChanged();
        if (onDataSetChanged != null)
            onDataSetChanged.run();
    }

    public class QuestionHolder {
        public View view;
        @Bind(R.id.question_text) TextView question;
        @Bind(R.id.question_left_answer) TextSwitcher leftAnswer;
        @Bind(R.id.question_right_answer) TextSwitcher rightAnswer;
        @Bind(R.id.highlighted_left) TextView highLeft;
        @Bind(R.id.highlighted_right) TextView highRight;
        @Bind(R.id.question_creator) TextView creator;
        @Bind(R.id.question_date) TextView date;
        @Bind(R.id.report_button) View report;
        @Bind(R.id.delete_button) View delete;
        @Bind(R.id.question_creator_picture) BoolioProfileImage creatorImage;
        @Bind(R.id.question_image) ImageView questionImage;


        public QuestionHolder(View view) {
            this.view = view;
            ButterKnife.bind(this, view);
        }
    }
}
