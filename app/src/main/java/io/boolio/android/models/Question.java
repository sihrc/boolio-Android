package io.boolio.android.models;

import java.util.List;

/**
 * Created by james on 4/16/15.
 */
public class Question {

    public String creatorId, creatorImage, creatorName, dateCreated, image, left, question, questionId, right;
    public int leftCount, rightCount;
    public List<String> usersWhoLeft, usersWhoRight, tags;

    public Question() {

    }
}
