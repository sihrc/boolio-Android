package io.boolio.android.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;

import org.json.JSONException;
import org.json.JSONObject;

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

    public QuestionAdapter(Context context, int resource) {
        super(context, resource);
        this.context = context;
        this.resource = resource;

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

        fillViews(holder, getItem(position));
        return view;
    }

    private void fillViews(QuestionHolder holder, Question question) {
        holder.question.setText(question.question);
        holder.leftAnswer.setText(question.left);
        holder.rightAnswer.setText(question.right);
        holder.creator.setText(question.creator.name);
        holder.date.setText(Utils.formatTimeDifferences(question.dateCreated) + " ago");

        holder.questionImage.setImageUrl(question.image, BoolioServer.getInstance(context).getImageLoader());
        holder.creatorImage.setImageUrl(question.creator.profilePic, BoolioServer.getInstance(context).getImageLoader());
    }

    private void setUpPostJSON(String direction, int position) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("questionId", getItem(position).questionId);
            jsonObject.put("answer", direction);
            jsonObject.put("id", BoolioUserHandler.getInstance(context).getUser().userId);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        // POST the answer
        BoolioServer.getInstance(context).postAnswer(jsonObject);
        remove(getItem(position));
        notifyDataSetChanged();

    }

    private class QuestionHolder {
        TextView question, leftAnswer, rightAnswer, creator, date;
        BoolioProfileImage creatorImage;
        NetworkImageView questionImage;
    }

}
