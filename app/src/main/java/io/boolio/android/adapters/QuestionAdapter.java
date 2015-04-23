package io.boolio.android.adapters;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;

<<<<<<< Updated upstream
import org.json.JSONException;
import org.json.JSONObject;
=======
import java.util.Timer;
import java.util.TimerTask;
>>>>>>> Stashed changes

import io.boolio.android.R;
import io.boolio.android.helpers.BoolioUserHandler;
import io.boolio.android.helpers.Utils;
import io.boolio.android.models.Question;
import io.boolio.android.network.BoolioServer;
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
            holder.container = view;
            holder.question = (TextView) view.findViewById(R.id.question_text);
            holder.leftAnswer = (TextView) view.findViewById(R.id.question_left_answer);
            holder.rightAnswer = (TextView) view.findViewById(R.id.question_right_answer);
            holder.creator = (TextView) view.findViewById(R.id.question_creator);
            holder.date = (TextView) view.findViewById(R.id.question_date);

            //Image Views
            holder.creatorImage = (BoolioProfileImage) view.findViewById(R.id.question_creator_picture);
            holder.questionImage = (NetworkImageView) view.findViewById(R.id.question_image);

            holder.leftAnswer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setUpPostJSON("left", position);

                }
            });
            holder.rightAnswer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setUpPostJSON("right", position);
                }
            });

            view.setTag(holder);
        } else {
            holder = (QuestionHolder) view.getTag();
        }

<<<<<<< Updated upstream
        fillViews(holder, getItem(position));
=======
        Question question = getItem(position);

        fillViews(holder, question);

>>>>>>> Stashed changes
        return view;
    }

    private void fillViews(QuestionHolder holder, Question question) {
        holder.question.setText(question.question);
<<<<<<< Updated upstream
        holder.leftAnswer.setText(question.left);
        holder.rightAnswer.setText(question.right);
=======
        holder.leftAnswer.setCurrentText(question.left);
        holder.rightAnswer.setCurrentText(question.right);
        holder.leftAnswer.setEnabled(true);
        holder.rightAnswer .setEnabled(true);
>>>>>>> Stashed changes
        holder.creator.setText(question.creatorName);
        holder.date.setText(Utils.formatTimeDifferences(question.dateCreated) + " ago");

        holder.questionImage.setImageUrl(question.image, BoolioServer.getInstance(context).getImageLoader());
        holder.creatorImage.setImageUrl(question.creatorImage, BoolioServer.getInstance(context).getImageLoader());
    }

    private void setUpPostJSON(String direction, int position) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("questionId", getItem(position).questionId);
            jsonObject.put("answer", direction);
            jsonObject.put("id", BoolioUserHandler.getInstance(context).getUser().userId);

<<<<<<< Updated upstream
        } catch (JSONException e) {
            e.printStackTrace();
        }
        // POST the answer
        BoolioServer.getInstance(context).postAnswer(jsonObject);
        remove(getItem(position));
        notifyDataSetChanged();
=======
        final NetworkCallback<Question> questionNetworkCallback = new NetworkCallback<Question>() {
            @Override
            public void handle(final Question object) {
                holder.leftAnswer.setText(String.valueOf(object.leftCount));
                holder.rightAnswer.setText(String.valueOf(object.rightCount));
                holder.leftAnswer.setEnabled(false);
                holder.rightAnswer.setEnabled(false);
                Animation animation = AnimationUtils.loadAnimation(context, R.anim.slide_right_out);
                animation.setStartOffset(1000);
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
                holder.container.setAnimation(animation);
                holder.container.animate();
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
>>>>>>> Stashed changes

    }

    private class QuestionHolder {
<<<<<<< Updated upstream
        TextView question, leftAnswer, rightAnswer, creator, date;
=======
        View container;
        TextView question, creator, date;
        TextSwitcher leftAnswer, rightAnswer;
>>>>>>> Stashed changes
        BoolioProfileImage creatorImage;
        NetworkImageView questionImage;
    }

}
