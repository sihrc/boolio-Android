package io.boolio.android.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;

import java.util.ArrayList;
import java.util.List;

import io.boolio.android.R;
import io.boolio.android.models.Question;
import io.boolio.android.network.BoolioServer;
import io.boolio.android.views.BoolioProfileImage;

/**
 * Created by james on 4/17/15.
 */
public class QuestionAdapter extends ArrayAdapter<Question> {
    List<Question> questions = new ArrayList<Question>();
    int resource;
    Context context;

    public QuestionAdapter(Context context, int resource, List<Question> objects) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
        this.questions = objects;

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
            holder.leftAnswer = (TextView) view.findViewById(R.id.question_left_answer);
            holder.rightAnswer = (TextView) view.findViewById(R.id.question_right_answer);
            holder.creator = (TextView) view.findViewById(R.id.question_creator);
            holder.date = (TextView) view.findViewById(R.id.question_date);
            //Image Views
            holder.creatorImage = (BoolioProfileImage) view.findViewById(R.id.question_creator_picture);
            holder.questionImage = (NetworkImageView) view.findViewById(R.id.question_image);

            view.setTag(holder);
        } else {
            holder = (QuestionHolder) view.getTag();
        }

        fillViews(holder, questions.get(position));
        return view;
    }

    @Override
    public int getCount() {
        return this.questions.size();
    }

    @Override
    public Question getItem(int position) {
        return this.questions.get(position);
    }

    private void fillViews(QuestionHolder holder, Question question) {
        holder.question.setText(question.question);
        holder.leftAnswer.setText(question.left);
        holder.rightAnswer.setText(question.right);
        holder.creator.setText(question.creator);
        holder.date.setText(question.dateCreated);

        holder.questionImage.setImageUrl(
                "http://upload.wikimedia.org/wikipedia/en/archive/9/9b/20041203215243!Olin_College_Great_Lawn.jpg"
                , BoolioServer.getInstance(context).getImageLoader());
        holder.creatorImage.setImageUrl(question.creatorImage, BoolioServer.getInstance(context).getImageLoader());

    }

    public void addQuestion(Question question) {
        this.questions.add(question);
        notifyDataSetChanged();
    }

    private class QuestionHolder {
        TextView question, leftAnswer, rightAnswer, creator, date;
        BoolioProfileImage creatorImage;
        NetworkImageView questionImage;
    }

}
