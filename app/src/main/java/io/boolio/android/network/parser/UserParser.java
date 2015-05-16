package io.boolio.android.network.parser;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collection;

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
        try {
            User user = new User(getString("name"), getString("profilePic"));
            user.userId = getString("_id");
            user.name = getString("name");
            user.gcmId = getString("gcm");
            user.profilePic = getString("profilePic");
            user.questionsAsked = stringArray.toArray(getJSONArray("questionsAsked"));
            user.questionsAnswered = stringArray.toArray(getJSONArray("questionsAnswered"));
            user.questionsSkipped = stringArray.toArray(getJSONArray("questionsSkipped"));
            user.version = getInt("version");
            return user;
        } catch (JSONException e) {
            Log.e("UserParser", e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public JSONObject toJSON(User object) {
        JSONObject jUser = new JSONObject();
        try {
            put(jUser, "id", object.userId);
            put(jUser, "oauthId", object.oauthId);
            put(jUser, "name", object.name);
            put(jUser, "asked", object.questionsAsked);
            put(jUser, "gcm", object.gcmId);
            put(jUser, "answered", object.questionsAnswered);
            put(jUser, "skipped", object.questionsSkipped);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jUser;
    }

    @Override
    public JSONArray toListIDs(Collection<User> objects) {
        JSONArray array = new JSONArray();
        for (User user : objects) {
            array.put(user.userId);
        }
        return array;
    }
}
