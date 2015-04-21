package io.boolio.android.models;

import java.util.List;

/**
 * Created by james on 4/16/15.
 */
public class Question {

    public String dateCreated, creatorName, image, left, right, question, creatorImage, questionId;
    public User creator;
    public int leftCount, rightCount;
    public List<String> usersWhoLeft, usersWhoRight, tags;

    public Question() {

    }
}
