package io.boolio.android.network.parser;

import android.util.Log;

import org.json.JSONException;

import io.boolio.android.models.Question;
import io.boolio.android.models.User;

/**
 * Created by james on 4/17/15.
 */
public class QuestionParser extends Parser<Question> {
    static QuestionParser instance;
    static JSONArrayParser<User> userArray = new JSONArrayParser<>();
    static JSONArrayParser<String> stringArray = new JSONArrayParser<>();

    public static QuestionParser getInstance() {
        if (instance == null)
            instance = new QuestionParser();
        return instance;
    }

    @Override
    public Question parse() {
        Question question = new Question();
        try {
            question.creator = UserParser.getInstance().parse(getJSONObject("creator"));
            question.question = getString("question");
            question.image = getString("image") == null ? "" : getString("image");
            question.left = getString("left");
            question.right = getString("right");
            question.dateCreated = getString("dateCreated");
            question.leftCount = getInt("leftCount");
            question.rightCount = getInt("rightCount");
            question.usersWhoLeft = stringArray.toArray(getJSONArray("usersWhoLeft"));
            question.usersWhoRight = stringArray.toArray(getJSONArray("usersWhoRight"));
            question.tags = stringArray.toArray(getJSONArray("tags"));

        } catch (JSONException e) {
            Log.e("UserParser", e.getMessage());
            e.printStackTrace();
        }

        return question;
    }
}
