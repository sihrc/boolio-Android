package io.boolio.android.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import io.boolio.android.R;
import io.boolio.android.callbacks.UserCallback;
import io.boolio.android.helpers.BoolioUserHandler;
import io.boolio.android.models.User;
import io.boolio.android.network.BoolioServer;
import io.boolio.android.views.BoolioProfileImage;

/**
 * Created by Chris on 4/17/15.
 */
public class ProfileFragment extends BoolioFragment {
    Context context;
    String userId;
    User user;


    ImageView profileSetting;
    BoolioProfileImage profileUserImage;
    TextView askedCount;
    TextView profileUsername;
    TextView answeredCount;
    RelativeLayout headerBar;
    TextView profileDisplayName;
    TextView karmaCount;


    public static ProfileFragment newInstance(String userId) {
        ProfileFragment fragment = new ProfileFragment();
        fragment.userId = userId;

        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        context = activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        BoolioServer.getInstance(context).getUserProfile(
                userId == null ? BoolioUserHandler.getInstance(context).getUser().userId : userId,
                new UserCallback() {
                    @Override
                    public void handleUser(User user) {
                        ProfileFragment.this.user = user;
                        updateViews();
                    }
                });

        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);

        profileSetting = (ImageView) rootView.findViewById(R.id.profile_setting);
        profileUserImage = (BoolioProfileImage) rootView.findViewById(R.id.profile_user_image);
        askedCount = (TextView) rootView.findViewById(R.id.asked_count);
        profileUsername = (TextView) rootView.findViewById(R.id.profile_username);
        answeredCount = (TextView) rootView.findViewById(R.id.answered_count);
        headerBar = (RelativeLayout) rootView.findViewById(R.id.header_bar);
        profileDisplayName = (TextView) rootView.findViewById(R.id.profile_user_name);
        karmaCount = (TextView) rootView.findViewById(R.id.karma_count);

        setupViews();

        return rootView;
    }

    private void setupViews() {
        // Settings Button
        profileSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(context)
                        .setTitle(R.string.settings)
                        .setItems(new CharSequence[]{"Logout", "Cancel"}, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case 0:
                                        Toast.makeText(context, "Logged Out", Toast.LENGTH_SHORT).show();
                                        BoolioUserHandler.getInstance(context).logout();
                                        dialog.dismiss();
                                        break;
                                    case 1:
                                        dialog.dismiss();
                                        break;
                                }
                            }
                        }).show();
            }
        });
    }

    private void updateViews() {
        if (user == null)
            return;
        if (user.userId.equals(BoolioUserHandler.getInstance(context).getUser().userId)) {
            profileSetting.setVisibility(View.VISIBLE);
            profileDisplayName.setText(R.string.my_profile_page);
        } else {
            profileSetting.setVisibility(View.GONE);
            profileDisplayName.setText(R.string.profile_page);
        }


        profileUserImage.setImageUrl(user.profilePic, BoolioServer.getInstance(context).getImageLoader());
        askedCount.setText(String.valueOf(user.questionsAsked.size()));
        answeredCount.setText(String.valueOf(user.questionsAnswered.size()));
        karmaCount.setText(String.valueOf(user.questionsAnswered.size() + user.questionsAsked.size())); //FIXME IMPLEMENT ON BACKEND
        profileUsername.setText(user.name); // FIXME ADD USERNAME
    }
}
