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

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.boolio.android.R;
import io.boolio.android.fragments.search.SearchImageFragment;
import io.boolio.android.helpers.BoolioUserHandler;
import io.boolio.android.helpers.PictureHelper;
import io.boolio.android.helpers.Utils;
import io.boolio.android.helpers.tracking.EventTracker;
import io.boolio.android.helpers.tracking.TrackEvent;
import io.boolio.android.models.Question;
import io.boolio.android.models.User;
import io.boolio.android.network.clients.BoolioQuestionClient;
import io.boolio.android.network.helpers.BoolioCallback;

/**
 * Created by james on 4/17/15.
 */
public class CreateQuestionFragment extends BoolioFragment {
    @Bind(R.id.create_question_left_answer) EditText left;
    @Bind(R.id.create_question_right_answer) EditText right;
    @Bind(R.id.create_question_text) EditText questionText;
    @Bind(R.id.progress_bar_saving) View progress;
    @Bind(R.id.create_question_image) ImageView networkImageView;

    Dialog imageAlertDialog;
    PictureHelper helper;
    Runnable runnable;
    Bitmap imageSaved;

    public static CreateQuestionFragment newInstance(Runnable runnable) {
        CreateQuestionFragment fragment = new CreateQuestionFragment();
        fragment.runnable = runnable;
        return fragment;
    }

    @Override
    public void onAttach(final Activity activity) {
        super.onAttach(activity);
        helper = new PictureHelper(activity);
        imageAlertDialog = new AlertDialog.Builder(activity).setItems(
            new CharSequence[]{"Load from Gallery", "Take a picture", "Search", "Cancel"},
            new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case 0: // Load from Gallery
                            EventTracker.getInstance(activity).track(TrackEvent.LOAD_PICTURE);
                            helper.dispatchChoosePictureIntent(activity, CreateQuestionFragment.this.getParentFragment());
                            break;
                        case 1: // Take a picture
                            EventTracker.getInstance(activity).track(TrackEvent.TAKE_PICTURE);
                            helper.dispatchTakePictureIntent(activity, CreateQuestionFragment.this.getParentFragment());
                            break;
                        case 2: // Search
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
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        helper.onActivityResult(this.getParentFragment(), requestCode, resultCode, data, networkImageView, new PictureHelper.BitmapCallback() {
            @Override
            public void onBitmap(Bitmap bitmap) {
                networkImageView.setImageBitmap(bitmap);
                imageSaved = bitmap;
            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(new Bundle());
    }

    @OnClick(R.id.create_question_image)
    public void onClick() {
        if (imageAlertDialog != null)
            imageAlertDialog.show();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_create_question, container, false);
        ButterKnife.bind(this, rootView);

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

        User user = BoolioUserHandler.getInstance().getUser();

        final Question question = new Question();
        question.question = questionText.getText().toString();
        question.left = left.getText().length() == 0 ? "No" : left.getText().toString();
        question.right = right.getText().length() == 0 ? "Yes" : right.getText().toString();
        question.creatorName = user.name;
        question.creatorPic = user.profilePic;
        question.creatorId = user._id;


        // Upload Image to Server
        progress.setVisibility(View.VISIBLE);
        BoolioQuestionClient.api().postQuestion(question, new BoolioCallback<Question>() {
            @Override
            public void handle(Question obj) {
                if (imageSaved != null)
                    BoolioQuestionClient.api().uploadImage(Utils.getTypedFile(activity, obj._id, imageSaved), new BoolioCallback<Question>() {
                        @Override
                        public void handle(Question resObj) {
                            EventTracker.getInstance(activity).trackQuestion(TrackEvent.CREATE_QUESTION, question, null);
                            progress.setVisibility(View.GONE);
                            reset();
                            if (runnable != null)
                                runnable.run();
                        }
                    });
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
