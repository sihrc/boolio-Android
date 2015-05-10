package io.boolio.android.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

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

        Button loginButton = (Button) rootView.findViewById(R.id.login_button);

        final LoginButton fbButton = (LoginButton) rootView.findViewById(R.id.fb_login);
        fbButton.invalidate();
        loginButton.setCompoundDrawablesWithIntrinsicBounds(
                fbButton.getCompoundDrawables()[0], null, null, null);
        loginButton.invalidate();

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fbButton.performClick();
            }
        });
        return rootView;
    }
}
