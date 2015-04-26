package io.boolio.android.network.parser;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import io.boolio.android.models.User;

/**
 * Created by Chris on 4/16/15.
 */
public class UserParser extends Parser<User> {
    static UserParser instance;
    static JSONArrayParser<String> stringArray = new JSONArrayParser<>();

    public static UserParser getInstance() {
        if (instance == null)
            instance = new UserParser();
        return instance;
    }

    @Override
    public User parse() {
        User user = new User();
        try {
            user.userId = getString("_id");
            user.name = getString("name");
            user.profilePic = getString("profilePic");
            user.questionsAsked = stringArray.toArray(getJSONArray("questionsAsked"));
            user.questionsAnswered = stringArray.toArray(getJSONArray("questionsAnswered"));
            user.questionsSkipped = stringArray.toArray(getJSONArray("questionsSkipped"));
        } catch (JSONException e) {
            Log.e("UserParser", e.getMessage());
            e.printStackTrace();
        }

        return user;
    }

    @Override
    public JSONObject toJSON(User object) {
        return null;
    }
}
