package io.boolio.android.models;

import java.util.List;

/**
 * Created by james on 4/16/15.
 */
public class Question {

    public String dateCreated, image, left, right, question, creatorImage, questionId;
    public User creator;
    public int leftCount, rightCount;
    public List<User> usersWhoLeft, usersWhoRight;
    public List<String> tags;

    public Question() {

    }
}
