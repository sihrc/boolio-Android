package io.boolio.android.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.facebook.widget.LoginButton;

import io.boolio.android.R;
import io.boolio.android.auth.Auth;
import io.boolio.android.models.UserType;

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

        // Find Views and Buttons
//        EditText loginEmail = (EditText) rootView.findViewById(R.id.login_email);
//        EditText loginPassword = (EditText) rootView.findViewById(R.id.login_password);
//        Button loginButton = (Button) rootView.findViewById(R.id.login_button);

        // Facebook Login
        setupFacebookLogin(rootView);
        return rootView;
    }

    private void setupFacebookLogin(View rootView) {
        // Custom Facebook Login Button
        Button loginFacebook = (Button) rootView.findViewById(R.id.facebook_login);

        // Facebook packaged Login Button for Login Functionality
        final LoginButton fbButton = (LoginButton) rootView.findViewById(R.id.fb_login);
        fbButton.setReadPermissions("public_profile", "email", "user_friends");
        // Steal Facebook Login Drawable
        loginFacebook.setCompoundDrawablesWithIntrinsicBounds(
                fbButton.getCompoundDrawables()[0], null, null, null);

        // Link Functionality
        loginFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Auth.setAuth(UserType.FACEBOOK);
                fbButton.callOnClick();
            }
        });
    }
}
