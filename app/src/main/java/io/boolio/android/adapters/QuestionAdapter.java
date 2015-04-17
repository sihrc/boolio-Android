package io.boolio.android.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import io.boolio.android.R;
import io.boolio.android.models.Question;

/**
 * Created by james on 4/17/15.
 */
public class QuestionAdapter extends ArrayAdapter<Question> {
    List<Question> questions = new ArrayList<Question>();
    int resource;
    Context context;

    public QuestionAdapter(Context context, int resource) {
        super(context, resource);
        this.context = context;
        this.resource = resource;

    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        QuestionHolder holder;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(resource, parent, false);
        holder = new QuestionHolder();

        //TextViews
        holder.question = (TextView) view.findViewById(R.id.question);
        holder.leftAnswer = (TextView) view.findViewById(R.id.question_left_answer);
        holder.rightAnswer = (TextView) view.findViewById(R.id.question_right_answer);
        holder.creator = (TextView) view.findViewById(R.id.question_creator);

        //Image Views
        holder.creatorImage = (ImageView) view.findViewById(R.id.question_creator_picture);
        holder.questionImage = (ImageView) view.findViewById(R.id.question_image);

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

        holder.questionImage.setBackground(context.getResources().getDrawable(R.drawable.logo));

    }

    public void addQuestion(Question question) {
        this.questions.add(question);
        notifyDataSetChanged();
    }

    private class QuestionHolder {
        TextView question, leftAnswer, rightAnswer, creator;
        ImageView creatorImage, questionImage;
    }

}
