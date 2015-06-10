package io.boolio.android.models;

import java.util.List;

/**
 * Created by james on 4/16/15.
 */
public class Question {
    public String creatorId, creatorPic, creatorName, dateCreated, image, left, question, _id, right;
    public int leftCount, rightCount;
    public List<String> usersWhoLeft, usersWhoRight, tags;
}
