package io.boolio.android.network.models;

/**
 * Created by Chris on 6/17/15.
 */
public class Answer {
    public String questionId, direction;

    public Answer(String questionId, String direction) {
        this.questionId = questionId;
        this.direction = direction;
    }
}
