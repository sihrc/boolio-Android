package io.boolio.android.network.parser;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collection;

import io.boolio.android.models.Question;

/**
 * Created by james on 4/17/15.
 */
public class QuestionParser extends Parser<Question> {
    static JSONArrayParser<String> stringArray = new JSONArrayParser<>();

    public static QuestionParser getInstance() {
        return new QuestionParser();
    }

    @Override
    public Question parse() {
        Question question = new Question();
        try {
            question.creatorName = getString("creatorName");
            question.creatorImage = getString("creatorPic");
            question.creatorId = getString("creatorId");
            question.question = getString("question");
            question.questionId = getString("_id");
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

    @Override
    public JSONObject toJSON(Question object) {
        JSONObject jsonObject = new JSONObject();
        try {
            put(jsonObject, "question", object.question);
            put(jsonObject, "left", object.left);
            put(jsonObject, "right", object.right);
            put(jsonObject, "creatorName", object.creatorName);
            put(jsonObject, "creatorPic", object.creatorImage);
            put(jsonObject, "creatorId", object.creatorId);
            put(jsonObject, "tags", new JSONArray(object.tags));
            put(jsonObject, "dateCreated", System.currentTimeMillis());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    @Override
    public JSONArray toListIDs(Collection<Question> objects) {
        JSONArray array = new JSONArray();
        for (Question question : objects) {
            array.put(question.questionId);
        }
        return array;
    }
}
