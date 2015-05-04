package io.boolio.android.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import io.boolio.android.R;
import io.boolio.android.custom.BoolioNetworkImageView;
import io.boolio.android.helpers.BoolioUserHandler;
import io.boolio.android.helpers.PictureHelper;
import io.boolio.android.helpers.Utils;
import io.boolio.android.models.Question;
import io.boolio.android.network.ServerQuestion;
import io.boolio.android.network.ServerUser;

/**
 * Created by james on 4/17/15.
 */
public class CreateQuestionFragment extends BoolioFragment {
    EditText left;
    EditText right;
    EditText tags;
    View progress;
    BoolioNetworkImageView networkImageView;
    PictureHelper helper;
    Runnable runnable;
    Bitmap imageSaved;
    String imageType = "";
    private EditText questionText;

    public static CreateQuestionFragment newInstance(Runnable runnable) {
        CreateQuestionFragment fragment = new CreateQuestionFragment();
        fragment.runnable = runnable;
        fragment.helper = new PictureHelper();
        return fragment;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        helper.onActivityResult(this.getParentFragment(), requestCode, resultCode, data, networkImageView, new PictureHelper.BitmapCallback() {
            @Override
            public void onBitmap(Bitmap bitmap) {
                networkImageView.setLocalImageBitmap(bitmap);
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
        tags = (EditText) rootView.findViewById(R.id.create_question_tag);
        progress = rootView.findViewById(R.id.progress_bar_saving);

        rootView.findViewById(R.id.create_question_submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitOnClickSetup();
            }
        });

        networkImageView = (BoolioNetworkImageView) rootView.findViewById(R.id.create_question_image);
        setupImageView();

        return rootView;
    }

    private void submitOnClickSetup() {
        if (questionText.getText().length() == 0 && imageSaved == null) {
            Toast.makeText(activity, "Please enter a question or choose an image", Toast.LENGTH_SHORT).show();
            return;
        }

        Question question = new Question();
        question.question = questionText.getText().toString();
        question.left = left.getText().length() == 0 ? "No" : left.getText().toString();
        question.right = right.getText().length() == 0 ? "Yes" : right.getText().toString();
        question.creatorName = BoolioUserHandler.getInstance(activity).getUser().name;
        question.creatorImage = BoolioUserHandler.getInstance(activity).getUser().profilePic;
        question.creatorId = BoolioUserHandler.getInstance(activity).getUser().userId;
        question.tags = Utils.parseStringArray(tags.getText().toString());


        // Upload Image to Server
        progress.setVisibility(View.VISIBLE);

        ServerQuestion.getInstance(activity).postQuestion(question, imageSaved, new Runnable() {
            @Override
            public void run() {
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
                                helper.dispatchChoosePictureIntent(activity, CreateQuestionFragment.this.getParentFragment());
                                break;
                            case 1: // Take a picture
                                imageType = "string";
                                helper.dispatchTakePictureIntent(activity, CreateQuestionFragment.this.getParentFragment());
                                break;
                            case 2: // Search
                                imageType = "url";
                                Toast.makeText(activity, "Sorry, this isn't implemented yet!", Toast.LENGTH_LONG).show();
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
        networkImageView.setLocalImageBitmap(null);
        imageSaved = null;
        questionText.setText("");
        left.setText("");
        right.setText("");
        tags.setText("");
    }
}
