package io.boolio.android.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import io.boolio.android.R;
import io.boolio.android.fragments.search.SearchImageFragment;
import io.boolio.android.helpers.BoolioCallback;
import io.boolio.android.helpers.BoolioUserHandler;
import io.boolio.android.helpers.PictureHelper;
import io.boolio.android.helpers.tracking.EventTracker;
import io.boolio.android.helpers.tracking.TrackEvent;
import io.boolio.android.models.Question;
import io.boolio.android.network.ServerQuestion;

/**
 * Created by james on 4/17/15.
 */
public class CreateQuestionFragment extends BoolioFragment {
    EditText left;
    EditText right;
    EditText questionText;

    View progress;
    ImageView networkImageView;
    PictureHelper helper;
    Runnable runnable;
    Bitmap imageSaved;
    String imageType = "";

    public static CreateQuestionFragment newInstance(Runnable runnable) {
        CreateQuestionFragment fragment = new CreateQuestionFragment();
        fragment.runnable = runnable;
        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        helper = new PictureHelper(activity);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        helper.onActivityResult(this.getParentFragment(), requestCode, resultCode, data, networkImageView, new PictureHelper.BitmapCallback() {
            @Override
            public void onBitmap(Bitmap bitmap) {
                networkImageView.setImageBitmap(bitmap);
                if (imageType.equals("string")) {
                    imageSaved = bitmap;
                } else {
                    // TODO URL
                }
            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(new Bundle());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_create_question, container, false);

        // Find Views and Buttons
        questionText = (EditText) rootView.findViewById(R.id.create_question_text);
        left = (EditText) rootView.findViewById(R.id.create_question_left_answer);
        right = (EditText) rootView.findViewById(R.id.create_question_right_answer);
        progress = rootView.findViewById(R.id.progress_bar_saving);

        networkImageView = (ImageView) rootView.findViewById(R.id.create_question_image);
        setupImageView();

        return rootView;
    }

    public void submitOnClickSetup() {
        boolean answersEmpty = left.getText().toString().replace(" ", "").isEmpty()
                || right.getText().toString().replace(" ", "").isEmpty();
        boolean questionEmpty = questionText.getText().length() == 0 && imageSaved == null;

        if (answersEmpty && questionEmpty) {
            Toast.makeText(activity, "Please enter a question, image, or answers", Toast.LENGTH_SHORT).show();
            return;
        }

        final Question question = new Question();
        question.question = questionText.getText().toString();
        question.left = left.getText().length() == 0 ? "No" : left.getText().toString();
        question.right = right.getText().length() == 0 ? "Yes" : right.getText().toString();
        question.creatorName = BoolioUserHandler.getInstance(activity).getUser().name;
        question.creatorImage = BoolioUserHandler.getInstance(activity).getUser().profilePic;
        question.creatorId = BoolioUserHandler.getInstance(activity).getUser().userId;


        // Upload Image to Server
        progress.setVisibility(View.VISIBLE);

        ServerQuestion.getInstance(activity).postQuestion(question, imageSaved, new Runnable() {
            @Override
            public void run() {
                EventTracker.getInstance(activity).trackQuestion(TrackEvent.CREATE_QUESTION, question, null);
                progress.setVisibility(View.GONE);
                reset();
                if (runnable != null)
                    runnable.run();
            }
        });
    }

    private void setupImageView() {
        final Dialog alert = new AlertDialog.Builder(activity).setItems(
                new CharSequence[]{"Load from Gallery", "Take a picture", "Search", "Cancel"},
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0: // Load from Gallery
                                imageType = "string";
                                EventTracker.getInstance(activity).track(TrackEvent.LOAD_PICTURE);
                                helper.dispatchChoosePictureIntent(activity, CreateQuestionFragment.this.getParentFragment());
                                break;
                            case 1: // Take a picture
                                imageType = "string";
                                EventTracker.getInstance(activity).track(TrackEvent.TAKE_PICTURE);
                                helper.dispatchTakePictureIntent(activity, CreateQuestionFragment.this.getParentFragment());
                                break;
                            case 2: // Search
                                imageType = "string";
                                EventTracker.getInstance(activity).track(TrackEvent.ATTEMPT_IMAGE_SEARCH);
                                SearchImageFragment.newInstance(new BoolioCallback<Uri>() {
                                    @Override
                                    public void handle(Uri object) {
                                        helper.dispatchCropPictureIntent(CreateQuestionFragment.this.getParentFragment(), networkImageView, object);
                                        EventTracker.getInstance(activity).track(TrackEvent.CHOSE_IMAGE_SEARCH);
                                    }
                                }).show(getChildFragmentManager(), "Search");
                                break;
                            case 3:
                            default:
                                break;
                        }
                        dialog.dismiss();
                    }
                }).setTitle(R.string.picture).create();

        networkImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alert.show();
            }
        });
    }

    private void reset() {
        networkImageView.setImageBitmap(null);
        imageSaved = null;
        questionText.setText("");
        left.setText("");
        right.setText("");
    }
}
