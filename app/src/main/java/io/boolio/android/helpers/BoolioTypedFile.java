package io.boolio.android.helpers;

import java.io.File;

import retrofit.mime.TypedFile;

/**
 * Created by Chris on 6/18/15.
 */
public class BoolioTypedFile extends TypedFile {
    String questionId;

    public BoolioTypedFile(String mimeType, String questionId, File file) {
        super(mimeType, file);
        this.questionId = questionId;
    }

    @Override
    public String fileName() {
        return questionId;
    }
}
