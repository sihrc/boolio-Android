package io.boolio.android.network.services;

import java.util.List;

import io.boolio.android.helpers.BoolioTypedFile;
import io.boolio.android.models.Question;
import io.boolio.android.network.BoolioData;
import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.Part;

/**
 * Created by Chris on 6/9/15.
 */
public interface BoolioQuestionService {
    // Questions
    @POST("/")
    void getQuestionFeed(@Body BoolioData data, Callback<List<Question>> callback);

    @POST("/search")
    void searchQuestions(@Body BoolioData query, Callback<List<Question>> callback);

    @POST("/ids")
    void getQuestions(@Body BoolioData data, Callback<List<Question>> callback);

    @POST("/create")
    void postQuestion(@Body Question question, Callback<Question> callback);

    @POST("/skip")
    void skipQuestion(@Body BoolioData question, Callback<?> callback);

    @POST("/unskip")
    void unskipQuestion(@Body Question question, Callback<?> callback);

    @Multipart
    @POST("/image")
    void uploadImage(@Part("part") BoolioTypedFile image, Callback<Question> callback);

    @POST("/answer")
    void postAnswer(@Body BoolioData data, Callback<Question> callback);

    @POST("/report")
    void reportQuestion(@Body BoolioData questionId, Callback<?> callback);

    @POST("/delete")
    void deleteQuestion(@Body BoolioData questionId, Callback<?> callback);
}
