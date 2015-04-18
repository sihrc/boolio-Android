package io.boolio.android.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.toolbox.NetworkImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

import io.boolio.android.MainActivity;
import io.boolio.android.R;
import io.boolio.android.custom.BoolioNetworkImageView;
import io.boolio.android.helpers.BoolioUserHandler;
import io.boolio.android.helpers.PictureHelper;
import io.boolio.android.helpers.Utils;
import io.boolio.android.network.BoolioServer;

/**
 * Created by james on 4/17/15.
 */
public class CreateQuestionFragment extends BoolioFragment {
    Context context;
    EditText questionText, left, right, tags;
    View progress;
    BoolioNetworkImageView networkImageView;
    PictureHelper helper;

    String imageSaved = "";
    String imageType = "";

    public static CreateQuestionFragment newInstance() {
        return new CreateQuestionFragment();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.context = activity;
        this.helper = new PictureHelper();
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
        JSONObject jsonObject = new JSONObject();
        if (questionText.getText().toString().length() == 0) {
            Toast.makeText(context, "Please enter a question", Toast.LENGTH_SHORT).show();
        } else {
            try {
                jsonObject.put("question", questionText.getText().toString());
                jsonObject.put("left", left.getText().toString().equals("") ? "No" : left.getText().toString());
                jsonObject.put("right", right.getText().toString().equals("") ? "Yes" : right.getText().toString());
                JSONArray array = new JSONArray(Arrays.asList(tags.getText().toString().split(", ")));
                jsonObject.put("tags", array);
                jsonObject.put("dateCreated", System.currentTimeMillis());
                jsonObject.put("creator", BoolioUserHandler.getInstance(context).getUser().userId);
                jsonObject.put("imageType", imageType);
                jsonObject.put("image", imageSaved);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            questionText.setText("");
            left.setText("");
            right.setText("");
            tags.setText("");

            // Upload Image to Server
            progress.setVisibility(View.VISIBLE);
            BoolioServer.getInstance(context).createQuestion(jsonObject, new Runnable() {
                @Override
                public void run() {
                    progress.setVisibility(View.GONE);
                    ((MainActivity) getActivity()).switchFragment(0);
                }
            });

        }
    }

    private void setupImageView() {
        final Dialog alert = new AlertDialog.Builder(context).setItems(
                new CharSequence[]{"Load from Gallery", "Take a picture", "Search", "Cancel"},
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0: // Load from Gallery
                                helper.dispatchChoosePictureIntent(getActivity(), CreateQuestionFragment.this);
                                imageType = "string";
                                break;
                            case 1: // Take a picture
                                helper.dispatchTakePictureIntent(getActivity(), CreateQuestionFragment.this);
                                imageType="string";
                                break;
                            case 2: // Search
                                imageType = "url";
                                Toast.makeText(context, "Sorry, this isn't implemented yet!", Toast.LENGTH_LONG).show();
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        helper.onActivityResult(this, requestCode, resultCode, data, networkImageView, new PictureHelper.BitmapCallback() {
            @Override
            public void onBitmap(Bitmap bitmap) {
                networkImageView.setLocalImageBitmap(bitmap);
                if (imageType.equals("string")) {
                    imageSaved = Utils.bitmapTo64String(bitmap);
                } else {
                    // TODO URL
                }
            }
        });

        super.onActivityResult(requestCode, resultCode, data);
    }
}
