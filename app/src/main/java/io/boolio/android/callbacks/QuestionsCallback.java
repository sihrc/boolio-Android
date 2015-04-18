package io.boolio.android.callbacks;

import java.util.List;

import io.boolio.android.models.Question;

/**
 * Created by Chris on 4/18/15.
 */
public interface QuestionsCallback {
    public void handleQuestions(List<Question> questionList);
}
