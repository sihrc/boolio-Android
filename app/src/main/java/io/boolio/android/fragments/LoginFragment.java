package io.boolio.android.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.login.LoginManager;
import com.facebook.login.widget.LoginButton;

import java.util.Arrays;

import io.boolio.android.R;

/**
 * Created by Chris on 4/16/15.
 */
public class LoginFragment extends BoolioFragment {
    Context context;

    public static LoginFragment newInstance() {
        return new LoginFragment();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        context = activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_login, container, false);

        // Facebook Login
        setupFacebookLogin(rootView);
        return rootView;
    }

    private void setupFacebookLogin(View rootView) {
        // Facebook packaged Login Button for Login Functionality
        final LoginButton fbButton = (LoginButton) rootView.findViewById(R.id.login_button);
        fbButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginManager.getInstance().logInWithReadPermissions(LoginFragment.this, Arrays.asList("public_profile", "user_friends"));
            }
        });
    }
}
