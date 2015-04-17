package io.boolio.android.models;

import java.util.List;

/**
 * Created by james on 4/16/15.
 */
public class Question {

    public String image, left, right, question, creator;
    public int leftCount, rightCount, dateCreated;
    public List<User> usersWhoLeft, usersWhoRight;
    public List<String> tags;

    public Question() {

    }
}
