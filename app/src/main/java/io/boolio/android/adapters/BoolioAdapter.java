package io.boolio.android.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextSwitcher;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import io.boolio.android.Boolio;
import io.boolio.android.R;
import io.boolio.android.animation.TextAnimation;
import io.boolio.android.custom.BoolioProfileImage;
import io.boolio.android.helpers.BoolioUserHandler;
import io.boolio.android.helpers.Dialogs;
import io.boolio.android.helpers.Utils;
import io.boolio.android.helpers.tracking.EventTracker;
import io.boolio.android.helpers.tracking.TrackEvent;
import io.boolio.android.models.Question;
import io.boolio.android.network.clients.BoolioQuestionClient;
import io.boolio.android.network.helpers.DefaultBoolioCallback;
import io.boolio.android.network.models.BoolioData;

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
            holder.questionImage = (ImageView) view.findViewById(R.id.question_image);

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
        if (question.creatorId != null && question.creatorId.equals(BoolioUserHandler.getInstance().getUserId())){
            holder.delete.setVisibility(View.VISIBLE);
            holder.report.setVisibility(View.GONE);
        } else {
            holder.delete.setVisibility(View.GONE);
            holder.report.setVisibility(View.VISIBLE);
        }

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

        boolean hasImage = !Utils.isNotHere(question.image);
        holder.questionImage.setVisibility(hasImage? View.VISIBLE : View.GONE);
        if (hasImage && (holder.questionImage.getTag() == null ||
                !holder.questionImage.getTag().equals(question.image))) {

            //we only load image if prev. URL and current URL do not match, or tag is null
            ImageAware imageAware = new ImageViewAware(holder.questionImage, false);
            ImageLoader.getInstance().displayImage(question.image, imageAware, Boolio.ImageOptions);
            holder.questionImage.setTag(question.image);
        }

        ImageLoader.getInstance().displayImage(question.creatorPic, holder.creatorImage);
    }

    public abstract void fillContent(QuestionHolder holder, Question question);

    public class QuestionHolder {
        View view, report, delete;
        TextView question, creator, date, highLeft, highRight;
        TextSwitcher leftAnswer, rightAnswer;
        BoolioProfileImage creatorImage;
        ImageView questionImage;
    }

    public void setOnDataSetChanged(Runnable onDataSetChanged) {
        this.onDataSetChanged = onDataSetChanged;
    }

    public void onDataSetChanged() {
        notifyDataSetChanged();
        if (onDataSetChanged != null)
            onDataSetChanged.run();
    }
}
